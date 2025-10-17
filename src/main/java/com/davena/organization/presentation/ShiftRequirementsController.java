package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.shiftRequirement.GetWardRequirementsRequest;
import com.davena.organization.application.dto.ward.shiftRequirement.WardRequirementsDto;
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
    public WardRequirementsDto getRequirements(@RequestBody GetWardRequirementsRequest request) {
        return requirementsService.getRequirements(request);
    }

    @PutMapping
    public WardRequirementsDto updateRequirement(@RequestBody WardRequirementsDto request) {
        return requirementsService.updateWardRequirements(request);
    }
}
