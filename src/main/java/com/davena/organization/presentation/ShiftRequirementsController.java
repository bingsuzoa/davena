package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.shiftRequirement.RequirementRequest;
import com.davena.organization.application.dto.ward.shiftRequirement.RequirementsResponse;
import com.davena.organization.domain.service.RequirementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("requirement")
public class ShiftRequirementsController {

    private final RequirementsService requirementsService;

    @GetMapping
    public RequirementsResponse getRequirements(@RequestBody RequirementRequest request) {
        return requirementsService.getRequirements(request);
    }

    @PutMapping
    public RequirementsResponse updateRequirement(@RequestBody RequirementRequest request) {
        return requirementsService.updateRequirement(request);
    }
}
