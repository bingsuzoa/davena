package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static final String HAS_ANY_MEMBER_OF_TEAM = "팀에 멤버가 배정된 경우에는 팀을 삭제할 수 없어요. 멤버를 다른 팀으로 우선 옮겨주세요.";
    public static final String CAN_NOT_REMOVE_DEFAULT_TEAM = "기본 팀은 삭제가 불가능합니다.";


    protected static Team createDefaultTeam(String name, UUID wardId) {
        return new Team(UUID.randomUUID(), wardId, name, true);
    }

    protected static Team createTeam(String name, UUID wardId) {
        return new Team(UUID.randomUUID(), wardId, name, false);
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
        if (!isDefault) {
            throw new IllegalArgumentException(CAN_NOT_REMOVE_DEFAULT_TEAM);
        }
        return true;
    }
}
