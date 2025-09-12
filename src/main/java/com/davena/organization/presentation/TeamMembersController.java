package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.team.TeamMembersRequest;
import com.davena.organization.application.dto.ward.team.TeamMembersResponse;
import com.davena.organization.application.dto.ward.team.TeamRequest;
import com.davena.organization.domain.service.TeamMembersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamMembersController {

    private final TeamMembersService teamMembersService;

    @GetMapping("/users")
    public TeamMembersResponse getTeamMembers(@RequestBody TeamRequest request) {
        return teamMembersService.getTeamMembers(request);
    }

    @PostMapping("/new")
    public TeamMembersResponse addTeam(@RequestBody TeamRequest request) {
        return teamMembersService.addNewTeam(request);
    }

    @DeleteMapping
    public TeamMembersResponse deleteTeam(@RequestBody TeamRequest request) {
        return teamMembersService.deleteTeam(request);
    }

    @PutMapping("/users")
    public TeamMembersResponse updateTeamMembers(@RequestBody TeamMembersRequest request) {
        return teamMembersService.updateTeamAssignments(request);
    }
}
