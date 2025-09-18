package com.davena.organization.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.PossibleShift;
import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.user.JoinStatus;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.WardRepository;
import com.davena.organization.domain.service.WardMembersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.davena.organization.domain.service.WardMembersService.ALREADY_EXIST_WARD_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WardMembersServiceTest {

    @Mock
    private ExistenceService existenceCheck;

    @Mock
    private WardRepository wardRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private WardMembersService wardMembersService;

    @Test
    @DisplayName("입력한 토큰과 일치하는 병동이 있으면 반환")
    void findWardByToken() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(wardRepository.findByToken(any())).thenReturn(Optional.of(ward));
        WardResponse response = wardMembersService.findWardByToken(ward.getToken());
        Assertions.assertEquals(ward.getId(), response.wardId());
    }

    @Test
    @DisplayName("병동 가입 신청 시 USER 상태 PENDING으로 변환")
    void applyForWard() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        JoinResponse response = wardMembersService.applyForWard(new JoinRequest(user.getId(), supervisorId, ward.getId()));
        Assertions.assertEquals(response.status(), JoinStatus.PENDING);
    }

    @Test
    @DisplayName("병동 가입 승인 시 USER 상태 APPROVED")
    void acceptUserJoinRequest() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(memberService.isAlreadyExistMember(any())).thenReturn(false);

        Member member = new Member(user.getId(), ward.getId(), user.getName());
        when(memberService.save(any())).thenReturn(member);
        JoinResponse response = wardMembersService.acceptUserJoinRequest(new JoinRequest(user.getId(), supervisorId, ward.getId()));
        Assertions.assertEquals(user.getStatus(), JoinStatus.APPROVE);
        Assertions.assertEquals(response.wardId(), ward.getId());
    }

    @Test
    @DisplayName("병동 가입 승인 시 Member객체 생성 + ward의 shifts 갖는지 확인, member는 기본 팀 배정 확인")
    void createMember() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        Member member = new Member(user.getId(), ward.getId(), user.getName());

        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(memberService.isAlreadyExistMember(any())).thenReturn(false);
        when(memberService.save(any())).thenReturn(member);
        wardMembersService.acceptUserJoinRequest(new JoinRequest(user.getId(), supervisorId, ward.getId()));

        Assertions.assertEquals(member.getWardId(), ward.getId());
        List<PossibleShift> shifts = member.getShifts();
        Assertions.assertEquals(8, shifts.size());
        Assertions.assertEquals(ward.getDefaultTeamId(), member.getTeamId());
    }


    @Test
    @DisplayName("병동 가입 거절 시 USER 상태 NONE")
    void rejectUserJoinRequest() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        JoinResponse response = wardMembersService.rejectUserJoinRequest(new JoinRequest(user.getId(), supervisorId, ward.getId()));
        Assertions.assertEquals(user.getStatus(), JoinStatus.NONE);
        Assertions.assertEquals(response.status(), JoinStatus.NONE);
        Assertions.assertEquals(response.wardId(), null);
    }


    /// /// 예외 테스트

    @Test
    @DisplayName("입력한 토큰과 일치하는 병동이 없으면 예외")
    void findWardByToken_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(wardRepository.findByToken(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wardMembersService.findWardByToken(ward.getToken());
        });
    }

    @Test
    @DisplayName("병동의 supervisor가 아닌 사용자가 승인 시 예외")
    void acceptUserJoinRequest_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID notSupervisorId = UUID.randomUUID();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(ward, notSupervisorId)).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wardMembersService.acceptUserJoinRequest(new JoinRequest(user.getId(), notSupervisorId, ward.getId()));
        });
    }

    @Test
    @DisplayName("병동의 supervisor가 아닌 사용자가 거절 시 예외")
    void rejectUserJoinRequest_exception() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID notSupervisorId = UUID.randomUUID();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(existenceCheck.verifySupervisor(ward, notSupervisorId)).thenThrow(IllegalArgumentException.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            wardMembersService.rejectUserJoinRequest(new JoinRequest(user.getId(), notSupervisorId, ward.getId()));
        });
    }

    @Test
    @DisplayName("병동 가입 승인 시 이미 병동에 가입된 사용자일 경우 예외 발생")
    void acceptUserJoinRequest_이미_가입된_사용자_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        User user = User.create("name", "loginId", "password", "phone");
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.getUser(any())).thenReturn(user);
        when(memberService.isAlreadyExistMember(any())).thenReturn(true);

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> wardMembersService.acceptUserJoinRequest(new JoinRequest(user.getId(), supervisorId, ward.getId()))
        );
        Assertions.assertEquals(ALREADY_EXIST_WARD_MEMBER, e.getMessage());

    }

}
