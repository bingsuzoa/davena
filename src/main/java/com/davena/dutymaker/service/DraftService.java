package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.schedule.payload.draft.Draft;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftCell;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.domain.Request;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DraftService {

    private final DraftRepository draftRepository;
    private final ScheduleRepository scheduleRepository;
    private final RequestRepository requestRepository;
    private final WardRepository wardRepository;
    private final MemberRepository memberRepository;
    private final ShiftTypeRepository shiftTypeRepository;

    public DraftPayload getDraft(Long scheduleId) {
        return draftRepository.findByScheduleId(scheduleId)
                .map(Draft::getPayload)
                .orElseGet(() -> buildInitialPayload(scheduleId));
    }

    public void updateDraft(Long scheduleId, DraftPayload payload) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        Draft draft = draftRepository.findByScheduleId(scheduleId)
                .orElse(new Draft(schedule));
        draft.updatePayload(payload);
        draftRepository.save(draft);
    }

    private DraftPayload buildInitialPayload(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow();
        YearMonth thisMonth = YearMonth.parse(schedule.getYearMonth());
        LocalDate monthStart = thisMonth.atDay(1);
        LocalDate monthEnd = thisMonth.atEndOfMonth();

        List<Request> requests = requestRepository.findRequestsByWardAndMonth(getWard(schedule).getId(), monthStart, monthEnd);

        Map<Long, Map<Integer, DraftCell>> board = new HashMap<>();

        for(Request request : requests) {
            Long memberId = request.getId();
            String memberName = memberRepository.findById(memberId).orElseThrow().getName();
            LocalDate start = request.getStartDate().isBefore(monthStart) ? monthStart : request.getStartDate();
            LocalDate end   = request.getEndDate().isAfter(monthEnd) ? monthEnd : request.getEndDate();
            ShiftType shift = request.getShiftType();

            Map<Integer, DraftCell> row = board.computeIfAbsent(memberId, k -> new HashMap<>());
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                int day = d.getDayOfMonth();
                row.put(day, new DraftCell(memberId, memberName, day, shift.getId(), shift.getName()));
            }
        }
        return new DraftPayload(board);
    }

    private Ward getWard(Schedule schedule) {
        return wardRepository.findByScheduleId(schedule.getId()).orElseThrow();
    }
}
