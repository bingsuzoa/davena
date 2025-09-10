package com.davena.organization.presentation;

import com.davena.organization.application.dto.user.UserRequest;
import com.davena.organization.application.dto.user.UserResponse;
import com.davena.organization.application.dto.ward.WardRequest;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.service.CreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization")
public class CreateController {
    private final CreateService createService;

    @PostMapping("/ward")
    public void createWard(@RequestBody WardRequest request) {
        WardResponse response = createService.createWard(request);
    }

    @PostMapping("/user")
    public void createUser(@RequestBody UserRequest request) {
        UserResponse response = createService.createUser(request);
    }
}
