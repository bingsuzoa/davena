package com.davena.organization.application.dto.ward.team;

import java.util.UUID;

public record TeamDto(
        UUID id,
        String name,
        boolean isDefault
) {
}
