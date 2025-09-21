package com.davena.constraint.application.dto.holidayRequest;

import java.util.Map;
import java.util.UUID;

public record WardHolidayResponse(
        Map<UUID, MemberHolidayResponse> requests
) {
}
