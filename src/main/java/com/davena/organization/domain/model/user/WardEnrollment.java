package com.davena.organization.domain.model.user;

import java.util.UUID;

public record WardEnrollment(
        UUID wardId,
        JoinStatus status
) {
}
