package com.davena.constraint.application.dto.holidayRequest;

import java.util.UUID;

public record HolidayRequestDto(
        UUID id,
        int year,
        int month,
        int day
) {
}
