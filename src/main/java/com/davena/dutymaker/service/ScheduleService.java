package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.schedule.AssignmentDto;
import com.davena.dutymaker.api.dto.schedule.ScheduleView;
import com.davena.dutymaker.api.dto.schedule.payload.draft.Draft;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.api.dto.schedule.payload.finalized.FinalizedPayload;
import com.davena.dutymaker.api.dto.schedule.payload.generated.CandidateAssignmentView;
import com.davena.dutymaker.api.dto.schedule.payload.generated.GeneratedPayload;
import com.davena.dutymaker.api.dto.schedule.requirement.RequirementRequest;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.schedule.Candidate;
import com.davena.dutymaker.domain.schedule.CandidateAssignment;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final WardRepository wardRepository;
    private final TeamRepository teamRepository;
    private final RequirementRuleRepository requirementRuleRepository;
    private final ShiftTypeRepository shiftRepository;
    private final ScheduleRepository scheduleRepository;
    private final DraftRepository draftRepository;
    private final DraftService draftService;

    public ScheduleView getScheduleView(Long wardId, YearMonth ym) {
        Schedule schedule = getOrCreateSchedule(wardId, ym);
        String wardName = getWard(wardId).getName();
        String targetMonth = ym.toString();

        return switch (schedule.getStatus()) {
            case FINALIZED -> {
                Candidate finalizedSchedule = scheduleRepository.findWithSelectedWithAssignments(schedule.getId())
                        .orElseThrow().getSelectedCandidate();
                List<AssignmentDto> assignmentDtos = finalizedSchedule.getAssignments().stream()
                        .sorted(Comparator.comparing(CandidateAssignment::getWorkDate)
                                .thenComparing(a -> a.getMember().getName()))
                        .map(a -> new AssignmentDto(
                                a.getWorkDate(),
                                a.getMember().getId(),
                                a.getMember().getName(),
                                a.getShiftType().getName()))
                        .toList();

                FinalizedPayload payload = new FinalizedPayload(
                        finalizedSchedule.getId(),
                        assignmentDtos
                );
                yield ScheduleView.finalized(schedule.getId(), wardName, targetMonth, payload);
            }
            case GENERATED -> {
                List<CandidateAssignmentView> candidates =
                        scheduleRepository.findWithCandidatesAndAssignments(schedule.getId())
                                .orElseThrow().getCandidates().stream()
                                .map(c -> new CandidateAssignmentView(
                                        c.getId(),
                                        c.getAssignments().stream()
                                                .map(a -> new AssignmentDto(
                                                        a.getWorkDate(),
                                                        a.getMember().getId(),
                                                        a.getMember().getName(),
                                                        a.getShiftType().getName()
                                                ))
                                                .toList()
                                ))
                                .toList();
                Long selectedId = schedule.getSelectedCandidate() != null
                        ? schedule.getSelectedCandidate().getId()
                        : null;

                GeneratedPayload payload = new GeneratedPayload(candidates);
                yield ScheduleView.generated(schedule.getId(), wardName, targetMonth, selectedId, payload);
            }
            case DRAFT -> {
                Draft draft = draftRepository.findByScheduleId(schedule.getId())
                        .orElseThrow();

                DraftPayload payload = draft.getPayload();
                yield ScheduleView.draft(schedule.getId(), wardName, targetMonth, payload);
            }
            case GENERATING -> {
                GeneratedPayload payload = new GeneratedPayload(List.of());
                yield ScheduleView.generated(schedule.getId(), wardName, targetMonth, null, payload);
            }
        };
    }


    private Schedule getOrCreateSchedule(Long wardId, YearMonth targetMonth) {
        String ym = String.format("%d-%02d", targetMonth.getYear(), targetMonth.getMonthValue());
        return scheduleRepository.findByWardIdAndYearMonth(wardId, ym)
                .orElseGet(() -> {
                    Schedule s = scheduleRepository.save(new Schedule(getWard(wardId), ym));
                    draftService.getDraft(s.getId());
                    return s;
                });
    }

    public void updateRule(Long wardId, RequirementRequest ruleRequest) {
        Ward ward = getWard(wardId);
        Team team = getTeam(ruleRequest.teamId());

        Map<DayType, Map<ShiftType, Integer>> rules = ruleRequest.requirementBox();
        for (DayType dayType : rules.keySet()) {
            updateRuleOfTeam(rules.get(dayType), dayType, team);
        }
    }

    private void updateRuleOfTeam(Map<ShiftType, Integer> rule, DayType dayType, Team team) {
        for (ShiftType shiftType : rule.keySet()) {
            int required = rule.get(shiftType);
            requirementRuleRepository.save(new RequirementRule(team, dayType, shiftType, required));
        }
    }

    private Team getTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(() ->
                new IllegalArgumentException(Team.NOT_EXIST_TEAM));
    }

    private Ward getWard(Long wardId) {
        return wardRepository.findById(wardId).orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }

    private ShiftType getShiftType(Long shiftTypeId) {
        return shiftRepository.findById(shiftTypeId).orElseThrow(()
                -> new IllegalArgumentException(ShiftType.NOT_EXIST_SHIFT_TYPE));
    }
}
