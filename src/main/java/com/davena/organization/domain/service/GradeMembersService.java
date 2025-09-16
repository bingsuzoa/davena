package com.davena.organization.domain.service;


import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.application.dto.ward.MemberDto;
import com.davena.organization.application.dto.ward.grade.*;
import com.davena.organization.domain.model.ward.Grade;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GradeMembersService {

    private final ExistenceService existenceCheck;
    private final MemberService memberService;

    public static final String HAS_ANY_MEMBER_OF_GRADE = "숙련도에 멤버가 배정된 경우에는 숙련도 삭제할 수 없어요. 멤버를 옮겨주세요.";

    public GradeMembersResponse addNewGrade(CreateGradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        ward.addNewGrade(request.name());
        return getGradeMembersDto(ward);
    }

    public GradeMembersResponse getGradeMembers(GetGradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        return getGradeMembersDto(ward);
    }

    public GradeMembersResponse deleteGrade(DeleteGradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        UUID gradeId = request.gradeId();
        validateGradeHasNoMembers(ward, gradeId);
        ward.deleteGrade(gradeId);
        return getGradeMembersDto(ward);
    }

    private void validateGradeHasNoMembers(Ward ward, UUID gradeId) {
        List<Member> membersOfGrade = memberService.getMembersOfGrade(ward.getId(), gradeId);
        if (!membersOfGrade.isEmpty()) {
            throw new IllegalArgumentException(HAS_ANY_MEMBER_OF_GRADE);
        }
    }

    public GradeMembersResponse updateWardGradeAssignments(UpdateGradeMembersRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        memberService.validateAtLeastOneMember(request.usersOfGrade());
        memberService.validateContainAllMembers(ward, request.usersOfGrade());
        updateMembersGrade(request);
        return getGradeMembersDto(ward);
    }

    private void updateMembersGrade(UpdateGradeMembersRequest request) {
        Map<UUID, List<UUID>> usersOfGrade = request.usersOfGrade();
        for (UUID gradeId : usersOfGrade.keySet()) {
            updateMemberGrade(gradeId, usersOfGrade.get(gradeId));
        }
    }

    private void updateMemberGrade(UUID gradeId, List<UUID> userDtos) {
        for (UUID userId : userDtos) {
            Member member = memberService.getMember(userId);
            member.updateGrade(gradeId);
        }
    }

    private GradeMembersResponse getGradeMembersDto(Ward ward) {
        List<Member> members = memberService.getAllMembersOfWard(ward.getId());

        Map<UUID, List<MemberDto>> gradeDtos = new HashMap<>();
        for (Grade grade : ward.getGrades()) {
            gradeDtos.putIfAbsent(grade.getId(), new ArrayList<>());
        }
        for (Member member : members) {
            gradeDtos.computeIfAbsent(member.getGradeId(), k -> new ArrayList<>())
                    .add(MemberDto.from(member));
        }
        Map<UUID, GradeDto> gradeMembersMap = new HashMap<>();
        for (UUID gradeId : gradeDtos.keySet()) {
            Grade grade = ward.getGrade(gradeId);
            gradeMembersMap.computeIfAbsent(gradeId, k -> new GradeDto(gradeId, grade.getName(), grade.isDefault(), gradeDtos.get(gradeId)));
        }
        return new GradeMembersResponse(ward.getId(), ward.getSupervisorId(), gradeMembersMap);
    }
}
