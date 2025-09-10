package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.shift.ShiftRequest;
import com.davena.organization.application.dto.ward.shift.WardShiftsResponse;
import com.davena.organization.domain.service.WardShiftsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shift")
public class WardShiftsController {

    private WardShiftsService wardShiftsService;

    @GetMapping
    public WardShiftsResponse getShifts(@RequestBody ShiftRequest request) {
        return wardShiftsService.getShifts(request);
    }

    @PostMapping("/new")
    public WardShiftsResponse addNewShift(@RequestBody ShiftRequest request) {
        return wardShiftsService.addNewShift(request);
    }

    @DeleteMapping
    public WardShiftsResponse deleteNewShift(@RequestBody ShiftRequest request) {
        return wardShiftsService.deleteShift(request);
    }

    @PutMapping
    public WardShiftsResponse updateShift(@RequestBody ShiftRequest request) {
        return wardShiftsService.updateShift(request);
    }
}
