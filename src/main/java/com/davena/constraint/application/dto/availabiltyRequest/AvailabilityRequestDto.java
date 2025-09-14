package com.davena.constraint.application.dto.availabiltyRequest;

import com.davena.constraint.domain.model.RequestType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AvailabilityRequestDto(
        UUID id,
        LocalDate start,
        LocalDate end,
        List<UnavailableShiftDto> shiftDtos,
        RequestType type
) {
}
