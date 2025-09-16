package com.davena.organization.application.dto.ward.team;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdateTeamMembersRequest(
        UUID wardId,
        UUID supervisorId,
        Map<UUID, List<UUID>> usersOfTeam
) {
}
