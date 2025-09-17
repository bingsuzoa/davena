package com.davena.organization.domain.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import com.davena.constraint.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WardMembersService {

    private final ExistenceService existenceCheck;
    private final MemberService memberService;
    private final WardRepository wardRepository;

    public static final String NOT_EXIST_WARD_BY_TOKEN = "입력하신 토큰을 가지는 병동이 존재하지 않습니다.";
    public static final String NOT_SUPERVISOR = "팀장이 아닌 사용자는 권한이 없습니다.";

    public WardResponse findWardByToken(String token) {
        Optional<Ward> optionalWard = wardRepository.findByToken(token);
        if (optionalWard.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_WARD_BY_TOKEN);
        }
        Ward ward = optionalWard.get();
        return new WardResponse(ward.getId(), ward.getName());
    }

    public JoinResponse applyForWard(JoinRequest request) {
        User user = existenceCheck.getUser(request.userId());
        Ward ward = existenceCheck.getWard(request.wardId());
        user.applyForWard(ward.getId());
        return new JoinResponse(user.getId(), user.getWardId(), ward.getName(), user.getStatus());
    }

    public JoinResponse acceptUserJoinRequest(JoinRequest request) {
        User user = existenceCheck.getUser(request.userId());
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        user.approveEnrollment(ward.getId());
        createMember(user, ward);
        return new JoinResponse(user.getId(), user.getWardId(), ward.getName(), user.getStatus());
    }

    private void createMember(User user, Ward ward) {
        if (!memberService.isAlreadyExistMember(user.getId())) {
            Member member = memberService.save(new Member(user.getId(), ward.getId(), user.getName()));
            member.initPossibleShifts(ward.getShifts());
        }
    }

    public JoinResponse rejectUserJoinRequest(JoinRequest request) {
        User user = existenceCheck.getUser(request.userId());
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        user.rejectEnrollment(ward.getId());
        return new JoinResponse(user.getId(), user.getWardId(), ward.getName(), user.getStatus());
    }
}
