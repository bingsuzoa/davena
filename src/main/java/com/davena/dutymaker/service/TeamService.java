package com.davena.dutymaker.service;

import com.davena.dutymaker.domain.organization.Team;
import com.davena.dutymaker.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    Optional<Team> getTeam(Long teamId) {
        if (teamId == null) return Optional.empty();
        return teamRepository.findById(teamId);
    }

    Team saveTeam(Team team) {
        return teamRepository.save(team);
    }

    void deleteTeamOfWard(Long wardId) {
        teamRepository.deleteByWardId(wardId);
    }
}
