package com.davena.constraint.application.dto.availabiltyRequest;

import java.util.UUID;

public record UnavailableShiftDto(
        UUID shiftId,
        String shiftName
) {
}
