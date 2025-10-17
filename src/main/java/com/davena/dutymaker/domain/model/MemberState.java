package com.davena.dutymaker.domain.model;

import com.davena.organization.domain.model.ward.Shift;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class MemberState {

    public MemberState(
            UUID id,
            Set<UUID> possibleShifts,
            Set<UUID> holidayRequests,
            Set<UUID> unavailShiftRequests
    ) {
        this.id = id;
        this.possibleShifts = possibleShifts;
        this.holidayRequests = holidayRequests;
        this.unavailShiftRequests = unavailShiftRequests;
    }

    private UUID id;
    private Set<UUID> possibleShifts;
    private Set<UUID> holidayRequests;
    private Set<UUID> unavailShiftRequests;
    private int consecWorkdays = 0;
    private LocalDate lastWorkDate;
    private LocalTime lastWorkEndTime;
    private boolean isYesterdayWork;
    private UUID lastWorkShiftId;
    private int consecNights = 0;
    private int monthlyNightCount = 0;
    private int mandatoryOffRemain = 0;

    public void updateMemberState(LocalDate today, Shift shift) {
        if (shift.isOff()) {
            isYesterdayWork = false;
            consecNights = 0;
            return;
        }
        if (shift.isNight()) {
            consecNights++;
            monthlyNightCount++;
        }

        consecWorkdays++;
        lastWorkDate = today;
        lastWorkEndTime = shift.getEndTime();
        isYesterdayWork = true;
        lastWorkShiftId = shift.getId();
    }
}
