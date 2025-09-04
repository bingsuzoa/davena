package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.UUID;

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

    protected static Shift createDefaultOff(String name, WardId wardId) {
        return new Shift(new ShiftId(UUID.randomUUID()), wardId, name, true);
    }

    protected static Shift createShift(String name, WardId wardId) {
        return new Shift(new ShiftId(UUID.randomUUID()), wardId, name, false);
    }
}
