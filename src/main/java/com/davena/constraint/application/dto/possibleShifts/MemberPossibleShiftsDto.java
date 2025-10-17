package com.davena.constraint.application.dto.possibleShifts;

import java.util.List;
import java.util.UUID;

public record MemberPossibleShiftsDto(
        UUID userId,
        String userName,
        List<PossibleShiftDto> shifts
) {
}
