package com.davena.dutymaker.domain.service;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.constraint.domain.model.Member;
import com.davena.dutymaker.application.dto.AssignmentDto;
import com.davena.dutymaker.application.dto.CandidateDto;
import com.davena.dutymaker.application.dto.GetScheduleRequest;
import com.davena.dutymaker.application.dto.ScheduleResponse;
import com.davena.dutymaker.domain.model.schedule.Candidate;
import com.davena.dutymaker.domain.model.schedule.Cell;
import com.davena.dutymaker.domain.model.schedule.Schedule;
import com.davena.dutymaker.domain.port.ScheduleRepository;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleReadService {

    private final ScheduleRepository scheduleRepository;
    private final MemberService memberService;
    private final WardService wardService;

    public ScheduleResponse getSchedule(GetScheduleRequest request) {
        Schedule schedule;
        if (request.scheduleId() == null) {
            schedule = scheduleRepository.saveSchedule(new Schedule(request.wardId(), request.year(), request.year()));
        } else {
            schedule = scheduleRepository.findById(request.scheduleId()).get();
        }
        return getScheduleResponse(schedule);
    }

    private ScheduleResponse getScheduleResponse(Schedule schedule) {
        int year = schedule.getYear();
        int month = schedule.getMonth();
        int lastDate = YearMonth.of(year, month).lengthOfMonth();

        Map<Integer, List<AssignmentDto>> dailyAssignments = new HashMap<>();
        for (int day = 1; day <= lastDate; day++) {
            dailyAssignments.put(day, new ArrayList<>());
        }

        List<CandidateDto> candidateDtos = new ArrayList<>();

        List<Candidate> candidates = schedule.getCandidates();
        if (candidates.isEmpty()) {
            candidateDtos.add(new CandidateDto(null, dailyAssignments));
            return new ScheduleResponse(schedule.getId(), year, month, lastDate, candidateDtos);
        }

        for (Candidate candidate : candidates) {
            candidateDtos.add(getCandidateDto(candidate, schedule));
        }
        return new ScheduleResponse(schedule.getId(), year, month, lastDate, candidateDtos);
    }

    private CandidateDto getCandidateDto(Candidate candidate, Schedule schedule) {
        int year = schedule.getYear();
        int month = schedule.getMonth();
        int lastDate = YearMonth.of(year, month).lengthOfMonth();

        Ward ward = wardService.getWard(schedule.getWardId());

        Map<Integer, List<AssignmentDto>> dailyAssignments = new HashMap<>();
        for (int day = 1; day <= lastDate; day++) {
            dailyAssignments.put(day, new ArrayList<>());
        }
        List<Cell> cells = candidate.getCells();
        if (cells.isEmpty()) {
            return new CandidateDto(candidate.getId(), dailyAssignments);
        }

        for (Cell cell : cells) {
            Member member = memberService.getMember(cell.getMemberId());
            Shift shift = ward.getShift(cell.getShiftId());
            dailyAssignments.get(cell.getWorkDay()).add(new AssignmentDto(cell.getId(), member.getName(), shift.getName()));
        }
        return new CandidateDto(candidate.getId(), dailyAssignments);
    }
}
