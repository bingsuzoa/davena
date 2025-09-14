package com.davena.constraint.application.dto.possibleShifts;

import java.util.UUID;

public record AllMembersPossibleShiftsRequest(
        UUID wardId,
        UUID supervisorId
) {
}
