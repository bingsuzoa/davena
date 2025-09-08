package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Shift {

    public Shift(
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
    private UUID wardId;
    private String name;
    private boolean isDefault;

    protected static Shift createDefaultOff(String name, UUID wardId) {
        return new Shift(UUID.randomUUID(), wardId, name, true);
    }

    protected static Shift createShift(String name, UUID wardId) {
        return new Shift(UUID.randomUUID(), wardId, name, false);
    }
}
