package com.davena.organization.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.application.dto.ward.grade.*;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Grade;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.service.GradeMembersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.davena.organization.domain.service.GradeMembersService.HAS_ANY_MEMBER_OF_GRADE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeMembersServiceTest {

    private ExistenceService existenceCheck = mock(ExistenceService.class);
    private MemberService memberService = mock(MemberService.class);

    private GradeMembersService gradeMembersService =
            new GradeMembersService(existenceCheck, memberService);

    /// ///해피 테스트
    @Test
    @DisplayName("새로운 숙련도 추가하기")
    void addNewGrade() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        UUID defaultGradeId = ward.getGrades().getFirst().getId();

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateGrade(defaultGradeId);
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateGrade(defaultGradeId);
        when(memberService.getMember(user2.getId())).thenReturn(member2);

        User user3 = User.create("name3", "loginId3", "password", "01011112224");
        Member member3 = new Member(user3.getId(), ward.getId(), user3.getName());
        member3.updateGrade(defaultGradeId);
        when(memberService.getMember(user3.getId())).thenReturn(member3);

        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2, member3));
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        CreateGradeRequest request = new CreateGradeRequest(supervisorId, ward.getId(), "2단계");
        GradeMembersResponse response = gradeMembersService.addNewGrade(request);
        Assertions.assertEquals(2, response.gradeMembers().size());
    }

    @Test
    @DisplayName("member들의 숙련도 업데이트하기")
    void updateWardGradeAssignments() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        UUID firstGradeId = ward.getGrades().getFirst().getId();
        UUID secondGradeId = ward.addNewGrade("2단계");

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateGrade(firstGradeId);
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateGrade(firstGradeId);
        when(memberService.getMember(user2.getId())).thenReturn(member2);

        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2));
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        doNothing().when(memberService).validateAtLeastOneMember(any());
        doNothing().when(memberService).validateContainAllMembers(any(), any());

        Map<UUID, List<UUID>> allMembersGrades = new HashMap<>();
        allMembersGrades.put(firstGradeId, List.of(user1.getId()));
        allMembersGrades.put(secondGradeId, List.of(user2.getId()));
        UpdateGradeMembersRequest request = new UpdateGradeMembersRequest(supervisorId, ward.getId(), allMembersGrades);

        GradeMembersResponse response = gradeMembersService.updateWardGradeAssignments(request);
        Map<UUID, GradeDto> updatedAllMembersGrades = response.gradeMembers();
        Assertions.assertEquals(1, updatedAllMembersGrades.get(secondGradeId).membersOfGrade().size());
    }

    /// ///예외 테스트
    @Test
    @DisplayName("Default 숙련도 삭제할 경우 예외 발생")
    void deleteGrade_default_숙련도_삭제_불가() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        UUID firstGradeId = ward.getGrades().getFirst().getId();

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateGrade(firstGradeId);
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1));
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        Map<UUID, List<UUID>> allMembersGrades = new HashMap<>();
        allMembersGrades.put(firstGradeId, List.of(user1.getId()));
        DeleteGradeRequest request = new DeleteGradeRequest(ward.getId(), supervisorId, firstGradeId);

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gradeMembersService.deleteGrade(request));
        Assertions.assertEquals(Grade.CAN_NOT_REMOVE_DEFAULT_GRADE, e.getMessage());
    }

    @Test
    @DisplayName("삭제하려는 숙련도에 멤버가 지정되어 있을 경우 삭제 불가")
    void deleteGrade_멤버_지정되어_있을_시_삭제_불가() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        when(existenceCheck.getWard(any())).thenReturn(ward);
        UUID supervisorId = ward.getSupervisorId();
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        UUID firstGradeId = ward.getGrades().getFirst().getId();
        UUID secondGradeId = ward.addNewGrade("2단계");

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateGrade(firstGradeId);
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateGrade(secondGradeId);
        when(memberService.getMember(user2.getId())).thenReturn(member2);

        List<Member> members = List.of(member2);
        when(memberService.getMembersOfGrade(any(), any())).thenReturn(members);

        DeleteGradeRequest request = new DeleteGradeRequest(ward.getId(), supervisorId, secondGradeId);
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> gradeMembersService.deleteGrade(request)
        );
        Assertions.assertEquals(HAS_ANY_MEMBER_OF_GRADE, e.getMessage());
    }
}
