package com.davena.constraint.application.dto.holidayRequest;

import java.time.LocalDate;
import java.util.UUID;

public record DeleteHolidayRequest(
        UUID wardId,
        UUID memberId,
        LocalDate requestDay
) {
}
