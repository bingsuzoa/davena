package com.davena.organization.application.dto.ward.shift;

import java.util.UUID;

public record DeleteShiftRequest(
        UUID wardId,
        UUID supervisorId,
        UUID shiftId
) {
}
