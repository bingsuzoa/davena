package com.davena.organization.application.dto.ward.shift;

import java.util.UUID;

public record ShiftRequest(
        UUID supervisorId,
        UUID wardId,
        String name
) {
}
