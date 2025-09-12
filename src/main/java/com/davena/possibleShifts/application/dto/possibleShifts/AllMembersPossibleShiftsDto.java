package com.davena.possibleShifts.application.dto.possibleShifts;

import java.util.List;
import java.util.UUID;

public record AllMembersPossibleShiftsDto(
        UUID wardId,
        UUID supervisorId,
        List<MemberPossibleShiftsDto> membersPossibleShiftsDto
) {
}
