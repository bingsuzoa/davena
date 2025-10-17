package com.davena.organization.application.dto.ward.shiftRequirement;

import java.util.UUID;

public record GetWardRequirementsRequest(
        UUID wardId,
        UUID supervisorId
) {
}
