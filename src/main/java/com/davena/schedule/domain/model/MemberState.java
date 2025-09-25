package com.davena.schedule.domain.model;

import com.davena.organization.domain.model.ward.Shift;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class MemberState {

    public MemberState(
            UUID memberId,
            Set<UUID> possibleShifts,
            Set<Integer> holidayRequest,
            Map<Integer, Set<UUID>> unavailShiftsRequest
    ) {
        this.memberId = memberId;
        this.possibleShifts = possibleShifts;
        this.holidayRequest = holidayRequest;
        this.unavailShiftsRequest = unavailShiftsRequest;
    }

    private UUID memberId;
    private int consecWorkDays = 0;
    private LocalDate lastWorkDate;
    private LocalTime lastWorkEndTime;
    private UUID lastWorkShiftId;
    private boolean isYesterdayWork;
    private int consecNights = 0;
    private int monthlyNightCount = 0;
    private int mandatoryOffRemain = 0;

    private Set<UUID> possibleShifts;
    private Set<Integer> holidayRequest;
    private Map<Integer, Set<UUID>> unavailShiftsRequest;

    public void applyPostNightOff() {
        mandatoryOffRemain += 2;
    }

    public boolean isPossibleShift(int day, UUID shiftId) {
        if (!possibleShifts.contains(shiftId)) {
            return false;
        }
        if (holidayRequest.contains(day)) {
            return false;
        }
        if (unavailShiftsRequest.containsKey(day) && unavailShiftsRequest.get(day).contains(shiftId)) {
            return false;
        }
        return true;
    }

    public void updateMemberState(LocalDate today, Shift shift) {
        if (shift.isOff()) {
            // ✅ 휴무일 처리
            if (mandatoryOffRemain > 0) {
                mandatoryOffRemain--;
            }
            isYesterdayWork = false;

            // ❌ 기존 코드: OFF 나오면 연속 근무/나이트까지 다 초기화됨
            // consecWorkDays = 0;
            // consecNights = 0;

            // ✅ 수정: OFF는 단순히 연속을 끊는 역할만 한다
            // - 연속 근무일(consecWorkDays)은 이어지지 않고 끊어짐 → 0 초기화
            // - 연속 나이트(consecNights)는 OFF 나오면 당연히 끊어야 함 → 0 초기화
            consecWorkDays = 0;
            consecNights = 0;

            lastWorkDate = today;
            lastWorkEndTime = null;
            lastWorkShiftId = shift.getId();
            return;
        }

        // ✅ 근무일 처리
        consecWorkDays++;

        if (shift.getEndTime().isAfter(LocalTime.of(6, 0))
                && shift.getEndTime().isBefore(LocalTime.of(12, 0))) {
            // Night shift
            consecNights++;
            monthlyNightCount++;

            if (consecNights == 2) {
                mandatoryOffRemain += 2;
                consecNights = 0;
                consecWorkDays = 0;
            }
        } else {
            consecNights = 0;
        }

        if (consecWorkDays == 6) {
            mandatoryOffRemain += 2;
            consecWorkDays = 0;
        }

        lastWorkDate = today;
        isYesterdayWork = true;
        lastWorkEndTime = shift.getEndTime();
        lastWorkShiftId = shift.getId();
    }

    public MemberState copy() {
        MemberState copy = new MemberState(
                this.memberId,
                new HashSet<>(this.possibleShifts),
                new HashSet<>(this.holidayRequest),
                this.unavailShiftsRequest.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new HashSet<>(e.getValue())
                        ))
        );

        copy.isYesterdayWork = this.isYesterdayWork;
        copy.consecWorkDays = this.consecWorkDays;
        copy.consecNights = this.consecNights;
        copy.monthlyNightCount = this.monthlyNightCount;
        copy.mandatoryOffRemain = this.mandatoryOffRemain;

        copy.lastWorkDate = this.lastWorkDate;
        copy.lastWorkEndTime = this.lastWorkEndTime;
        copy.lastWorkShiftId = this.lastWorkShiftId;

        return copy;
    }
}
