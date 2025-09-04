package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.*;
import com.davena.organization.domain.service.CreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization")
public class OrganizationController {
    private final CreateService createService;

    @PostMapping("/ward")
    public void createWard(@RequestBody WardRequest request) {
        WardResponse response = createService.createWard(request);
    }

    @PostMapping("/team")
    public void addTeam(@RequestBody TeamRequest request) {
        TeamResponse response = createService.addNewTeam(request);
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
