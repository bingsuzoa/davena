package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.team.*;
import com.davena.organization.domain.service.TeamMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamMembersController {

    private final TeamMembersService teamMembersService;

    @GetMapping("/users")
    public TeamMembersResponse getTeamMembers(@RequestBody GetTeamRequest request) {
        return teamMembersService.getTeamMembers(request);
    }

    @PostMapping("/new")
    public TeamMembersResponse addTeam(@RequestBody CreateTeamRequest request) {
        return teamMembersService.addNewTeam(request);
    }

    @DeleteMapping
    public TeamMembersResponse deleteTeam(@RequestBody DeleteTeamRequest request) {
        return teamMembersService.deleteTeam(request);
    }

    @PutMapping("/users")
    public TeamMembersResponse updateTeamMembers(@RequestBody UpdateTeamMembersRequest request) {
        return teamMembersService.updateTeamAssignments(request);
    }
}
