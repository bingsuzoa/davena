package com.davena.organization.application.dto.ward.shiftRequirement;

import java.util.UUID;

public record RequirementShiftDto(
        UUID shiftId,
        String name,
        int requiredCount
) {
}
