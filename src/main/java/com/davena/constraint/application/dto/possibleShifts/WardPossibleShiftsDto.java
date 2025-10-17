package com.davena.constraint.application.dto.possibleShifts;

import java.util.List;
import java.util.UUID;

public record WardPossibleShiftsDto(
        UUID wardId,
        UUID supervisorId,
        List<MemberPossibleShiftsDto> shits
) {
}
