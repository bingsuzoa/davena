package com.davena.constraint.application.dto.holidayRequest;

import java.util.UUID;

public record WardHolidayRequest(
        UUID wardId,
        UUID supervisorId,
        int year,
        int month
) {
}
