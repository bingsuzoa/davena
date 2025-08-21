package com.davena.dutymaker.domain.organization.member;

import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.domain.policy.PolicyRules;
import com.davena.dutymaker.domain.policy.ShiftDateRules;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
public class MemberState {

    public MemberState(
            Long memberId,
            Long teamId,
            SkillGrade grade,
            Set<ShiftType> allowedShifts
    ) {
        this.memberId = memberId;
        this.teamId = teamId;
        this.grade = grade;
        this.allowShifts = allowedShifts;
    }

    private final Long memberId;
    private final Long teamId;
    private final SkillGrade grade;
    private int consecWorkDays = 0;
    private LocalDate lastWorkDate;
    private LocalDateTime lastWorkEndTime;
    private ShiftType lastWorkShift;
    private boolean isYesterdayWork;
    private int consecNights = 0;
    private int monthlyNightCount = 0;
    private int mandatoryOffRemain = 0;
    private Set<ShiftType> allowShifts;

    public void applyPostNightOff() {
        mandatoryOffRemain += PolicyRules.MIN_OFF_AFTER_NIGHT;
    }

    public boolean isPossibleShift(ShiftType shift) {
        return allowShifts.contains(shift);
    }

    public void updateMemberState(LocalDate today, ShiftType shift) {
        if (!shift.isWorking()) {
            if (mandatoryOffRemain > 0) {
                mandatoryOffRemain--;
            }
            isYesterdayWork = false;
            consecWorkDays = 0;
            consecNights = 0;

            lastWorkDate = today;
            lastWorkEndTime = ShiftDateRules.getEndTime(today, shift);
            lastWorkShift = shift;
            return;
        }
        consecWorkDays++;

        if (shift.isNight()) {
            consecNights++;
            monthlyNightCount++;

            if (consecNights == PolicyRules.MAX_CONSEC_NIGHTS) {
                mandatoryOffRemain += PolicyRules.MIN_OFF_AFTER_NIGHT;;
                consecNights = 0;
                consecWorkDays = 0;
            }
        } else {
            consecNights = 0;
        }

        if (consecWorkDays == PolicyRules.MAX_CONSEC_WORK_DAY) {
            mandatoryOffRemain += 2;
            consecWorkDays = 0;
        }

        lastWorkDate = today;
        isYesterdayWork = true;
        lastWorkEndTime = ShiftDateRules.getEndTime(today, shift);
        lastWorkShift = shift;
    }
}
