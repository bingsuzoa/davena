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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final MemberStateService memberStateService;
    private final TeamStateService teamStateService;
    private final HardPolicyFilter hardPolicyFilter;
    private final CandidateAssignmentRepository candidateAssignmentRepository;
    private final MemberRepository memberRepository;
    private final ShiftTypeRepository shiftTypeRepository;

    public Candidate generateCandidate(Schedule schedule, Ward ward) {
        long start = System.currentTimeMillis();

        Candidate candidate = doGenerate(schedule, ward);

        long end = System.currentTimeMillis();
        log.info("generateCandidate took {} ms (scheduleId={})", (end - start), schedule.getId());

        return candidate;
    }


    @Transactional
    public Candidate doGenerate(Schedule schedule, Ward ward) {
        Candidate candidate = new Candidate(); // 하나만 만듦
        schedule.addCandidate(candidate);

        YearMonth ym = YearMonth.parse(schedule.getYearMonth());
        Map<Long, MemberState> memberStates = initMemberStates(ward.getId(), schedule.getId());
        Map<Long, Map<Integer, TeamState>> teamStates = initTeamStates(schedule.getId(), ward.getId());

        ShiftType off = shiftTypeRepository.findByWardIdAndIsWorkingFalse(ward.getId())
                .orElseThrow(() -> new IllegalStateException("OFF 타입이 존재하지 않습니다."));

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate today = LocalDate.of(ym.getYear(), ym.getMonth(), day);

            // 1. 의무 OFF 먼저 반영
            for (MemberState ms : memberStates.values()) {
                if (ms.getMandatoryOffRemain() > 0) {
                    assignOff(ms, candidate, today, off, false);
                    ms.updateMemberState(today, off);
                }
            }

            // 2. 팀 단위 근무 배정
            for (Team team : ward.getTeams()) {
                TeamState todayTeamState = teamStates.get(team.getId()).get(day);

                for (ShiftType shift : todayTeamState.getShiftTypes()) {
                    if (!shift.isWorking()) continue;
                    int need = todayTeamState.getRemain().get(shift);
                    if (need <= 0) continue;

                    List<MemberState> possibleMembers =
                            getPossibleMemberOfDay(memberStates, candidate, today, shift);

                    log.debug("Day={}, Shift={}, 후보 멤버={}", today, shift.getName(),
                            possibleMembers.stream().map(ms -> ms.getMemberId()).toList());

                    // 차지 필요할 경우 우선 배정
                    if (todayTeamState.needChargeFor(shift)) {
                        pickCharge(possibleMembers, candidate, today, shift, todayTeamState);
                        need--;
                    }

                    // 남은 자리 채우기
                    while (need > 0) {
                        pickBalancedCandidate(possibleMembers, candidate, today, shift, todayTeamState);
                        need--;
                    }
                }
            }
        }

        // ✅ 여기서 signature 생성
        String signature = generateSignature(candidate);

        if (candidateRepository.existsByScheduleIdAndSignature(schedule.getId(), signature)) {
            throw new IllegalStateException("중복 Candidate");
        }

        candidate.setSignature(signature);

        // ✅ candidate + assignments 함께 저장 (cascade=ALL 덕분에 assignments도 자동 저장)
        return candidateRepository.saveAndFlush(candidate);
    }




    private String generateSignature(Candidate candidate) {
        return candidate.getAssignments().stream() // List<CandidateAssignment>
                .sorted(Comparator.comparing(CandidateAssignment::getWorkDate)
                        .thenComparing(ca -> ca.getMember().getId())
                        .thenComparing(ca -> ca.getShiftType().getId())
                        .thenComparing(CandidateAssignment::isCharge))
                .map(ca -> ca.getWorkDate() + "-"
                        + ca.getMember().getId() + "-"
                        + ca.getShiftType().getId() + "-"
                        + ca.isCharge())
                .collect(Collectors.joining("|"));
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
            Optional<MemberState> match = possibleMembers.stream()
                    .filter(ms -> ms.getMemberId().equals(possibleMember.getId()))
                    .findFirst();

            if (match.isPresent()) {
                MemberState charge = match.get();
                assignWork(charge, candidate, today, shift, true, teamState);
                possibleMembers.remove(charge);
                return;
            }
        }

        // 🚑 fallback: 차지 후보가 없으면 그냥 일반 배정으로 대체
        if (!possibleMembers.isEmpty()) {
            MemberState fallback = possibleMembers.get(ThreadLocalRandom.current().nextInt(possibleMembers.size()));
            assignWork(fallback, candidate, today, shift, false, teamState); // 차지=false
            possibleMembers.remove(fallback);
            System.out.println("⚠️ " + today + " " + shift + " 차지 배정 실패 → 일반 근무자로 대체");
            return;
        }

        // 그래도 없으면 마지막에만 예외 던지기
        throw new IllegalArgumentException(today + "에 " + shift + " 차지 배정 불가 (fallback 실패)");
    }

    private void assignWork(MemberState memberState, Candidate candidate,
                            LocalDate today, ShiftType shiftType,
                            boolean isCharge, TeamState teamState) {
        memberState.updateMemberState(today, shiftType);
        Member member = memberRepository.findById(memberState.getMemberId()).orElseThrow();

        CandidateAssignment assignment =
                new CandidateAssignment(member, today, shiftType, isCharge);

        candidate.addAssignment(assignment); // cascade 덕분에 Candidate 저장 시 같이 저장됨
        teamState.apply(shiftType, member.getSkillGrade(), isCharge);
    }

    private void assignOff(MemberState memberState, Candidate candidate,
                           LocalDate today, ShiftType shiftType, boolean isCharge) {
        memberState.updateMemberState(today, shiftType);
        Member member = memberRepository.findById(memberState.getMemberId()).orElseThrow();

        CandidateAssignment assignment =
                new CandidateAssignment(member, today, shiftType, isCharge);

        candidate.addAssignment(assignment);
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
        Map<Long, MemberState> original = memberStateService.initMemberState(wardId, scheduleId);
        List<Long> keys = new ArrayList<>(original.keySet());
        Collections.shuffle(keys);

        Map<Long, MemberState> shuffled = new LinkedHashMap<>();
        for (Long key : keys) {
            shuffled.put(key, original.get(key));
        }

        return shuffled;
    }

    private Map<Long, Map<Integer, TeamState>> initTeamStates(Long scheduleId, Long wardId) {
        return teamStateService.initTeamState(scheduleId, wardId);
    }
}
