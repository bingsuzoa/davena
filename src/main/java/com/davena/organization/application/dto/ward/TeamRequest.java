package com.davena.organization.application.dto.ward;

import java.util.UUID;

public record TeamRequest(
        UUID supervisorId,
        UUID wardId,
        String name
) {
}
