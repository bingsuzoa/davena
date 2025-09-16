package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.shift.CreateShiftRequest;
import com.davena.organization.application.dto.ward.shift.DeleteShiftRequest;
import com.davena.organization.application.dto.ward.shift.GetShiftRequest;
import com.davena.organization.application.dto.ward.shift.WardShiftsDto;
import com.davena.organization.domain.service.WardShiftsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shift")
public class WardShiftsController {

    private WardShiftsService wardShiftsService;

    @GetMapping
    public WardShiftsDto getWardShifts(@RequestBody GetShiftRequest request) {
        return wardShiftsService.getShifts(request);
    }

    @PostMapping("/new")
    public WardShiftsDto addNewShift(@RequestBody CreateShiftRequest request) {
        return wardShiftsService.addNewShift(request);
    }

    @DeleteMapping
    public WardShiftsDto deleteNewShift(@RequestBody DeleteShiftRequest request) {
        return wardShiftsService.deleteShift(request);
    }

    @PutMapping
    public WardShiftsDto updateShift(@RequestBody WardShiftsDto request) {
        return wardShiftsService.updateShift(request);
    }
}
