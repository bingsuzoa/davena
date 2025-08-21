package com.davena.dutymaker.service.generator;

import com.davena.dutymaker.domain.organization.member.MemberState;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.policy.PolicyRules;
import com.davena.dutymaker.domain.policy.ShiftDateRules;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HardPolicyFilter {

    public boolean canAssign(MemberState memberState, LocalDate today, Team team, ShiftType expectedShift) {
        if (!team.getName().equals(memberState.getTeamName())) {
            return false;
        }
        if (!memberState.isPossibleShift(expectedShift)) {
            return false;
        }
        if (!memberState.canWorkOn(today)) {
            return false;
        }
        if (memberState.getMandatoryOffRemain() > 0) {
            return false;
        }
        if (!hasMinRestTime(memberState, today, expectedShift)) {
            return false;
        }
        if (isExceedConsecNights(memberState, expectedShift)) {
            return false;
        }
        if (isExceedMonthlyNightCount(memberState, expectedShift)) {
            return false;
        }
        if (isExceedConsecWorkDay(memberState, expectedShift)) {
            return false;
        }
        return true;
    }

    private boolean hasMinRestTime(MemberState memberState, LocalDate today, ShiftType expectedShift) {
        if (!memberState.isYesterdayWork()) {
            return true;
        }

        long restMinutes = Duration.between(
                memberState.getLastWorkEndTime(),
                ShiftDateRules.getStartTime(today, expectedShift)
        ).toMinutes();
        return restMinutes >= PolicyRules.MIN_REST_HOURS * 60;
    }

    private boolean isExceedConsecNights(MemberState memberState, ShiftType expectedShift) {
        if (!expectedShift.isNight()) {
            return false;
        }
        int consecNight = memberState.getConsecNights();
        return consecNight + 1 >= PolicyRules.MAX_CONSEC_NIGHTS ? true : false;
    }

    private boolean isExceedMonthlyNightCount(MemberState memberState, ShiftType expectedShift) {
        if (!expectedShift.isNight()) {
            return false;
        }
        int monthlyNightCount = memberState.getMonthlyNightCount();
        return monthlyNightCount + 1 >= PolicyRules.MONTHLY_NIGHT_MAX;
    }

    private boolean isExceedConsecWorkDay(MemberState memberState, ShiftType expectedShift) {
        if (!expectedShift.isWorking()) {
            return false;
        }
        int consecWorkDay = memberState.getConsecWorkDays();
        return consecWorkDay + 1 >= PolicyRules.MAX_CONSEC_WORK_DAY;
    }

}
