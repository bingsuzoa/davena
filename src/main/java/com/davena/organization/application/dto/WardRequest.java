package com.davena.organization.application.dto;

import java.util.UUID;

public record WardRequest(
        UUID hospitalId,
        UUID memberId,
        String wardName
) {
}
