package com.davena.organization.application.dto.ward.shiftRequirement;

import java.util.List;
import java.util.UUID;

public record RequirementsResponse(
        UUID supervisorId,
        UUID wardId,
        List<RequirementDto> requirements
) {
}