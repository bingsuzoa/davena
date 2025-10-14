package com.davena.dutymaker.domain.service;

import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.dutymaker.application.dto.AssignmentDto;
import com.davena.dutymaker.application.dto.ScheduleDto;
import com.davena.dutymaker.application.dto.request.CreateScheduleRequest;
import com.davena.dutymaker.application.dto.request.UpdateCellRequest;
import com.davena.dutymaker.domain.model.schedule.Candidate;
import com.davena.dutymaker.domain.model.schedule.Cell;
import com.davena.dutymaker.domain.model.schedule.Schedule;
import com.davena.dutymaker.domain.port.CandidateRepository;
import com.davena.dutymaker.domain.port.CellRepository;
import com.davena.dutymaker.domain.port.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleCreateService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleReadService scheduleReadService;
    private final CellRepository cellRepository;
    private final CandidateRepository candidateRepository;
    private final MemberService memberService;

    public ScheduleDto createNewSchedule(CreateScheduleRequest request) {
        int year = request.year();
        int month = request.month();
        Schedule schedule = scheduleRepository.saveSchedule(new Schedule(request.wardId(), year, month));
        Candidate candidate = candidateRepository.saveCandidate(new Candidate(schedule.getId()));

        int lastDate = YearMonth.of(year, month).lengthOfMonth();
        for (int day = 1; day <= lastDate; day++) {
            for (Member member : memberService.getAllMembersOfWard(request.wardId())) {
                candidate.addCell(cellRepository.saveCell(new Cell(candidate.getId(), member.getUserId(), day, null)));
            }
        }
        schedule.addCandidate(candidate);
        return scheduleReadService.getScheduleDto(schedule);
    }

    public ScheduleDto saveCustomSchedule(UpdateCellRequest request) {
        Schedule schedule = scheduleReadService.getScheduleById(request.scheduleId());

        for (Map.Entry<Integer, List<AssignmentDto>> entry: request.assignments().entrySet()) {
            for (AssignmentDto assignment : entry.getValue()) {
                schedule.updateCell(request.candidateId(), assignment.cellId(), assignment.shiftId());
            }
        }
        schedule.finalizeStatus();
        return scheduleReadService.getScheduleDto(schedule);
    }
}
