package com.davena.organization.application.dto.user;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name
) {
}
