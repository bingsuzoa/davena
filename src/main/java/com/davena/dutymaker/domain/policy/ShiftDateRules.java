package com.davena.dutymaker.domain.policy;

import com.davena.dutymaker.domain.ShiftType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class ShiftDateRules {


    public static LocalDate getRealWorkDate(LocalDate today, ShiftType shift) {
        return (shift.getStartTime().equals(LocalTime.MIDNIGHT)) ? today.plusDays(1) : today;
    }

    public static LocalDateTime getStartTime(LocalDate today, ShiftType shift) {
        LocalDate startDate = (shift.getStartTime().equals(LocalTime.MIDNIGHT)) ? today.plusDays(1) : today;
        return LocalDateTime.of(startDate, shift.getStartTime());
    }

    public static LocalDateTime getEndTime(LocalDate today, ShiftType shift) {
        LocalDate startDate = getRealWorkDate(today, shift);
        boolean crossesMidnight = !shift.getEndTime().isAfter(shift.getStartTime());
        LocalDate endDate = crossesMidnight ? startDate.plusDays(1) : startDate;
        return LocalDateTime.of(endDate, shift.getEndTime());
    }
}
