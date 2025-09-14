package com.davena.constraint.application.dto.availabiltyRequest;

import com.davena.constraint.domain.model.RequestType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateAvailabilityRequest(
        UUID memberId,
        LocalDate start,
        LocalDate end,
        List<UUID> shiftIds,
        RequestType type,
        String reason
) {
}
