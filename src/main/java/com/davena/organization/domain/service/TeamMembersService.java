package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserDto;
import com.davena.organization.application.dto.ward.team.TeamDto;
import com.davena.organization.application.dto.ward.team.TeamMembersDto;
import com.davena.organization.application.dto.ward.team.TeamRequest;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMembersService {

    private final ExistenceService existenceCheck;
    private final UserRepository userRepository;

    public static final String AT_LEAST_ONE_MEMBER_OF_TEAM = "팀에는 최소 한 명 이상의 멤버가 있어야 합니다.";
    public static final String NOT_CONTAINS_ALL_MEMBER = "팀을 구성할 시 병동의 모든 인원을 대상으로 해야 합니다.";

    public TeamMembersDto addNewTeam(TeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        ward.addNewTeam(request.name());
        return getTeamMembersDto(ward, ward.getTeamUsers());
    }

    public TeamMembersDto deleteTeam(TeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        UUID teamId = request.teamId();
        ward.deleteTeam(teamId);
        return getTeamMembersDto(ward, ward.getTeamUsers());
    }

    public TeamMembersDto getTeamMembers(TeamRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        return getTeamMembersDto(ward, ward.getTeamUsers());
    }

    public TeamMembersDto updateMembersOfTeam(TeamMembersDto teamMembersDto) {
        Ward ward = existenceCheck.getWard(teamMembersDto.wardId());
        existenceCheck.verifySupervisor(ward, teamMembersDto.supervisorId());
        validateAtLeastOneMember(teamMembersDto);
        validateContainAllMembers(ward, teamMembersDto);

        ward.clearAllTeamMembers();

        teamMembersDto.usersOfTeam().forEach((teamDto, userDtos) -> {
            UUID teamId = teamDto.id();
            List<UUID> userIds = userDtos.stream()
                    .map(UserDto::id)
                    .toList();

            ward.setUsersToTeam(teamId, userIds);
        });

        return getTeamMembersDto(ward, ward.getTeamUsers());
    }

    private void validateAtLeastOneMember(TeamMembersDto teamMembersDto) {
        Map<TeamDto, List<UserDto>> teamMembers = teamMembersDto.usersOfTeam();
        for (List<UserDto> users : teamMembers.values()) {
            if (users.isEmpty()) {
                throw new IllegalArgumentException(AT_LEAST_ONE_MEMBER_OF_TEAM);
            }
        }
    }

    private void validateContainAllMembers(Ward ward, TeamMembersDto teamMembersDto) {
        Set<UUID> dtoMembers = teamMembersDto.usersOfTeam().values().stream()
                .flatMap(List::stream)
                .map(UserDto::id)
                .collect(Collectors.toSet());

        Set<UUID> wardMembers = ward.getUsers();

        if (!dtoMembers.equals(wardMembers)) {
            throw new IllegalArgumentException(NOT_CONTAINS_ALL_MEMBER);
        }
    }

    private TeamMembersDto getTeamMembersDto(Ward ward, Map<TeamDto, List<UUID>> teamUsers) {
        List<UUID> allUserIds = teamUsers.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();

        List<User> users = userRepository.findAllById(allUserIds);
        Map<UUID, User> userMap = users.stream()
                .collect(Collectors.toMap(u -> u.getId(), u -> u));

        Map<TeamDto, List<UserDto>> teamUserDtos = teamUsers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(uid -> {
                                    User u = userMap.get(uid);
                                    return new UserDto(u.getId(), u.getName());
                                })
                                .toList()
                ));
        return new TeamMembersDto(ward.getSupervisorId(), ward.getId(), teamUserDtos);
    }
}
