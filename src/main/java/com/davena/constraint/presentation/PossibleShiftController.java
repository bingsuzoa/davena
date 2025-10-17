package com.davena.constraint.presentation;

import com.davena.constraint.application.dto.possibleShifts.MemberPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.MemberPossibleShiftsRequest;
import com.davena.constraint.application.dto.possibleShifts.WardPossibleShiftsDto;
import com.davena.constraint.application.dto.possibleShifts.WardPossibleShiftsRequest;
import com.davena.constraint.domain.service.PossibleShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("possible-shifts")
public class PossibleShiftController {

    private final PossibleShiftService possibleShiftService;

    @GetMapping("/ward")
    public WardPossibleShiftsDto getWardPossibleShifts(@RequestBody WardPossibleShiftsRequest request) {
        return possibleShiftService.getWardPossibleShifts(request);
    }

    @PutMapping("/ward")
    public WardPossibleShiftsDto updateWardPossibleShifts(@RequestBody WardPossibleShiftsDto request) {
        return possibleShiftService.updateWardPossibleShifts(request);
    }
}
