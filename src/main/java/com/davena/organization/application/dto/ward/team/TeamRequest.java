package com.davena.organization.application.dto.ward.team;

import java.util.UUID;

public record TeamRequest(
        UUID teamID,
        UUID supervisorId,
        UUID wardId,
        String name
) {
}
