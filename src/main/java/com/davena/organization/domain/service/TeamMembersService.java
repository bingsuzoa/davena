package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserDto;
import com.davena.organization.application.dto.ward.team.TeamDto;
import com.davena.organization.application.dto.ward.team.TeamMembersRequest;
import com.davena.organization.application.dto.ward.team.TeamMembersResponse;
import com.davena.organization.application.dto.ward.team.TeamRequest;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.service.util.ExistenceService;
import com.davena.organization.domain.service.util.Mapper;
import com.davena.organization.domain.service.util.MembersValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMembersService {

    private final ExistenceService existenceCheck;
    private final MembersValidator membersValidator;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public TeamMembersResponse addNewTeam(TeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        ward.addNewTeam(request.name());
        return getTeamMembersDto(ward, mapper.getTeamUsers(ward.getTeams()));
    }

    public TeamMembersResponse deleteTeam(TeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        UUID teamId = request.teamId();
        ward.deleteTeam(teamId);
        return getTeamMembersDto(ward, mapper.getTeamUsers(ward.getTeams()));
    }

    public TeamMembersResponse getTeamMembers(TeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        return getTeamMembersDto(ward, mapper.getTeamUsers(ward.getTeams()));
    }

    public TeamMembersResponse updateMembersOfTeam(TeamMembersRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        membersValidator.validateAtLeastOneMember(request.usersOfTeam());
        membersValidator.validateContainAllMembers(ward, request.usersOfTeam());

        ward.clearAllTeamMembers();
        request.usersOfTeam().forEach(ward::setUsersToTeam);

        return getTeamMembersDto(ward, mapper.getTeamUsers(ward.getTeams()));
    }

    private TeamMembersResponse getTeamMembersDto(Ward ward, Map<TeamDto, List<UUID>> teamUsers) {
        List<UUID> allUserIds = teamUsers.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();

        Map<UUID, User> userMap = userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<TeamDto, List<UserDto>> teamUserDtos = teamUsers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(userMap::get)
                                .map(UserDto::from)
                                .toList()
                ));

        return new TeamMembersResponse(ward.getSupervisorId(), ward.getId(), teamUserDtos);
    }
}
