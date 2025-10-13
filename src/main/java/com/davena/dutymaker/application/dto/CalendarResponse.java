package com.davena.dutymaker.application.dto;

public record CalendarResponse(
        int year,
        int month,
        int lastDate,
) {
}
