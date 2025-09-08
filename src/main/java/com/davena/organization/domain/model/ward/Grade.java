package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Grade {

    public Grade(
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

    private List<UUID> users = new ArrayList<>();

    public static final String HAS_ANY_MEMBER_OF_GRADE = "숙련도에 멤버가 배정된 경우에는 숙련도 삭제할 수 없어요. 멤버를 옮겨주세요.";
    public static final String CAN_NOT_REMOVE_DEFAULT_GRADE = "기본 숙련도는 삭제가 불가능합니다.";

    private UUID id;
    private UUID wardId;
    private String name;
    private boolean isDefault;

    protected static Grade createDefaultGrade(String name, UUID wardId) {
        return new Grade(UUID.randomUUID(), wardId, name, true);
    }

    protected static Grade createGrade(String name, UUID wardId) {
        return new Grade(UUID.randomUUID(), wardId, name, false);
    }

    protected boolean validateRemovableGrade() {
        if (!users.isEmpty()) {
            throw new IllegalArgumentException(HAS_ANY_MEMBER_OF_GRADE);
        }
        if (!isDefault) {
            throw new IllegalArgumentException(CAN_NOT_REMOVE_DEFAULT_GRADE);
        }
        return true;
    }

    protected void clearUsers() {
        users.clear();
    }

    protected void updateUsers(List<UUID> newUsers) {
        users.addAll(newUsers);
    }
}
