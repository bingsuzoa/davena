package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.grade.*;
import com.davena.organization.domain.service.GradeMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grade")
public class GradeMembersController {

    private final GradeMembersService gradeMembersService;

    @GetMapping("/users")
    public GradeMembersResponse getGradeMembers(@RequestBody GetGradeRequest request) {
        return gradeMembersService.getGradeMembers(request);
    }

    @PostMapping("/new")
    public GradeMembersResponse addGrade(@RequestBody CreateGradeRequest request) {
        return gradeMembersService.addNewGrade(request);
    }

    @DeleteMapping
    public GradeMembersResponse deleteGrade(@RequestBody DeleteGradeRequest request) {
        return gradeMembersService.deleteGrade(request);
    }

    @PutMapping("/users")
    public GradeMembersResponse updateGradeMembers(@RequestBody UpdateGradeMembersRequest request) {
        return gradeMembersService.updateWardGradeAssignments(request);
    }

}
