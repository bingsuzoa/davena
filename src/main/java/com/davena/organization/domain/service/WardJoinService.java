package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.application.dto.ward.team.TeamMembersDto;
import com.davena.organization.domain.model.user.JoinStatus;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.TeamId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.model.ward.WardId;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final ExistenceService existenceCheck;
    private final WardRepository wardRepository;

    public static final String NOT_EXIST_WARD_BY_TOKEN = "입력하신 토큰을 가지는 병동이 존재하지 않습니다.";
    public static final String NOT_SUPERVISOR = "팀장이 아닌 사용자는 권한이 없습니다.";
    private static final String AT_LEAST_ONE_MEMBER_OF_TEAM = "팀에는 최소 한 명 이상의 멤버가 있어야 합니다.";

    public WardResponse findWardByToken(String token) {
        Optional<Ward> optionalWard = wardRepository.findByToken(token);
        if (optionalWard.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_WARD_BY_TOKEN);
        }
        return WardResponse.from(optionalWard.get());
    }

    public JoinResponse applyForWard(JoinRequest request) {
        User user = existenceCheck.getUser(new UserId(request.userId()));
        Ward ward = existenceCheck.getWard(new WardId(request.wardId()));
        user.applyForWard(ward.getId());
        return JoinResponse.from(user.getId(), user.getWardId(), ward.getName(), user.getStatus());
    }

    public JoinResponse approveJoinRequest(JoinRequest request) {
        User user = existenceCheck.getUser(new UserId(request.userId()));
        Ward ward = existenceCheck.getWard(new WardId(request.wardId()));
        existenceCheck.verifySupervisor(ward, new UserId(request.supervisorId()));
        user.approveEnrollment(ward.getId());
        ward.addNewUser(user.getId());
        return JoinResponse.from(user.getId(), user.getWardId(), ward.getName(), user.getStatus());
    }

    public JoinResponse rejectJoinRequest(JoinRequest request) {
        User user = existenceCheck.getUser(new UserId((request.userId())));
        Ward ward = existenceCheck.getWard(new WardId(request.wardId()));
        existenceCheck.verifySupervisor(ward, new UserId(request.supervisorId()));
        user.rejectEnrollment(ward.getId());
        return JoinResponse.from(user.getId(), ward.getId(), ward.getName(), JoinStatus.REJECTED);
    }

    public TeamMembersDto updateMembersOfTeam(TeamMembersDto teamMembersDto) {
        Ward ward = existenceCheck.getWard(new WardId(teamMembersDto.wardId()));
        existenceCheck.verifySupervisor(ward, new UserId(teamMembersDto.supervisorId()));
        Map<UUID, List<UUID>> raw = teamMembersDto.usersOfTeam();

        Map<TeamId, List<UserId>> converted = raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> new TeamId(e.getKey()),
                        e -> e.getValue().stream()
                                .map(UserId::new)
                                .toList()
                ));

        for(TeamId teamId : converted.keySet()) {
            validateAtLeastOneMember(converted.get(teamId));
            ward.assignUserToTeam(teamId, converted.get(teamId));
        }
        return TeamMembersDto.from(ward.getSupervisorId(), ward.getId(), ward.getTeamUsers());
    }

    private void validateAtLeastOneMember(List<UserId> users) {
        if(users.isEmpty()) {
            throw new IllegalArgumentException(AT_LEAST_ONE_MEMBER_OF_TEAM);
        }
    }


}
