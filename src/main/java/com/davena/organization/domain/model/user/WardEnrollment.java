package com.davena.organization.domain.model.user;

import com.davena.organization.domain.model.ward.WardId;

public record WardEnrollment(
        WardId wardId,
        JoinStatus status
) {
}
