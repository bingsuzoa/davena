package com.davena.organization.application.dto.ward.team;

import java.util.UUID;

public record DeleteTeamRequest(
        UUID wardId,
        UUID supervisorId,
        UUID teamId
) {
}
