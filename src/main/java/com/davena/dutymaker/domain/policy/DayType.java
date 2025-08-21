package com.davena.dutymaker.domain.policy;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum DayType {
    WEEKDAY, WEEKEND, HOLIDAY;

    public static DayType from(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY)
                ? WEEKEND
                : WEEKDAY;
    }
}
