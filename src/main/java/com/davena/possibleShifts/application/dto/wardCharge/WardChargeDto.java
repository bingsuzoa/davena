package com.davena.possibleShifts.application.dto.wardCharge;

import java.util.List;
import java.util.UUID;

public record WardChargeDto(
        UUID wardId,
        UUID supervisorId,
        List<TeamChargeDto> teamChargeDto
) {
}
