package com.davena.possibleShifts.application.dto.wardCharge;

import java.util.List;
import java.util.UUID;

public record TeamChargeDto(
        UUID teamId,
        String teamName,
        List<ChargeMemberDto> chargeMembersDto
) {
}
