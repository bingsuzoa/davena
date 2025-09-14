package com.davena.constraint.application.dto.availabiltyRequest;

import java.util.List;
import java.util.UUID;

public record MemberAvailabilityResponse(
        UUID memberId,
        String name,
        List<AvailabilityRequestDto> requests
) {
}
