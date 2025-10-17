package com.davena.constraint.application.dto.shiftRequest;

import java.util.UUID;

public record MemberUnavailShiftRequest(
        UUID wardId,
        UUID memberId,
        int year,
        int month
) {
}
