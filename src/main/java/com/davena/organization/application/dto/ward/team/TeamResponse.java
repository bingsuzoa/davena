package com.davena.organization.application.dto.ward.team;

import java.util.UUID;

public record TeamResponse(
        UUID wardId,
        UUID teamId,
        String name
) {
}
