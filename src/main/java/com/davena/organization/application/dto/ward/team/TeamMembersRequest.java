package com.davena.organization.application.dto.ward.team;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TeamMembersRequest(
        UUID supervisorId,
        UUID wardId,
        Map<UUID, List<UUID>> usersOfTeam
) {
}
