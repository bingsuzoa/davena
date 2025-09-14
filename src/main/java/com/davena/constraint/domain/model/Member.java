package com.davena.constraint.domain.model;

import com.davena.organization.domain.model.ward.DayType;
import com.davena.organization.domain.model.ward.Shift;
import lombok.Getter;

import java.util.*;

@Getter
public class Member {

    public Member(
            UUID userId,
            UUID wardId,
            String name
    ) {
        this.userId = userId;
        this.wardId = wardId;
        this.name = name;
    }

    private UUID userId;
    private UUID wardId;
    private String name;
    private UUID teamId;
    private UUID gradeId;

    private boolean canCharge = false;
    private int rank = LOWEST_RANK;
    private Map<DayType, List<PossibleShift>> possibleShifts = new HashMap<>();

    public static final String NOT_EXIST_SHIFT = "존재하지 않는 근무 유형입니다.";
    private static final int LOWEST_RANK = 100;

    public void updateGrade(UUID gradeId) {
        this.gradeId = gradeId;
    }

    public void updateTeam(UUID teamId) {
        this.teamId = teamId;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCanCharge(boolean canCharge) {
        this.canCharge = canCharge;
    }

    public void updateRank(int rank) {
        this.rank = rank;
    }

    public void addWardNewShift(DayType dayType, UUID shiftId, String shiftName) {
        possibleShifts.get(dayType).add(new PossibleShift(shiftId, shiftName));
    }

    public void updateShiftName(DayType dayType, UUID shiftId, String newName) {
        PossibleShift possibleShift = possibleShifts.get(dayType).stream()
                .filter(shift -> shift.getShiftId().equals(shiftId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SHIFT));
        possibleShift.updateName(newName);
    }

    public void deleteWardShift(DayType dayType, UUID shiftId) {
        possibleShifts.get(dayType).remove(shiftId);
    }

    public void initPossibleShifts(Map<DayType, List<Shift>> shiftsOfWard) {
        for (DayType dayType : shiftsOfWard.keySet()) {
            possibleShifts.put(dayType, new ArrayList<>());
            for (Shift shift : shiftsOfWard.get(dayType)) {
                possibleShifts.get(dayType).add(new PossibleShift(shift.getId(), shift.getName()));
            }
        }
    }

    public void updatePossibleShift(DayType dayType, UUID shiftId, boolean isPossible) {
        List<PossibleShift> shifts = possibleShifts.get(dayType);
        PossibleShift shift = shifts.stream()
                .filter(s -> s.getShiftId().equals(shiftId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SHIFT));

        shift.updatePossibleShift(isPossible);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return userId.equals(member.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }


}
