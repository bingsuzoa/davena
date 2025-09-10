package com.davena.organization.application.dto.ward.shiftRequirement;

import com.davena.organization.domain.model.ward.DayType;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public record RequirementDto(
        UUID teamId,
        String teamName,
        Map<DayType, List<RequirementShiftDto>> requirements
) {
}
