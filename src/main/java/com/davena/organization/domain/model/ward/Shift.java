package com.davena.organization.domain.model.ward;

import lombok.Getter;

import java.time.LocalTime;
import java.util.*;

@Getter
public class Shift {

    protected Shift(
            UUID id,
            UUID wardId,
            DayType dayType,
            String name,
            boolean isOff,
            WorkTime workTime
    ) {
        this.id = id;
        this.wardId = wardId;
        this.dayType = dayType;
        this.name = name;
        this.isOff = isOff;
        this.workTime = workTime;
    }

    private UUID id;
    private UUID wardId;
    private DayType dayType;
    private String name;
    private boolean isOff;
    private WorkTime workTime;

    public static final String Off = "OFF";
    public static final String Day = "Day";
    public static final String Eve = "Eve";
    public static final String Nig = "Nig";

    public LocalTime getStartTime() {
        if (isOff()) return null;
        return workTime.startTime();
    }

    public LocalTime getEndTime() {
        if (isOff()) return null;
        return workTime.endTime();
    }

    protected static LocalTime toLocalTime(int workHour, int workMinute) {
        if(workHour == 24) {
            return LocalTime.of(0, workMinute);
        }
        return LocalTime.of(workHour, workMinute);
    }

    protected void updateShift(DayType dayType, String name, boolean isOff, Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {
        if(!isOff) {
            LocalTime start = toLocalTime(startHour, startMinute);
            LocalTime end = toLocalTime(endHour, endMinute);
            this.dayType = dayType;
            this.name = name;
            this.workTime = new WorkTime(start, end);
        }
    }
}
