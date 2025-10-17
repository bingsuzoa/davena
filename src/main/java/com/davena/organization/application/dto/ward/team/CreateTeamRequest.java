package com.davena.organization.application.dto.ward.team;

import java.util.UUID;

public record CreateTeamRequest(
        UUID wardId,
        UUID supervisorId,
        String name
) {
}
