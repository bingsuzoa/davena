package com.davena.organization.application.dto.ward;

import com.davena.organization.domain.model.ward.TeamId;
import com.davena.organization.domain.model.ward.WardId;

import java.util.UUID;

public record TeamResponse(
        UUID wardId,
        UUID teamId,
        String name
) {
    public static TeamResponse from(WardId wardId, TeamId teamId, String name) {
        return new TeamResponse(wardId.id(), teamId.id(), name);
    }
}
