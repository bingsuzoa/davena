package com.davena.organization.application.dto.ward.team;

import java.util.UUID;

public record TeamRequest(
        UUID teamId,
        UUID supervisorId,
        UUID wardId,
        String name
) {
}
