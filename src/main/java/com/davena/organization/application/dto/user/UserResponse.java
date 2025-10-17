package com.davena.organization.application.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String name
) {
}
