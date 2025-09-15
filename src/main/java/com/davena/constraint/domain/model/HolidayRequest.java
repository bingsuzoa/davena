package com.davena.constraint.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class HolidayRequest {

    public HolidayRequest(
            UUID id,
            UUID memberId,
            int year,
            int month,
            LocalDate requestDay,
            String reason
    ) {
        this.id = id;
        this.memberId = memberId;
        this.year = year;
        this.month = month;
        this.requestDay = requestDay;
        this.reason = reason;
    }

    private UUID id;
    private UUID memberId;
    private int year;
    private int month;
    private LocalDate requestDay;
    private String reason;

    public static HolidayRequest create(UUID memberId, LocalDate requestDay, String reason) {
        int year = requestDay.getYear();
        int month = requestDay.getMonthValue();
        return new HolidayRequest(UUID.randomUUID(), memberId, year, month, requestDay, reason);
    }
}
