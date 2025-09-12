package com.davena.possibleShifts.application.dto.possibleShifts;

import java.util.UUID;

public record AllMembersPossibleShiftsRequest(
        UUID wardId,
        UUID supervisorId
) {
}
