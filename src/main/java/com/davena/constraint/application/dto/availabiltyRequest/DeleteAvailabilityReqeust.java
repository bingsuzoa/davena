package com.davena.constraint.application.dto.availabiltyRequest;

import java.util.UUID;

public record DeleteAvailabilityReqeust(
        UUID memberId,
        UUID requestId
) {
}
