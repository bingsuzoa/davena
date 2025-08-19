package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.schedule.AssignmentDto;
import com.davena.dutymaker.api.dto.schedule.RequirementRuleRequest;
import com.davena.dutymaker.api.dto.schedule.ScheduleView;
import com.davena.dutymaker.api.dto.schedule.payload.draft.Draft;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.api.dto.schedule.payload.finalized.FinalizedPayload;
import com.davena.dutymaker.api.dto.schedule.payload.generated.CandidateAssignmentView;
import com.davena.dutymaker.api.dto.schedule.payload.generated.GeneratedPayload;
import com.davena.dutymaker.domain.organization.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.schedule.Candidate;
import com.davena.dutymaker.domain.schedule.CandidateAssignments;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final WardRepository wardRepository;
    private final TeamRepository teamRepository;
    private final RequirementRuleRepository requirementRuleRepository;
    private final ShiftTypeRepository shiftRepository;
    private final ScheduleRepository scheduleRepository;
    private final DraftRepository draftRepository;

    public ScheduleView getScheduleView(Long wardId, YearMonth ym) {
        Schedule schedule = getOrCreateSchedule(wardId, ym);
        String wardName = getWard(wardId).getName();
        String targetMonth = ym.toString();

        return switch (schedule.getStatus()) {
            case FINALIZED -> {
                Candidate finalizedSchedule = scheduleRepository.findWithSelectedWithAssignments(schedule.getId())
                        .orElseThrow().getSelectedCandidate();
                List<AssignmentDto> assignmentDtos = finalizedSchedule.getAssignments().stream()
                        .sorted(Comparator.comparing(CandidateAssignments::getWorkDate)
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
        Optional<Schedule> optionalSchedule = scheduleRepository.findByWardIdAndYearMonth(wardId, targetMonth.toString());
        if (optionalSchedule.isEmpty()) {
            return scheduleRepository.save(new Schedule(getWard(wardId), targetMonth.toString()));
        }
        return optionalSchedule.get();
    }

    public void updateRequirementRule(Long wardId, RequirementRuleRequest ruleRequest) {
        Ward ward = getWard(wardId);
        Optional<Team> team = getTeam(ruleRequest.teamId());
        ShiftType shiftType = getShiftType(ruleRequest.shiftTypeId());
        DayType dayType = ruleRequest.dayType();
        int required = ruleRequest.required();
        RequirementRule rule;
        if (team.isEmpty()) {
            rule = RequirementRule.forWard(ward, dayType, shiftType, required);
        } else {
            rule = RequirementRule.forTeam(ward, team.get(), dayType, shiftType, required);
        }
        requirementRuleRepository.save(rule);
    }

    private Optional<Team> getTeam(Long teamId) {
        return teamRepository.findById(teamId);
    }

    private Ward getWard(Long wardId) {
        return wardRepository.findById(wardId).orElseThrow(() -> new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }

    private ShiftType getShiftType(Long shiftTypeId) {
        return shiftRepository.findById(shiftTypeId).orElseThrow(()
                -> new IllegalArgumentException(ShiftType.NOT_EXIST_SHIFT_TYPE));
    }
}
