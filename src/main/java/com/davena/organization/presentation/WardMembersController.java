package com.davena.organization.presentation;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.service.WardMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ward")
public class WardMembersController {

    private final WardMembersService wardMembersService;

    @GetMapping("/by-token")
    public void findWardByToken(@RequestParam String token) {
        WardResponse response = wardMembersService.findWardByToken(token);
    }

    @PostMapping("/users/apply")
    public void applyForWard(@RequestBody JoinRequest request) {
        JoinResponse response = wardMembersService.applyForWard(request);
    }

    @PostMapping("/users/approve")
    public void approveJoinRequest(@RequestBody JoinRequest request) {
        JoinResponse response = wardMembersService.approveJoinRequest(request);
    }

    @PostMapping("/users/reject")
    public void rejectJoinReqeust(@RequestBody JoinRequest request) {
        JoinResponse response = wardMembersService.rejectJoinRequest(request);
    }

}
