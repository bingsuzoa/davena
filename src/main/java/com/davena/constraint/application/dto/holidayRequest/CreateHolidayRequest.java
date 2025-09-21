package com.davena.constraint.application.dto.holidayRequest;

import java.time.LocalDate;
import java.util.UUID;

public record CreateHolidayRequest(
        UUID wardId,
        UUID memberId,
        LocalDate requestDay,
        String reason
) {
}
