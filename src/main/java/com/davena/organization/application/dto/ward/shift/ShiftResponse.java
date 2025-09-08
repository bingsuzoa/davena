package com.davena.organization.application.dto.ward.shift;

import java.util.UUID;

public record ShiftResponse(
        UUID wardId,
        UUID shiftId,
        String name
) {
}
