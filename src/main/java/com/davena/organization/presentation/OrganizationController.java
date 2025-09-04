package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.WardRequest;
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
        createService.createWard(request);
    }


}
