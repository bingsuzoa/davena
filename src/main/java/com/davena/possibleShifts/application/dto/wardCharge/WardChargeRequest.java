package com.davena.possibleShifts.application.dto.wardCharge;

import java.util.UUID;

public record WardChargeRequest(
        UUID wardId,
        UUID supervisorId
) {
}
