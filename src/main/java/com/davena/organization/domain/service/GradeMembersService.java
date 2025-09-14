package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserDto;
import com.davena.organization.application.dto.ward.grade.GradeDto;
import com.davena.organization.application.dto.ward.grade.GradeMembersRequest;
import com.davena.organization.application.dto.ward.grade.GradeMembersResponse;
import com.davena.organization.application.dto.ward.grade.GradeRequest;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.common.ExistenceService;
import com.davena.organization.domain.service.util.Mapper;
import com.davena.organization.domain.service.util.MembersValidator;
import com.davena.constraint.domain.model.Member;
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
    private final UserRepository userRepository;
    private final Mapper mapper;

    public GradeMembersResponse addNewGrade(GradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        ward.addNewGrade(request.name());
        return getGradeMembersDto(ward, mapper.getGradeUsers(ward.getGrades()));
    }

    public GradeMembersResponse getGradeMembers(GradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        return getGradeMembersDto(ward, mapper.getGradeUsers(ward.getGrades()));
    }

    public GradeMembersResponse deleteGrade(GradeRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        UUID gradeId = request.gradeId();
        ward.deleteGrade(gradeId);
        return getGradeMembersDto(ward, mapper.getGradeUsers(ward.getGrades()));
    }

    public GradeMembersResponse updateWardGradeAssignments(GradeMembersRequest request) {
        Ward ward = existenceCheck.getWard(request.wardId());
        existenceCheck.verifySupervisor(ward, request.supervisorId());
        membersValidator.validateAtLeastOneMember(request.usersOfGrade());
        membersValidator.validateContainAllMembers(ward, request.usersOfGrade());
        ward.clearAllGradeMembers();
        request.usersOfGrade().forEach(ward::setUsersToGrade);
        GradeMembersResponse gradeMembers =  getGradeMembersDto(ward, mapper.getGradeUsers(ward.getGrades()));
        syncMembersGrade(gradeMembers);
        return gradeMembers;
    }

    private void syncMembersGrade(GradeMembersResponse gradeMembers) {
        Map<GradeDto, List<UserDto>> usersOfGrade = gradeMembers.usersOfGrade();
        for(GradeDto gradeDto : usersOfGrade.keySet()) {
            syncMemberGrade(gradeDto, usersOfGrade.get(gradeDto));
        }
    }

    private void syncMemberGrade(GradeDto gradeDto, List<UserDto> userDtos) {
        UUID gradeId = gradeDto.id();
        for(UserDto userDto : userDtos) {
            Member member = existenceCheck.getMember(userDto.id());
            member.updateGrade(gradeId);
        }
    }

    private GradeMembersResponse getGradeMembersDto(Ward ward, Map<GradeDto, List<UUID>> gradeUsers) {
        List<UUID> allUserIds = gradeUsers.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();

        Map<UUID, User> userMap = userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<GradeDto, List<UserDto>> gradeUserDtos = gradeUsers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(userMap::get)
                                .map(UserDto::from)
                                .toList()
                ));

        return new GradeMembersResponse(ward.getSupervisorId(), ward.getId(), gradeUserDtos);
    }
}
