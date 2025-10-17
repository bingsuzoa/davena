package com.davena.organization.application.dto.ward.shiftRequirement;

import com.davena.organization.domain.model.ward.DayType;

import java.util.UUID;

public record RequirementShiftDto(
        UUID shiftId,
        DayType dayType,
        String shiftName,
        int requiredCount
) {
}
