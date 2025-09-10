package com.davena.organization.application.dto.ward.shiftRequirement;

import com.davena.organization.domain.model.ward.DayType;

import java.util.UUID;

public record RequirementRequest(
        UUID supervisorId,
        UUID wardId,
        UUID teamId,
        DayType dayType,
        UUID shiftId,
        int updatedRequirement
) {
}
