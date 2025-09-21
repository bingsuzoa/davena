package com.davena.constraint.application.dto.shiftRequest;

import java.time.LocalDate;
import java.util.UUID;

public record CreateShiftRequest(
        UUID wardId,
        UUID memberId,
        UUID shiftId,
        LocalDate requestDay,
        String reason
) {
}
