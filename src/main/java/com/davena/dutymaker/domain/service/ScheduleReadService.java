package com.davena.dutymaker.domain.service;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.dutymaker.application.dto.AssignmentDto;
import com.davena.dutymaker.application.dto.CandidateDto;
import com.davena.dutymaker.application.dto.ScheduleDto;
import com.davena.dutymaker.application.dto.request.AssignScheduleRequest;
import com.davena.dutymaker.application.dto.request.GetScheduleRequest;
import com.davena.dutymaker.domain.model.schedule.Candidate;
import com.davena.dutymaker.domain.model.schedule.Cell;
import com.davena.dutymaker.domain.model.schedule.Schedule;
import com.davena.dutymaker.domain.port.ScheduleRepository;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleReadService {

    private final ScheduleRepository scheduleRepository;
    private final MemberService memberService;
    private final WardService wardService;

    public static final String NOT_EXIST_SCHEDULE = "존재하지 않는 스케줄입니다.";

    public ScheduleDto getScheduleDto(GetScheduleRequest request) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(request.scheduleId());
        if (optionalSchedule.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_SCHEDULE);
        }
        return getScheduleDto(optionalSchedule.get());
    }

    public Schedule getScheduleById(UUID scheduleId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_SCHEDULE);
        }
        return optionalSchedule.get();
    }

    public Map<UUID, List<Cell>> getLastMonthCells(AssignScheduleRequest request) {
        int year = request.year();
        int month = request.month();

        if (month == 1) {
            year -= 1;
            month = 12;
        }
        Schedule lastMonthSchedule = getScheduleByYearAndMonth(request.wardId(), year, month);
        List<Cell> cells = lastMonthSchedule.getCellsOfFinalizedCandidate();
        Collections.sort(cells);
        return cells.stream()
                .collect(Collectors.groupingBy(Cell::getMemberId,
                        Collectors.toList()));
    }

    public Schedule getScheduleByYearAndMonth(UUID wardId, int year, int month) {
        Optional<Schedule> optionalSchedule = scheduleRepository.getScheduleByWardIdAndYearAndMonth(wardId, year, month);
        if (optionalSchedule.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_SCHEDULE);
        }
        return optionalSchedule.get();
    }

    public ScheduleDto getScheduleDto(Schedule schedule) {
        int year = schedule.getYear();
        int month = schedule.getMonth();
        int lastDate = YearMonth.of(year, month).lengthOfMonth();
        Ward ward = wardService.getWard(schedule.getWardId());

        List<CandidateDto> candidateDtos = new ArrayList<>();
        for (Candidate candidate : schedule.getCandidates()) {
            candidateDtos.add(getCandidateDto(candidate, schedule, ward));
        }
        return new ScheduleDto(ward.getId(), schedule.getId(), year, month, lastDate, candidateDtos);
    }

    private CandidateDto getCandidateDto(Candidate candidate, Schedule schedule, Ward ward) {
        int year = schedule.getYear();
        int month = schedule.getMonth();
        int lastDate = YearMonth.of(year, month).lengthOfMonth();

        Map<Integer, List<AssignmentDto>> dailyAssignments = new HashMap<>();
        for (int day = 1; day <= lastDate; day++) {
            dailyAssignments.put(day, new ArrayList<>());
        }
        List<Cell> cells = candidate.getCells();
        for (Cell cell : cells) {
            String memberName = cell.getMemberId() == null ? null : memberService.getMember(cell.getMemberId()).getName();
            String shiftName = cell.getShiftId() == null ? null : ward.getShift(cell.getShiftId()).getName();
            dailyAssignments.get(cell.getWorkDay()).add(new AssignmentDto(cell.getId(), cell.getMemberId(), cell.getShiftId(), memberName, shiftName));
        }
        return new CandidateDto(candidate.getId(), dailyAssignments);
    }
}
