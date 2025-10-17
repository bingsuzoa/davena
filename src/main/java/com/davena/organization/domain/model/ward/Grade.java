package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Grade {

    protected Grade(
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
    public static final String DEFAULT_GRADE = "1단계";
    public static final String CAN_NOT_REMOVE_DEFAULT_GRADE = "기본 숙련도는 삭제가 불가능합니다.";

    private UUID id;
    private UUID wardId;
    private String name;
    private boolean isDefault;

    protected void validateDefaultGrade() {
        if (isDefault) {
            throw new IllegalArgumentException(CAN_NOT_REMOVE_DEFAULT_GRADE);
        }
    }
}