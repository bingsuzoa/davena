package com.davena.schedule.domain.service;

import com.davena.organization.domain.model.ward.Shift;
import com.davena.schedule.domain.model.MemberState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class HardPolicy {

    public boolean canAssign(MemberState memberState, LocalDate today, Shift shift) {
        if (!memberState.isPossibleShift(today.getDayOfMonth(), shift.getId())) {
            return false;
        }
        if (memberState.getMandatoryOffRemain() > 0) {
            return true;
        }
        if (!hasMinRestTime(memberState, today, shift)) {
            return false;
        }
        if (isExceedConsecNights(memberState, shift)) {
            return false;
        }
        if (isExceedMonthlyNightCount(memberState, shift)) {
            return false;
        }
        if (isExceedConsecWorkDay(memberState, shift)) {
            return false;
        }
        return true;
    }

    private boolean hasMinRestTime(MemberState memberState, LocalDate today, Shift shift) {
        if (shift.isOff()) {
            return true;
        }
        if (!memberState.isYesterdayWork()) {
            return true;
        }

        LocalDateTime prevEnd = LocalDateTime.of(memberState.getLastWorkDate(), memberState.getLastWorkEndTime());
        LocalDateTime nextStart = LocalDateTime.of(today, shift.getStartTime());

        // 어제 끝이 오늘 시작보다 늦으면 하루 더해줌
        if (nextStart.isBefore(prevEnd)) {
            nextStart = nextStart.plusDays(1);
        }

        long restMinutes = Duration.between(prevEnd, nextStart).toMinutes();
        return restMinutes >= 11 * 60;
    }

    private boolean isExceedConsecNights(MemberState memberState, Shift shift) {
        if (shift.isOff()) return false;
        if (!(shift.getEndTime().isAfter(LocalTime.of(6, 0)) && shift.getEndTime().isBefore(LocalTime.of(12, 0)))) {
            return false;
        }
        int consecNight = memberState.getConsecNights();
        return consecNight + 1 > 3 ? true : false;
    }

    private boolean isExceedMonthlyNightCount(MemberState memberState, Shift shift) {
        if (shift.isOff()) return false;
        if (!(shift.getEndTime().isAfter(LocalTime.of(6, 0)) && shift.getEndTime().isBefore(LocalTime.of(12, 0)))) {
            return false;
        }
        int monthlyNightCount = memberState.getMonthlyNightCount();
        return monthlyNightCount + 1 > 15;
    }

    private boolean isExceedConsecWorkDay(MemberState memberState, Shift shift) {
        if (shift.isOff()) {
            return false;
        }
        int consecWorkDay = memberState.getConsecWorkDays();
        return consecWorkDay + 1 > 6;
    }
}
