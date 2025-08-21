package com.davena.dutymaker.service.generator;

import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.member.MemberState;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.organization.team.TeamState;
import com.davena.dutymaker.domain.schedule.Candidate;
import com.davena.dutymaker.domain.schedule.CandidateAssignment;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.CandidateAssignmentRepository;
import com.davena.dutymaker.repository.CandidateRepository;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.ShiftTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final MemberStateService memberStateService;
    private final TeamStateService teamStateService;
    private final HardPolicyFilter hardPolicyFilter;
    private final CandidateAssignmentRepository candidateAssignmentRepository;
    private final MemberRepository memberRepository;
    private final ShiftTypeRepository shiftTypeRepository;

    public Candidate generateCandidate(Schedule schedule, Ward ward) {
        Candidate candidate = candidateRepository.save(new Candidate(schedule));

        YearMonth ym = YearMonth.parse(schedule.getYearMonth());
        Map<Long, MemberState> memberStates = initMemberStates(ward.getId(), schedule.getId());
        Map<Long, Map<Integer, TeamState>> teamStates = initTeamStates(schedule.getId(), ward.getId());

        ShiftType off = shiftTypeRepository.findByWardIdAndIsWorkingFalse(ward.getId()).orElseThrow();

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate today = LocalDate.of(ym.getYear(), ym.getMonth(), day);

            for (MemberState ms : memberStates.values()) {
                if (ms.getMandatoryOffRemain() > 0) {
                    assignOff(ms, candidate, today, off, false);
                    ms.updateMemberState(today, off);
                }
            }

            for (Team team : ward.getTeams()) {
                TeamState todayTeamState = teamStates.get(team.getId()).get(day);

                for (ShiftType shift : todayTeamState.getShiftTypes()) {
                    if(!shift.isWorking()) continue;
                    int need = todayTeamState.getRemain().get(shift);
                    if (need <= 0) continue;

                    List<MemberState> possibleMembers = getPossibleMemberOfDay(memberStates, candidate, today, shift);

                    if (todayTeamState.needChargeFor(shift)) {
                        pickCharge(possibleMembers, candidate, today, shift, todayTeamState);
                        need--;
                    }

                    while (need > 0) {
                        pickBalancedCandidate(possibleMembers, candidate, today, shift, todayTeamState);
                        need--;
                    }
                }
            }
        }
        return candidate;
    }

    private void pickBalancedCandidate(List<MemberState> possibleMembers, Candidate candidate, LocalDate today, ShiftType shift, TeamState teamState) {
        if (possibleMembers == null || possibleMembers.isEmpty()) {
            throw new IllegalArgumentException(today + " " + shift + " 랜덤 배정 불가, possibleMembers 비어 있음");
        }

        Map<SkillGrade, Integer> appliedGrade = teamState.getAppliedGrade();

        Map<SkillGrade, List<MemberState>> groupedOfGrade = possibleMembers.stream()
                .collect(Collectors.groupingBy(MemberState::getGrade));

        SkillGrade targetGrade = groupedOfGrade.keySet().stream()
                .min(Comparator.comparing(grade -> appliedGrade.getOrDefault(grade, 0)))
                .orElseThrow();

        List<MemberState> targetMembers = groupedOfGrade.get(targetGrade);
        MemberState selected = targetMembers.get(ThreadLocalRandom.current().nextInt(targetMembers.size()));
        assignWork(selected, candidate, today, shift, false, teamState);
        possibleMembers.remove(selected);
    }

    private void pickCharge(List<MemberState> possibleMembers, Candidate candidate, LocalDate today, ShiftType shift, TeamState teamState) {
        PriorityQueue<Member> chargeOrder = new PriorityQueue<>(teamState.getChargeOrder());
        while (!chargeOrder.isEmpty()) {
            Member possibleMember = chargeOrder.poll();
            System.out.println("차지 후보: " + possibleMember.getName() + " / id=" + possibleMember.getId());

            Optional<MemberState> match = possibleMembers.stream()
                    .filter(ms -> ms.getMemberId().equals(possibleMember.getId()))
                    .findFirst();

            if (match.isPresent()) {
                MemberState charge = match.get();
                System.out.println("→ 매칭됨. 상태=" + charge);
                assignWork(charge, candidate, today, shift, true, teamState);
                possibleMembers.remove(charge);
                return;

            }
        }
        throw new IllegalArgumentException(today + "에 " + shift + " 차지 배정 불가");
    }

    private void assignWork(MemberState memberState, Candidate candidate, LocalDate today, ShiftType shiftType, boolean isCharge, TeamState teamState) {
        memberState.updateMemberState(today, shiftType);
        Member member = memberRepository.findById(memberState.getMemberId()).orElseThrow();
        CandidateAssignment candidateAssignment = candidateAssignmentRepository.save(new CandidateAssignment(candidate, member, today, shiftType, isCharge));
        candidate.addAssignments(candidateAssignment);
        teamState.apply(shiftType, member.getSkillGrade(), isCharge);
    }

    private void assignOff(MemberState memberState, Candidate candidate, LocalDate today, ShiftType shiftType, boolean isCharge) {
        memberState.updateMemberState(today, shiftType);
        Member member = memberRepository.findById(memberState.getMemberId()).orElseThrow();
        CandidateAssignment candidateAssignment = candidateAssignmentRepository.save(new CandidateAssignment(candidate, member, today, shiftType, isCharge));
        candidate.addAssignments(candidateAssignment);
    }

    private List<MemberState> getPossibleMemberOfDay(Map<Long, MemberState> memberStates, Candidate candidate, LocalDate today, ShiftType shiftType) {
        List<MemberState> possibleMembers = new ArrayList<>();
        for (MemberState memberState : memberStates.values()) {

            boolean alreadyAssigned = candidate.getAssignments().stream()
                    .anyMatch(a -> a.getMember().getId().equals(memberState.getMemberId())
                            && a.getWorkDate().equals(today));

            if (alreadyAssigned) continue;

            if (hardPolicyFilter.canAssign(memberState, today, shiftType)) {
                possibleMembers.add(memberState);
            }
        }
        return possibleMembers;
    }

    private Map<Long, MemberState> initMemberStates(Long wardId, Long scheduleId) {
        return memberStateService.initMemberState(wardId, scheduleId);
    }

    private Map<Long, Map<Integer, TeamState>> initTeamStates(Long scheduleId, Long wardId) {
        return teamStateService.initTeamState(scheduleId, wardId);
    }
}
