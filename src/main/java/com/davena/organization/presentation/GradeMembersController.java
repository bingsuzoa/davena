package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.grade.GradeMembersRequest;
import com.davena.organization.application.dto.ward.grade.GradeMembersResponse;
import com.davena.organization.application.dto.ward.grade.GradeRequest;
import com.davena.organization.domain.service.GradeMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grade")
public class GradeMembersController {

    private final GradeMembersService gradeMembersService;

    @GetMapping("/users")
    public GradeMembersResponse getGradeMembers(@RequestBody GradeRequest request) {
        return gradeMembersService.getGradeMembers(request);
    }

    @PostMapping("/new")
    public GradeMembersResponse addGrade(@RequestBody GradeRequest request) {
        return gradeMembersService.addNewGrade(request);
    }

    @DeleteMapping
    public GradeMembersResponse deleteGrade(@RequestBody GradeRequest request) {
        return gradeMembersService.deleteGrade(request);
    }

    @PutMapping("/users")
    public GradeMembersResponse updateGradeMembers(@RequestBody GradeMembersRequest request) {
        return gradeMembersService.updateWardGradeAssignments(request);
    }

}
