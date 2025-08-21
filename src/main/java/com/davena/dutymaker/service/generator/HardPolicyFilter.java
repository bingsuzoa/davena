package com.davena.dutymaker.service.generator;

import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.member.MemberState;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.policy.PolicyRules;
import com.davena.dutymaker.domain.policy.ShiftDateRules;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HardPolicyFilter {

    /// ///이거 테스트때문에 만든거니까 나중에 삭제
    private final MemberRepository memberRepository;

    public boolean canAssign(MemberState memberState, LocalDate today, ShiftType expectedShift) {
        Member member = memberRepository.findById(memberState.getMemberId()).orElseThrow();
        System.out.println("🔆" + member.getName() + " : "
                + "연속 근무일 수 : " + memberState.getConsecWorkDays()
                + "마지막 근무일 : " + memberState.getLastWorkDate()
                + "마지막 근무 시간 : " + memberState.getLastWorkEndTime()
                + "마지막 근무명 : " + memberState.getLastWorkShift().getName()
                + "어제 근무 함? : " + memberState.isYesterdayWork()
                + "연속 나이트 개수 : " + memberState.getConsecNights()
                + "한달에 나이트 몇개함? : " + memberState.getMonthlyNightCount()
                + "강제 오프 있음 ? : " + memberState.getMandatoryOffRemain()
        );
        if (!memberState.isPossibleShift(expectedShift)) {
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
        return consecNight > PolicyRules.MAX_CONSEC_NIGHTS ? true : false;
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
