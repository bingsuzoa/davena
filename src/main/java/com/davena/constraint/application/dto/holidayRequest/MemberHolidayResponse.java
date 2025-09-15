package com.davena.constraint.application.dto.holidayRequest;

import java.util.List;
import java.util.UUID;

public record MemberHolidayResponse(
        UUID wardId,
        UUID memberId,
        String memberName,
        List<HolidayRequestDto> requests
) {
}
