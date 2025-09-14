package com.davena.constraint.application.dto.availabiltyRequest;

import java.util.List;
import java.util.UUID;

public record WardAvailabilityResponse(
        UUID wardId,
        List<MemberAvailabilityResponse> allMembersRequests
) {
}
