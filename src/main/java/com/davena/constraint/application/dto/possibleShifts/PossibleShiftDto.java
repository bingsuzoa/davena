package com.davena.constraint.application.dto.possibleShifts;

import java.util.UUID;

public record PossibleShiftDto(
        UUID shiftId,
        String shiftName,
        boolean isPossible
) {
}
