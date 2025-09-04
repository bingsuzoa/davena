package com.davena.organization.presentation;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-setting")
public class UserSettingController {

    private final JoinService joinService;

    @GetMapping("/wards/by-token")
    public void findWardByToken(@RequestParam String token) {
        WardResponse response = joinService.findWardByToken(token);
    }

    @PostMapping("/members/apply")
    public void applyForWard(@RequestBody JoinRequest request) {
        JoinResponse response = joinService.applyForWard(request);
    }

    @PostMapping("/members/approve")
    public void approveJoinRequest(@RequestBody JoinRequest request) {
        JoinResponse response = joinService.approveJoinRequest(request);
    }

    @PostMapping("/members/reject")
    public void rejectJoinReqeust(@RequestBody JoinRequest request) {
        JoinResponse response = joinService.rejectJoinRequest(request);
    }

    @PatchMapping("/members/{id}/team")
    public void patchTeam() {

    }

    @PatchMapping("/members/{id}/grade")
    public void patchGrade() {

    }

    @PatchMapping("/members/{id}/shifts")
    public void patchShifts() {

    }

    @PatchMapping("/members/{id}/charge")
    public void patchCharge() {

    }
}
