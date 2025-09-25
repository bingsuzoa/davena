package com.davena.schedule.domain.service;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.constraint.domain.model.HolidayRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.PossibleShift;
import com.davena.constraint.domain.model.UnavailShiftRequest;
import com.davena.constraint.domain.port.HolidayRepository;
import com.davena.constraint.domain.port.UnavailShiftRepository;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.schedule.application.dto.GenerateRequest;
import com.davena.schedule.domain.model.MemberState;
import com.davena.schedule.domain.model.Schedule;
import com.davena.schedule.domain.model.canididate.Cell;
import com.davena.schedule.domain.port.CellRepository;
import com.davena.schedule.domain.port.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberStateService {

    private final MemberService memberService;
    private final WardService wardService;
    private final ScheduleRepository scheduleRepository;
    private final CellRepository cellRepository;
    private final UnavailShiftRepository unavailShiftRepository;
    private final HolidayRepository holidayRepository;

    public static final String NOT_EXIST_SCHEDULE = "해당 월의 스케줄이 존재하지 않습니다.";

    public List<MemberState> getMemberState(GenerateRequest request) {
        return initMemberState(request);
    }

    private List<MemberState> initMemberState(GenerateRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Map<UUID, List<Cell>> lastMonthCells = getLastMonthSchedule(request);

        YearMonth thisMonth = YearMonth.of(request.year(), request.month());
        YearMonth lastMonth = thisMonth.minusMonths(1);

        LocalDate firstDayOfThisMonth = thisMonth.atDay(1);
        LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);
        LocalDate lastWeekStart = lastDayOfLastMonth.minusDays(6);

        List<MemberState> memberStates = new ArrayList<>();

        for (Map.Entry<UUID, List<Cell>> entry : lastMonthCells.entrySet()) {
            UUID memberId = entry.getKey();
            List<Cell> cells = entry.getValue();

            List<Cell> lastWeekCells = cells.stream()
                    .filter(c -> {
                        LocalDate workDate = lastMonth.atDay(c.getWorkDay());
                        return !workDate.isBefore(lastWeekStart) && !workDate.isAfter(lastDayOfLastMonth);
                    })
                    .sorted()
                    .toList();

            Member member = memberService.getMember(memberId);
            Set<UUID> allowedShifts = member.getShifts().stream()
                    .map(PossibleShift::getShiftId)
                    .collect(Collectors.toSet());

            MemberState state = new MemberState(memberId, allowedShifts, getHolidaySetOfMember(member, request), getUnavailShiftSetOfMember(member, request));

            for (Cell cell : lastWeekCells) {
                LocalDate workDate = lastMonth.atDay(cell.getWorkDay());
                state.updateMemberState(workDate, ward.getShift(cell.getShiftId()));
            }
            memberStates.add(state);
        }
        return memberStates;
    }

    private Set<Integer> getHolidaySetOfMember(Member member, GenerateRequest request) {
        List<HolidayRequest> holidayRequests = holidayRepository.findByMemberIdAndYearAndMonth(member.getUserId(), request.year(), request.month());
        Set<Integer> holidays = new HashSet<>();

        for (HolidayRequest holidayRequest : holidayRequests) {
            holidays.add(holidayRequest.getRequestDay().getDayOfMonth());
        }
        return holidays;
    }

    private Map<Integer, Set<UUID>> getUnavailShiftSetOfMember(Member member, GenerateRequest request) {
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByMemberIdAndYearAndMonth(member.getUserId(), request.year(), request.month());
        Map<Integer, Set<UUID>> unavailShifts = new HashMap<>();

        for (UnavailShiftRequest unavailShiftRequest : unavailShiftRequests) {
            int day = unavailShiftRequest.getRequestDay().getDayOfMonth();
            unavailShifts.putIfAbsent(day, new HashSet<>());
            unavailShifts.get(day).add(unavailShiftRequest.getShiftId());
        }
        return unavailShifts;
    }

    private Map<UUID, List<Cell>> getLastMonthSchedule(GenerateRequest request) {
        Optional<Schedule> optionalLastMonthSchedule = scheduleRepository.getScheduleByWardIdAndYearAndMonth(request.wardId(), request.year(), request.month() - 1);

        if (optionalLastMonthSchedule.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_SCHEDULE);
        }

        List<Cell> cells = cellRepository.findByCandidateId(optionalLastMonthSchedule.get().getFinalizedCandidateId());
        Collections.sort(cells);
        return cells.stream()
                .collect(Collectors.groupingBy(Cell::getMemberId,
                        Collectors.toList()));
    }
}
