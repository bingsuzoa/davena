package com.davena.organization.presentation;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.application.dto.ward.team.TeamMembersDto;
import com.davena.organization.domain.service.WardJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-setting")
public class UserSettingController {

    private final WardJoinService wardJoinService;

    @GetMapping("/wards/by-token")
    public void findWardByToken(@RequestParam String token) {
        WardResponse response = wardJoinService.findWardByToken(token);
    }

    @PostMapping("/users/apply")
    public void applyForWard(@RequestBody JoinRequest request) {
        JoinResponse response = wardJoinService.applyForWard(request);
    }

    @PostMapping("/users/approve")
    public void approveJoinRequest(@RequestBody JoinRequest request) {
        JoinResponse response = wardJoinService.approveJoinRequest(request);
    }

    @PostMapping("/users/reject")
    public void rejectJoinReqeust(@RequestBody JoinRequest request) {
        JoinResponse response = wardJoinService.rejectJoinRequest(request);
    }

    @PutMapping("/users/team")
    public void updateTeamMembers(@RequestBody TeamMembersDto teamMembersDto) {
        TeamMembersDto updatedTeamMembers = wardJoinService.updateMembersOfTeam(teamMembersDto);
    }

    @PatchMapping("/users/{id}/grade")
    public void patchGrade() {

    }

    @PatchMapping("/users/{id}/shifts")
    public void patchShifts() {

    }

    @PatchMapping("/users/{id}/charge")
    public void patchCharge() {

    }
}
