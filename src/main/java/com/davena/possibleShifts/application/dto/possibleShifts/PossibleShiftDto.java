package com.davena.possibleShifts.application.dto.possibleShifts;

import java.util.UUID;

public record PossibleShiftDto(
        UUID shiftId,
        String shiftName,
        boolean isPossible
) {
}
