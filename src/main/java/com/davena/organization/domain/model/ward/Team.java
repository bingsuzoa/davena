package com.davena.organization.domain.model.ward;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Team {

    private Team(
            UUID id,
            UUID wardId,
            String name,
            boolean isDefault
    ) {
        this.id = id;
        this.wardId = wardId;
        this.name = name;
        this.isDefault = isDefault;
    }

    private UUID id;
    private String name;
    private UUID wardId;
    private boolean isDefault;

    private List<UUID> users = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private Map<DayType, Map<UUID, ShiftRequirement>> requirements = new HashMap<>();

    public static final String HAS_ANY_MEMBER_OF_TEAM = "팀에 멤버가 배정된 경우에는 팀을 삭제할 수 없어요. 멤버를 다른 팀으로 우선 옮겨주세요.";
    public static final String CAN_NOT_REMOVE_DEFAULT_TEAM = "기본 팀은 삭제가 불가능합니다.";


    protected static Team createDefaultTeam(String name, UUID wardId, Map<DayType, List<Shift>> wardShifts) {
        Team team = new Team(UUID.randomUUID(), wardId, name, true);
        team.initializeRequirements(wardShifts);
        return team;
    }

    protected static Team createTeam(String name, UUID wardId, Map<DayType, List<Shift>> wardShifts) {
        Team team = new Team(UUID.randomUUID(), wardId, name, false);
        team.initializeRequirements(wardShifts);
        return team;
    }

    private void initializeRequirements(Map<DayType, List<Shift>> wardShifts) {
        int initRequirementNumber = 0;
        for (Map.Entry<DayType, List<Shift>> entry : wardShifts.entrySet()) {
            DayType dayType = entry.getKey();
            Map<UUID, ShiftRequirement> shiftReqMap = new HashMap<>();
            for (Shift shift : entry.getValue()) {
                shiftReqMap.put(
                        shift.getId(),
                        ShiftRequirement.of(shift.getId(), shift.getName(), initRequirementNumber) // shiftId, 초기값
                );
            }
            requirements.put(dayType, shiftReqMap);
        }
    }

    protected UUID addNewUser(UUID userId) {
        users.add(userId);
        return userId;
    }

    protected void updateUsers(List<UUID> newUsers) {
        users.addAll(newUsers);
    }

    protected void clearUsers() {
        users.clear();
    }

    protected boolean validateRemovableTeam() {
        if (!users.isEmpty()) {
            throw new IllegalArgumentException(HAS_ANY_MEMBER_OF_TEAM);
        }
        if (isDefault) {
            throw new IllegalArgumentException(CAN_NOT_REMOVE_DEFAULT_TEAM);
        }
        return true;
    }

    protected Map<DayType, Map<UUID, ShiftRequirement>> getRequirements() {
        return Collections.unmodifiableMap(
                requirements.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> Collections.unmodifiableMap(e.getValue())
                        ))
        );
    }

    protected void updateRequirement(DayType dayType, UUID shiftId, int updatedRequirement) {
        Map<UUID, ShiftRequirement> shiftRequirements = requirements.get(dayType);
        shiftRequirements.get(shiftId).updateRequiredCount(updatedRequirement);
    }
}
