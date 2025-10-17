package com.davena.dutymaker.domain.model;

import com.davena.organization.domain.model.ward.DayType;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DayTypeResolver {

    public static DayType of(int year, int month, int day) {
        DayOfWeek dayOfWeek = LocalDate.of(year, month, day).getDayOfWeek();
        return switch (dayOfWeek) {
            case SATURDAY, SUNDAY -> DayType.WEEKEND;
            default -> DayType.WEEKDAY;
        };
    }
}
