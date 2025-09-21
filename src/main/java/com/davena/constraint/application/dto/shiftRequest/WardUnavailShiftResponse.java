package com.davena.constraint.application.dto.shiftRequest;

import java.util.Map;
import java.util.UUID;

public record WardUnavailShiftResponse(
        Map<UUID, MemberUnavailShiftsResponse> unavailShifts
) {
}
