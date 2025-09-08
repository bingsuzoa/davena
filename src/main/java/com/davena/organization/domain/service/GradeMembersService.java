package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserDto;
import com.davena.organization.application.dto.ward.grade.GradeDto;
import com.davena.organization.application.dto.ward.grade.GradeMembersRequest;
import com.davena.organization.application.dto.ward.grade.GradeMembersResponse;
import com.davena.organization.application.dto.ward.grade.GradeRequest;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeMembersService {

    private final ExistenceService existenceCheck;
    private final MembersValidator membersValidator;
    private final UserMapper userMapper;

    public GradeMembersResponse addNewGrade(GradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        ward.addNewGrade(request.name());
        return getGradeMembersDto(ward, ward.getGradeUsers());
    }

    public GradeMembersResponse getGradeMembers(GradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        return getGradeMembersDto(ward, ward.getGradeUsers());
    }

    public GradeMembersResponse deleteGrade(GradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        UUID gradeId = request.gradeId();
        ward.deleteGrade(gradeId);
        return getGradeMembersDto(ward, ward.getGradeUsers());
    }

    public GradeMembersResponse updateMembersOfGrade(GradeMembersRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        membersValidator.validateAtLeastOneMember(request.usersOfGrade());
        membersValidator.validateContainAllMembers(ward, request.usersOfGrade());
        ward.clearAllGradeMembers();
        request.usersOfGrade().forEach(ward::setUsersToGrade);
        return getGradeMembersDto(ward, ward.getGradeUsers());
    }

    private GradeMembersResponse getGradeMembersDto(Ward ward, Map<GradeDto, List<UUID>> gradeMembers) {
        Map<UUID, User> userMap = userMapper.getUserMap(gradeMembers);

        Map<GradeDto, List<UserDto>> gradeMemberDtos = gradeMembers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> userMapper.toUserDtos(e.getValue(), userMap)
                ));
        return new GradeMembersResponse(ward.getSupervisorId(), ward.getId(), gradeMemberDtos);
    }
}
