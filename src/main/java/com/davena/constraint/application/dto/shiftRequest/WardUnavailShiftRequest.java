package com.davena.constraint.application.dto.shiftRequest;

import java.util.UUID;

public record WardUnavailShiftRequest(
        UUID wardId,
        UUID supervisorId,
        int year,
        int month
) {
}
