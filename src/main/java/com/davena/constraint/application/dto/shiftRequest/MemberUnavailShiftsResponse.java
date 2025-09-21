package com.davena.constraint.application.dto.shiftRequest;

import java.util.List;
import java.util.UUID;

public record MemberUnavailShiftsResponse(
        UUID wardId,
        UUID memberId,
        String memberName,
        List<UnavailableShiftDto> unavailableShifts
) {
}
