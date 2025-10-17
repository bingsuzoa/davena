package com.davena.constraint.application.dto.holidayRequest;

import java.util.UUID;

public record MemberHolidayRequest(
        UUID wardId,
        UUID memberId,
        int year,
        int month
) {
}
