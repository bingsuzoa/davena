package com.davena.organization.application.dto.ward;

import java.util.UUID;

public record WardResponse(
        UUID wardId,
        String name
) {
}