package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.*;
import com.davena.organization.application.dto.ward.grade.GradeRequest;
import com.davena.organization.application.dto.ward.grade.GradeResponse;
import com.davena.organization.application.dto.ward.shift.ShiftRequest;
import com.davena.organization.application.dto.ward.shift.ShiftResponse;
import com.davena.organization.application.dto.ward.team.TeamRequest;
import com.davena.organization.application.dto.ward.team.TeamResponse;
import com.davena.organization.domain.service.CreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization")
public class OrganizationController {
    private final CreateService createService;

    @PostMapping("/ward")
    public void createWard(@RequestBody WardRequest request) {
        WardResponse response = createService.createWard(request);
    }


    @PostMapping("/grade")
    public void addGrade(@RequestBody GradeRequest request) {
        GradeResponse response = createService.addNewGrade(request);
    }

    @PostMapping("/shift")
    public void addShift(@RequestBody ShiftRequest request) {
        ShiftResponse response = createService.addNewShift(request);
    }


}
