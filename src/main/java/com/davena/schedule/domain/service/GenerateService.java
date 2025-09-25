package com.davena.schedule.domain.service;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Team;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.schedule.application.dto.GenerateRequest;
import com.davena.schedule.domain.model.MemberState;
import com.davena.schedule.domain.model.ShiftState;
import com.davena.schedule.domain.model.TeamState;
import com.davena.schedule.domain.model.canididate.Candidate;
import com.davena.schedule.domain.model.canididate.Cell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateService {

    private final MemberStateService memberStateService;
    private final TeamStateService teamStateService;
    private final MemberService memberService;
    private final WardService wardService;
    private final HardPolicy hardPolicy;

    private static final int MAX_RESULTS = 5;
    private static final int BEAM_WIDTH = 200; // 후보 폭 제한

    public List<Candidate> generate(GenerateRequest request) {
        long start = System.nanoTime();

        List<MemberState> memberStates = memberStateService.getMemberState(request);
        Collections.shuffle(memberStates); // 랜덤성 부여

        YearMonth ym = YearMonth.of(request.year(), request.month());
        List<Candidate> candidates = List.of(new Candidate(request.scheduleId()));

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate today = ym.atDay(day);
            List<TeamState> teamStates = teamStateService.initTeamStates(request, day);

            List<Candidate> next = new ArrayList<>();
            for (Candidate base : candidates) {
                expandDay(today, base, memberStates, teamStates, request, next);
            }

            // 후보가 너무 많으면 무작위 섞고 제한
            Collections.shuffle(next);
            if (next.size() > BEAM_WIDTH) {
                next = next.subList(0, BEAM_WIDTH);
            }
            candidates = next;
        }

        // 중복 제거 + 최대 5개
        Set<String> seen = new HashSet<>();
        List<Candidate> results = candidates.stream()
                .filter(c -> seen.add(getSignature(c)))
                .limit(MAX_RESULTS)
                .toList();

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        log.info("""
                Generate finished
                  - Time: {} ms
                  - WardId: {}
                  - Month: {}
                  - TotalCandidates: {}
                  - UniqueResults: {}
                """,
                elapsedMs,
                request.wardId(),
                ym,
                candidates.size(),
                results.size()
        );

        return results;
    }

    /** 하루 단위 확장 */
    private void expandDay(LocalDate today, Candidate base,
                           List<MemberState> memberStates,
                           List<TeamState> teamStates,
                           GenerateRequest request,
                           List<Candidate> out) {

        List<Candidate> dayCandidates = List.of(base);

        for (Team team : wardService.getWard(request.wardId()).getTeams()) {
            List<Candidate> nextTeamCandidates = new ArrayList<>();
            for (Shift shift : wardService.getWard(request.wardId()).getShifts()) {
                for (Candidate candidate : dayCandidates) {
                    List<Candidate> expanded = assignShift(today, team, shift, candidate, memberStates, teamStates);
                    nextTeamCandidates.addAll(expanded);
                }
            }
            dayCandidates = nextTeamCandidates;
        }

        out.addAll(dayCandidates);
    }

    private List<Candidate> assignShift(LocalDate today, Team team, Shift shift,
                                        Candidate baseCandidate, List<MemberState> memberStates,
                                        List<TeamState> teamStates) {
        List<Candidate> newCandidates = new ArrayList<>();

        TeamState teamState = findTeamState(teamStates, team.getId());
        ShiftState shiftState = teamState.getShiftStates().get(shift.getId());

        if (shiftState == null || shiftState.getRemain() <= 0) {
            return List.of(baseCandidate);
        }

        // 1. 기본 필터
        List<MemberState> baseCandidates = memberStates.stream()
                .filter(m -> m.isPossibleShift(today.getDayOfMonth(), shift.getId()))
                .toList();

        // 2. HardPolicy
        List<MemberState> possible = baseCandidates.stream()
                .filter(m -> hardPolicy.canAssign(m, today, shift))
                .toList();

        // 3. charge 우선
        if (!shiftState.isHasCharge()) {
            for (UUID chargeId : teamState.getChargeOrder()) {
                MemberState ms = findMember(possible, chargeId);
                if (ms != null) {
                    Candidate copy = baseCandidate.copy();
                    Member member = memberService.getMember(ms.getMemberId());
                    assign(copy, today, teamState.copy(), shiftState.copy(), ms.copy(), member, shift, true);
                    newCandidates.add(copy);
                    break;
                }
            }
            return newCandidates.isEmpty() ? List.of(baseCandidate) : newCandidates;
        }

        // 4. grade balance → 완화 버전
        int minGradeCount = shiftState.getAppliedGrade().values().stream()
                .min(Integer::compare)
                .orElse(0);

        List<MemberState> balanced = possible.stream()
                .filter(m -> {
                    Member member = memberService.getMember(m.getMemberId());
                    int count = shiftState.getAppliedGrade().getOrDefault(member.getGradeId(), 0);
                    return count <= minGradeCount + 1; // 최소 +1까지 허용
                })
                .toList();

        List<MemberState> candidatesForShift = balanced.isEmpty() ? possible : balanced;
        List<MemberState> randomized = new ArrayList<>(candidatesForShift);
        Collections.shuffle(randomized);

        for (MemberState ms : randomized) {
            Candidate copy = baseCandidate.copy();
            Member member = memberService.getMember(ms.getMemberId());
            assign(copy, today, teamState.copy(), shiftState.copy(), ms.copy(), member, shift, false);
            newCandidates.add(copy);
        }

        return newCandidates.isEmpty() ? List.of(baseCandidate) : newCandidates;
    }

    private void assign(Candidate candidate, LocalDate today, TeamState teamState,
                        ShiftState shiftState, MemberState memberState,
                        Member member, Shift shift, boolean isCharge) {

        candidate.addCell(new Cell(UUID.randomUUID(), candidate.getId(),
                member.getUserId(), today.getDayOfMonth(), shift.getId()));

        memberState.updateMemberState(today, shift);
        shiftState.updateRemain();
        shiftState.getAppliedGrade().merge(member.getGradeId(), 1, Integer::sum);
        if (isCharge) shiftState.setHasCharge(true);
    }

    private TeamState findTeamState(List<TeamState> teamStates, UUID teamId) {
        return teamStates.stream()
                .filter(t -> t.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow();
    }

    private MemberState findMember(List<MemberState> memberStates, UUID memberId) {
        return memberStates.stream()
                .filter(m -> m.getMemberId().equals(memberId))
                .findFirst()
                .orElse(null);
    }

    private String getSignature(Candidate candidate) {
        return candidate.getCells().stream()
                .sorted(Comparator.comparing(Cell::getWorkDay)
                        .thenComparing(Cell::getShiftId)
                        .thenComparing(Cell::getMemberId))
                .map(c -> c.getWorkDay() + "-" + c.getShiftId() + "-" + c.getMemberId())
                .collect(Collectors.joining("|"));
    }
}
