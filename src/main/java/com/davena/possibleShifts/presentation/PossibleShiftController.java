package com.davena.possibleShifts.presentation;

import com.davena.possibleShifts.application.dto.possibleShifts.AllMembersPossibleShiftsDto;
import com.davena.possibleShifts.application.dto.possibleShifts.AllMembersPossibleShiftsRequest;
import com.davena.possibleShifts.domain.service.PossibleShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("possible-shifts")
public class PossibleShiftController {

    private final PossibleShiftService possibleShiftService;

    @GetMapping
    public AllMembersPossibleShiftsDto getAllMembersPossibleShifts(@RequestBody AllMembersPossibleShiftsRequest request) {
        return possibleShiftService.getAllMembersPossibleShifts(request);
    }

    @PutMapping
    public AllMembersPossibleShiftsDto updatePossibleShifts(@RequestBody AllMembersPossibleShiftsDto request) {
        return possibleShiftService.updateAllMembersPossibleShifts(request);
    }
}
