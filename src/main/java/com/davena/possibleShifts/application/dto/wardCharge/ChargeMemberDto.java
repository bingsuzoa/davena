package com.davena.possibleShifts.application.dto.wardCharge;

import java.util.UUID;

public record ChargeMemberDto(
        UUID memberId,
        String name,
        boolean canCharge,
        int rank
) {
}
