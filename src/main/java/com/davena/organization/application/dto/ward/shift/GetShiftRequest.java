package com.davena.organization.application.dto.ward.shift;

import java.util.UUID;

public record GetShiftRequest(
        UUID wardId,
        UUID supervisorId
) {
}
