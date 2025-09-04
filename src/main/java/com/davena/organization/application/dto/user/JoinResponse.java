package com.davena.organization.application.dto.user;

import com.davena.organization.domain.model.user.JoinStatus;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.WardId;

import java.util.UUID;

public record JoinResponse(
        UUID userId,
        UUID wardId,
        String wardName,
        JoinStatus status
)  {
    public static JoinResponse from(UserId userId, WardId wardId, String name, JoinStatus status) {
        return new JoinResponse(userId.id(), wardId.id(), name, status);
    }
}
