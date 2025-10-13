package com.davena.dutymaker.domain.service;

import com.davena.dutymaker.application.dto.GenerateRequest;
import com.davena.dutymaker.domain.model.schedule.Cell;
import com.davena.dutymaker.domain.model.schedule.Schedule;
import com.davena.dutymaker.domain.port.CellRepository;
import com.davena.dutymaker.domain.port.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CellRepository cellRepository;

    public static final String NOT_EXIST_SCHEDULE = "존재하지 않는 스케줄입니다.";

    public Map<UUID, List<Cell>> getLastMonthCells(GenerateRequest request) {
        int year = request.year();
        int month = request.month();

        if (month == 1) {
            year -= 1;
            month = 12;
        }
        Optional<Schedule> lastMonthSchedule = scheduleRepository.getScheduleByWardIdAndYearAndMonth(request.wardId(), year, month);
        if (lastMonthSchedule.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_SCHEDULE);
        }
        UUID lastMonthFinalizedCandidateId = lastMonthSchedule.get().getFinalizedCandidateId();
        List<Cell> cells = cellRepository.findByCandidateId(lastMonthFinalizedCandidateId);
        Collections.sort(cells);
        return cells.stream()
                .collect(Collectors.groupingBy(Cell::getMemberId,
                        Collectors.toList()));
    }
}
