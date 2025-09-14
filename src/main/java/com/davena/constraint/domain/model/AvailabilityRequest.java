package com.davena.constraint.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
public class AvailabilityRequest {

    public AvailabilityRequest(
            UUID id,
            UUID memberId,
            LocalDate start,
            LocalDate end,
            List<UUID> shiftIds,
            RequestType type,
            String reason
    ) {
        this.id = id;
        this.memberId = memberId;
        this.start = start;
        this.end = end;
        this.shiftIds = shiftIds;
        this.type = type;
        this.reason = reason;
    }

    private UUID id;
    private UUID memberId;
    private LocalDate start;
    private LocalDate end;
    private List<UUID> shiftIds;
    private RequestType type;
    private String reason;

    public static AvailabilityRequest create(UUID memberId, LocalDate start, LocalDate end, List<UUID> shiftIds, RequestType type, String reason) {
        return new AvailabilityRequest(UUID.randomUUID(), memberId, start, end, shiftIds, type, reason);
    }
}
