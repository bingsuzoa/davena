package com.davena.organization.application.dto.ward.shiftRequirement;

import java.util.List;
import java.util.UUID;

public record TeamRequirementsDto(
        UUID teamId,
        String teamName,
        List<RequirementShiftDto> requirements
) {
}
