package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.schedule.backfill.BackfillGrid;
import com.davena.dutymaker.api.dto.schedule.payload.draft.Draft;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftCell;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.schedule.Candidate;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BackfillService {

    private final WardRepository wardRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final DraftRepository draftRepository;
    private final TeamRepository teamRepository;
    private final CandidateRepository candidateRepository;

    @Transactional
    public BackfillGrid buildEmptyBackfillGrid(Long wardId, YearMonth targetYm) {
        YearMonth prev = targetYm.minusMonths(1);
        LocalDate end = prev.atEndOfMonth();
        LocalDate start = end.minusDays(6);

        Ward ward = wardRepository.findById(wardId).orElseThrow();

        String ymKey = String.format("%d-%02d", prev.getYear(), prev.getMonthValue());
        Schedule schedule = scheduleRepository.findByWardIdAndYearMonth(wardId, ymKey)
                .orElseGet(() -> scheduleRepository.save(new Schedule(ward, ymKey)));

        Map<Long, Map<Integer, DraftCell>> board = new HashMap<>();
        for (Member m : memberRepository.findMembersWithTeamByWardId(wardId)) {
            Team team = m.getTeam();
            Map<Integer, DraftCell> row = new HashMap<>();
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                int day = d.getDayOfMonth();
                row.put(day, new DraftCell(
                        m.getId(),
                        m.getName(),
                        team.getId(),
                        team.getName(),
                        day,
                        null,
                        null,
                        m.isCharge()
                ));
            }
            board.put(m.getId(), row);
        }
        DraftPayload payload = new DraftPayload(board);
        Draft draft = draftRepository.findByScheduleId(schedule.getId())
                .orElseGet(() -> new Draft(schedule));
        draft.updatePayload(payload);
        draftRepository.save(draft);
        return new BackfillGrid(schedule.getId(), payload);
    }

    public void updateDraft(Long scheduleId, DraftPayload payload) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        Draft draft = draftRepository.findByScheduleId(scheduleId).orElse(new Draft(schedule));
        draft.updatePayload(payload);
        draftRepository.save(draft);
    }

    public void applyInitialHistory(Long scheduleId, DraftPayload payload) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        Draft draft = draftRepository.findByScheduleId(scheduleId).orElse(new Draft(schedule));
        draft.updatePayload(payload);
        schedule.finalizeStatus(candidateRepository.save(new Candidate(schedule)));
        draftRepository.save(draft);
    }
}
