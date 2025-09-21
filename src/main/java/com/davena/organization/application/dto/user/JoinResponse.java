package com.davena.organization.application.dto.user;

import com.davena.organization.domain.model.user.JoinStatus;

import java.util.UUID;

public record JoinResponse(
        UUID userId,
        UUID wardId,
        String wardName,
        JoinStatus status
) {
}
