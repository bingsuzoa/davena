package com.davena.constraint.application.dto.holidayRequest;

import java.time.LocalDate;
import java.util.UUID;

public record HolidayRequestDto(
        UUID id,
        LocalDate requestDay
) {
}
