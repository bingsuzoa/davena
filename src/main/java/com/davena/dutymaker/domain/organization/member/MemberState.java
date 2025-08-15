package com.davena.dutymaker.domain.member;

import com.davena.dutymaker.domain.ShiftType;
import com.davena.dutymaker.domain.policy.PolicyRules;
import com.davena.dutymaker.domain.policy.ShiftDateRules;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class MemberState {

    public MemberState(Long memberId) {
        this.memberId = memberId;
    }

    private final Long memberId;
    private int consecWorkDays = 0;
    private LocalDate lastWorkDate;
    private LocalDateTime lastWorkEndTime;
    private ShiftType lastWorkShift;
    private boolean isYesterdayWork;
    private int consecNights = 0;
    private int monthlyNightCount = 0;
    private int mandatoryOffRemain = 0;
    private Set<LocalDate> vacations;
    private Set<LocalDate> blocked;
    private Set<ShiftType> allowShifts;

    public void applyPostNightOff() {
        mandatoryOffRemain += PolicyRules.MIN_OFF_AFTER_NIGHT;
    }

    public boolean canWorkOn(LocalDate today) {
        return (vacations.contains(today) || blocked.contains(today)) ? false : true;
    }

    public boolean isPossibleShift(ShiftType shift) {
        return allowShifts.contains(shift);
    }

    public void updateMemberState(LocalDate today, ShiftType shift) {
        if (!shift.isWorking()) {
            isYesterdayWork = false;
            consecWorkDays = 0;
            consecNights = 0;
            return;
        }
        if (shift.isNight()) {
            consecNights++;
            monthlyNightCount++;
        }
        lastWorkDate = today;
        isYesterdayWork = true;
        lastWorkEndTime = ShiftDateRules.getEndTime(today, shift);
        lastWorkShift = shift;
    }
}
