package com.davena.organization.application.dto.ward.shiftRequirement;

import java.util.List;
import java.util.UUID;

public record WardRequirementsDto(
        UUID wardId,
        UUID supervisorId,
        List<TeamRequirementsDto> requirements
) {
}
