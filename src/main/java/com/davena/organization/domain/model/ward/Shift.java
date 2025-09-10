package com.davena.organization.domain.model.ward;

import com.davena.organization.application.dto.ward.shift.ShiftDto;
import lombok.Getter;

import java.time.LocalTime;
import java.util.*;

@Getter
public class Shift {

    public Shift(
            UUID id,
            UUID wardId,
            String name,
            boolean isDefault,
            boolean isOff,
            WorkTime workTime
    ) {
        this.id = id;
        this.wardId = wardId;
        this.name = name;
        this.isDefault = isDefault;
        this.isOff = isOff;
        this.workTime = workTime;
    }

    private UUID id;
    private UUID wardId;
    private String name;
    private boolean isDefault;
    private boolean isOff;
    private WorkTime workTime;

    public static final String Off = "OFF";
    public static final String Day = "Day";
    public static final String Eve = "Eve";
    public static final String Nig = "Nig";

    protected static Map<DayType, List<Shift>> getDefaultShifts(UUID wardId) {
        Map<DayType, List<Shift>> shiftsOfDayType = new HashMap<>();
        shiftsOfDayType.put(DayType.WEEKDAY, createDefaultShifts(wardId));
        shiftsOfDayType.put(DayType.WEEKEND, createDefaultShifts(wardId));
        return shiftsOfDayType;
    }

    private static List<Shift> createDefaultShifts(UUID wardId) {
        List<Shift> shifts = new ArrayList<>();
        shifts.add(new Shift(UUID.randomUUID(), wardId, Off, true, true, new WorkTime(null, null)));
        shifts.add(new Shift(UUID.randomUUID(), wardId, Day, true, false, new WorkTime(LocalTime.of(07, 30), LocalTime.of(16, 30))));
        shifts.add(new Shift(UUID.randomUUID(), wardId, Eve, true, false, new WorkTime(LocalTime.of(16, 00), LocalTime.of(00, 00))));
        shifts.add(new Shift(UUID.randomUUID(), wardId, Nig, true, false, new WorkTime(LocalTime.of(00, 00), LocalTime.of(8, 00))));
        return shifts;
    }

    protected static Shift addNewShift(UUID wardId, String name, LocalTime start, LocalTime end) {
        return new Shift(UUID.randomUUID(), wardId, name, false, false, new WorkTime(start, end));
    }

    protected LocalTime getStartTime() {
        return workTime.startTime();
    }

    protected LocalTime getEndTime() {
        return workTime.endTime();
    }

    protected void update(String name, LocalTime start, LocalTime end) {
        this.name = name;
        workTime = new WorkTime(start, end);
    }

    public ShiftDto toDto() {
        return new ShiftDto(id, name, getStartTime(), getEndTime());
    }
}
