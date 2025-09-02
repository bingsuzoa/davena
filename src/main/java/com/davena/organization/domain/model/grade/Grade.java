package com.davena.organization.domain.model.grade;

import com.davena.organization.domain.model.ward.WardId;
import lombok.Getter;

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
}
