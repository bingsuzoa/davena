package com.davena.constraint.presentation;

import com.davena.constraint.application.dto.holidayRequest.*;
import com.davena.constraint.domain.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holiday")
public class HolidayController {

    private final HolidayService holidayService;


    @GetMapping("/ward")
    public WardHolidayResponse getWardHolidays(@RequestBody WardHolidayRequest request) {
        return holidayService.getWardHolidays(request);
    }

    @GetMapping("/member")
    public MemberHolidayResponse getMemberHolidays(@RequestBody MemberHolidayRequest request) {
        return holidayService.getMemberHolidays(request);
    }

    @PostMapping("/member")
    public MemberHolidayResponse addMemberHolidayRequest(@RequestBody CreateHolidayRequest request) {
        return holidayService.addMemberHoliday(request);
    }

    @DeleteMapping("/member")
    public MemberHolidayResponse deleteMemberHolidayRequest(@RequestBody DeleteHolidayRequest request) {
        return holidayService.deleteMemberHoliday(request);
    }
}
