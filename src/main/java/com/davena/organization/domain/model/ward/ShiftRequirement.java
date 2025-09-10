package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ShiftRequirement {

    private ShiftRequirement(
            UUID id,
            UUID shiftId,
            String shiftName,
            int requiredCount
    ) {
        this.id = id;
        this.shiftId = shiftId;
        this.shiftName = shiftName;
        this.requiredCount = requiredCount;
    }

    private final UUID id;
    private final UUID shiftId;
    private final String shiftName;
    private int requiredCount = 0;

    public static ShiftRequirement of(UUID shiftId, String shiftName, int requiredCount) {
        return new ShiftRequirement(UUID.randomUUID(), shiftId, shiftName, requiredCount);
    }

    public void updateRequiredCount(int newCount) {
        this.requiredCount = newCount;
    }
}
