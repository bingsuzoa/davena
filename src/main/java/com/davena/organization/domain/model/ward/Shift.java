package com.davena.organization.domain.model.shift;

import com.davena.organization.domain.model.ward.WardId;
import lombok.Getter;

@Getter
public class Shift {

    public Shift(
            ShiftId id,
            WardId wardId,
            String name,
            boolean isDefault
    ) {
        this.id = id;
        this.wardId = wardId;
        this.name = name;
        this.isDefault = isDefault;
    }

    private ShiftId id;
    private WardId wardId;
    private String name;
    private boolean isDefault;
}
