package com.davena.organization.application.dto.ward.team;

import java.util.UUID;

public record GetTeamRequest(
        UUID wardId,
        UUID supervisorId
) {
}
