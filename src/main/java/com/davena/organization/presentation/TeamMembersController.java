package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.team.TeamMembersDto;
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
    public TeamMembersDto getTeamMembers(@RequestBody TeamRequest request) {
        return teamMembersService.getTeamMembers(request);
    }

    @PostMapping("/new")
    public TeamMembersDto addTeam(@RequestBody TeamRequest request) {
        return teamMembersService.addNewTeam(request);
    }

    @DeleteMapping
    public TeamMembersDto deleteTeam(@RequestBody TeamRequest request) {
        return teamMembersService.deleteTeam(request);
    }

    @PutMapping("/users")
    public TeamMembersDto updateTeamMembers(@RequestBody TeamMembersDto teamMembersDto) {
        return teamMembersService.updateMembersOfTeam(teamMembersDto);
    }
}
