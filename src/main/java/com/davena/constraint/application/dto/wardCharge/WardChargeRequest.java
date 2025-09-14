package com.davena.constraint.application.dto.wardCharge;

import java.util.UUID;

public record WardChargeRequest(
        UUID wardId,
        UUID supervisorId
) {
}
