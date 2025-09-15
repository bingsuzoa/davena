package com.davena.constraint.presentation;

import com.davena.constraint.application.dto.shiftRequest.*;
import com.davena.constraint.domain.service.UnavailShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/unavail-shift")
public class UnavailShiftController {

    private final UnavailShiftService unavailShiftService;

    @GetMapping("/ward")
    public WardUnavailShiftResponse getWardUnavailShifts(@RequestBody WardUnavailShiftRequest request) {
        return unavailShiftService.getWardUnavailShiftRequests(request);
    }

    @GetMapping("/ward")
    public MemberUnavailShiftsResponse getMemberUnavailShifts(@RequestBody MemberUnavailShiftRequest request) {
        return unavailShiftService.getMemberUnavailShiftRequest(request);
    }

    @PostMapping("/member")
    public MemberUnavailShiftsResponse addMemberUnavailShifts(@RequestBody CreateShiftRequest request) {
        return unavailShiftService.addMemberUnavailShift(request);
    }

    @DeleteMapping("/member")
    public MemberUnavailShiftsResponse deleteMemberUnavailShifts(@RequestBody DeleteShiftRequest request) {
        return unavailShiftService.deleteMemberUnavailShift(request);
    }
}
