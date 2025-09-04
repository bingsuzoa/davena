package com.davena.organization.application.dto.ward;

import java.util.UUID;

public record WardRequest(
        UUID hospitalId,
        UUID supervisorId,
        String wardName
) {
}
