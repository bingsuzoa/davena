package com.davena.constraint.application.dto.shiftRequest;

import java.util.UUID;

public record UnavailableShiftDto(
        UUID shiftId,
        String shiftName
) {
}
