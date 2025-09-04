package com.davena.organization.application.dto.user;

import java.util.UUID;

public record JoinRequest(
        UUID userId,
        UUID wardId
) {
}
