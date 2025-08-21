package com.davena.dutymaker.service.generator;

import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.member.MemberState;
import com.davena.dutymaker.domain.schedule.Candidate;
import com.davena.dutymaker.domain.schedule.CandidateAssignment;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.MemberAllowedShiftRepository;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.ScheduleRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberStateService {

    private final ScheduleRepository scheduleRepository;
    private final WardRepository wardRepository;
    private final MemberRepository memberRepository;
    private final MemberAllowedShiftRepository allowedShiftRepository;

    public Map<Long, MemberState> initMemberState(Long wardId, Long scheduleId) {
        Ward ward = getWardWithTeamsAndRules(wardId);
        Schedule schedule = getScheduleWithDraft(scheduleId);
        List<Member> membersOfWard = getMembersOfWard(wardId);

        Map<Long, MemberState> memberStates = new HashMap<>();
        for (Member member : membersOfWard) {
            MemberState memberState = new MemberState(member.getId(), member.getTeam().getId(), member.getSkillGrade(), getPossibleShifts(member));
            memberStates.put(member.getId(), applyLastMonth(memberState, ward, schedule));
        }
        return memberStates;
    }

    private Set<ShiftType> getPossibleShifts(Member member) {
        return new HashSet<>(allowedShiftRepository.findShiftTypesByMemberId(member.getId()));
    }

    private MemberState applyLastMonth(MemberState memberState, Ward ward, Schedule schedule) {
        return applyLastWeek(memberState, ward, schedule);
    }

    private MemberState applyLastWeek(MemberState memberState, Ward ward, Schedule schedule) {
        String lastMonth = YearMonth.parse(schedule.getYearMonth())
                .minusMonths(1)
                .toString();

        Candidate lastCandidate = scheduleRepository
                .findByWardAndYearMonthWithSelected(ward.getId(), lastMonth)
                .orElseThrow(() -> new IllegalArgumentException(Schedule.NOT_EXIST_FINALIZED_SCHEDULE))
                .getSelectedCandidate();

        if (lastCandidate == null) {
            throw new IllegalStateException(Schedule.NOT_EXIST_FINALIZED_SCHEDULE);
        }

        List<CandidateAssignment> lastAssignments =
                lastCandidate.getLastWeekAssignmentsFor(memberState).stream()
                        .sorted(Comparator.comparing(CandidateAssignment::getWorkDate))
                        .toList();

        for (CandidateAssignment assignment : lastAssignments) {
            ShiftType shift = assignment.getShiftType();
            LocalDate date = assignment.getWorkDate();
            memberState.updateMemberState(date, shift);
        }
        return memberState;
    }

    private Ward getWardWithTeamsAndRules(Long wardId) {
        return wardRepository.getWardWithTeamsAndRules(wardId).orElseThrow(() ->
                new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }

    private List<Member> getMembersOfWard(Long wardId) {
        return memberRepository.findMembersByWardId(wardId);
    }

    private List<Member> getMembersOfTeam(Long teamId) {
        return memberRepository.findMembersWithTeamByTeamId(teamId);
    }

    private Schedule getScheduleWithDraft(Long scheduleId) {
        return scheduleRepository.findByIdWithDraft(scheduleId).orElseThrow(() ->
                new IllegalArgumentException(Schedule.NOT_EXIST_THIS_MONTH_SCHEDULE));
    }
}
