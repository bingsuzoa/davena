package com.davena.organization.domain.model.ward;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum DayType {
    WEEKDAY, WEEKEND;

    public static DayType getDayType(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return DayType.WEEKEND;
        }
        return DayType.WEEKDAY;
    }
}
