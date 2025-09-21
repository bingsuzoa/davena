package com.davena.constraint.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class UnavailShiftRequest {

    public UnavailShiftRequest(
            UUID id,
            UUID memberId,
            int year,
            int month,
            LocalDate requestDay,
            UUID shiftId,
            String reason
    ) {
        this.id = id;
        this.memberId = memberId;
        this.year = year;
        this.month = month;
        this.requestDay = requestDay;
        this.shiftId = shiftId;
        this.reason = reason;
    }

    private UUID id;
    private UUID memberId;
    private int year;
    private int month;
    private LocalDate requestDay;
    private UUID shiftId;
    private String reason;

    public static UnavailShiftRequest create(UUID memberId, LocalDate requestDay, UUID shiftId, String reason) {
        int year = requestDay.getYear();
        int month = requestDay.getMonthValue();
        return new UnavailShiftRequest(UUID.randomUUID(), memberId, year, month, requestDay, shiftId, reason);
    }


}
