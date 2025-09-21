package com.davena.constraint.application.dto.possibleShifts;

import java.util.UUID;

public record WardPossibleShiftsRequest(
        UUID wardId,
        UUID supervisorId
) {
}
