package com.davena.organization.application.dto.ward.team;

import java.util.Map;
import java.util.UUID;

public record TeamMembersResponse(
        UUID wardId,
        UUID supervisorId,
        Map<UUID, TeamDto> teamMembers
) {
}
