package com.davena.dutymaker.domain.service;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.service.HolidayService;
import com.davena.constraint.domain.service.UnavailShiftService;
import com.davena.dutymaker.application.dto.request.AssignScheduleRequest;
import com.davena.dutymaker.domain.model.MemberState;
import com.davena.dutymaker.domain.model.schedule.Cell;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberStateService {

    private final MemberService memberService;
    private final WardService wardService;
    private final HolidayService holidayService;
    private final UnavailShiftService unavailShiftService;
    private final ScheduleReadService scheduleReadService;

    private List<MemberState> initMemberState(AssignScheduleRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Map<UUID, List<Cell>> lastMonthCells = scheduleReadService.getLastMonthCells(request);

        List<MemberState> memberStates = new ArrayList<>();
        YearMonth thisMonth = YearMonth.of(request.year(), request.month());

        for (Map.Entry<UUID, List<Cell>> entry : lastMonthCells.entrySet()) {
            memberStates.add(sortLastMonthCells(ward, thisMonth, entry.getKey(), entry.getValue()));
        }
        return memberStates;
    }

    private MemberState sortLastMonthCells(Ward ward, YearMonth thisMonth, UUID memberId, List<Cell> cells) {
        LocalDate firstDateOfThisMonth = thisMonth.atDay(1);

        YearMonth lastMonth = thisMonth.minusMonths(1);
        LocalDate startDateOfLastMonth = firstDateOfThisMonth.minusDays(6);
        LocalDate lastDateOfLastMonth = firstDateOfThisMonth.minusDays(1);

        List<Cell> lastWeekCells = cells.stream()
                .filter(c -> {
                    LocalDate workDate = lastMonth.atDay(c.getWorkDay());
                    return !workDate.isBefore(startDateOfLastMonth) && !workDate.isAfter(lastDateOfLastMonth);
                })
                .sorted()
                .toList();
        return createMemberState(thisMonth, ward, memberId, lastWeekCells);
    }

    private MemberState createMemberState(YearMonth thisMonth, Ward ward, UUID memberId, List<Cell> lastWeekCells) {
        Member member = memberService.getMember(memberId);

        Set<UUID> possibleShiftIds = member.getShiftIdSet();
        Set<UUID> holidayRequests = holidayService.getMemberHolidaysByYearAndMonth(memberId, thisMonth.getYear(), thisMonth.getMonthValue());
        Set<UUID> unvailShiftRequests = unavailShiftService.getMemberUnavailRequestsByYearAndMonth(memberId, thisMonth.getYear(), thisMonth.getMonthValue());

        MemberState memberState = new MemberState(memberId, possibleShiftIds, holidayRequests, unvailShiftRequests);
        for (Cell cell : lastWeekCells) {
            Shift shift = ward.getShift(cell.getShiftId());
            memberState.updateMemberState(LocalDate.of(thisMonth.getYear(), thisMonth.getMonthValue(), cell.getWorkDay()), shift);
        }
        return memberState;
    }


}
