package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Grade {

    public Grade(
            GradeId id,
            WardId wardId,
            String name,
            boolean isDefault
    ) {
        this.id = id;
        this.wardId = wardId;
        this.name = name;
        this.isDefault = isDefault;
    }

    private GradeId id;
    private WardId wardId;
    private String name;
    private boolean isDefault;

    protected static Grade createDefaultGrade(String name, WardId wardId) {
        return new Grade(new GradeId(UUID.randomUUID()), wardId, name, true);
    }

    protected static Grade createGrade(String name, WardId wardId) {
        return new Grade(new GradeId(UUID.randomUUID()), wardId, name, false);
    }
}
