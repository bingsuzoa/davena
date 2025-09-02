package com.davena.organization.presentation;

import com.davena.organization.application.dto.WardRequest;
import com.davena.organization.domain.service.CreateWardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WardController {
    private final CreateWardService createWardService;

    @PostMapping("/ward")
    public void createWard(@RequestBody WardRequest request) {
        createWardService.createWard(request);
    }
}
