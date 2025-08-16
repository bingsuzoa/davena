package com.davena.dutymaker.api.controller;

import com.davena.dutymaker.api.dto.TeamDistributionRequest;
import com.davena.dutymaker.service.TeamDistributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamDistributionController {

    private final TeamDistributionService teamDistributionService;

    @PutMapping("/{wardId}/distribution")
    public void distributeTeams(@PathVariable Long wardId, @Valid @RequestBody TeamDistributionRequest request) {
        teamDistributionService.updateTeamDistribution(wardId, request);
    }
}
