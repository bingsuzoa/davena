package com.davena.constraint.application.dto.availabiltyRequest;

import java.time.LocalDate;
import java.util.UUID;

public record GetAvailabilityRequest(
        UUID memberId,
        LocalDate month
) {
}
