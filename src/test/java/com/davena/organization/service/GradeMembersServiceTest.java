package com.davena.organization.service;

import com.davena.common.ExistenceService;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.application.dto.user.UserDto;
import com.davena.organization.application.dto.ward.grade.GradeDto;
import com.davena.organization.application.dto.ward.grade.GradeMembersResponse;
import com.davena.organization.application.dto.ward.grade.CreateGradeRequest;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.service.GradeMembersService;
import com.davena.organization.domain.service.util.Mapper;
import com.davena.organization.domain.service.util.MembersValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeMembersServiceTest {

    private ExistenceService existenceCheck = mock(ExistenceService.class);
    private MembersValidator membersValidator = mock(MembersValidator.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private Mapper mapper = new Mapper();

    private GradeMembersService gradeMembersService =
            new GradeMembersService(existenceCheck, membersValidator, userRepository, mapper);


    @Test
    @DisplayName("새로운 숙련도 추가하기")
    void addNewGrade() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        ward.addNewUser(user1.getId());
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        when(existenceCheck.getMember(user1.getId())).thenReturn(member1);
        when(existenceCheck.getUser(user1.getId())).thenReturn(user1);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        ward.addNewUser(user2.getId());
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        when(existenceCheck.getUser(user2.getId())).thenReturn(user2);

        User user3 = User.create("name3", "loginId3", "password", "01011112224");
        ward.addNewUser(user3.getId());
        Member member3 = new Member(user3.getId(), ward.getId(), user3.getName());
        when(existenceCheck.getUser(user3.getId())).thenReturn(user3);

        when(userRepository.findAllById(any())).thenReturn(List.of(user1, user2, user3));

        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        doNothing().when(membersValidator).validateAtLeastOneMember(any());
        doNothing().when(membersValidator).validateContainAllMembers(any(), any());
        when(userRepository.findAllById(any())).thenReturn(List.of(user1, user2, user3));

        CreateGradeRequest request = new CreateGradeRequest(null, supervisorId, ward.getId(), "eE");
        GradeMembersResponse response = gradeMembersService.addNewGrade(request);

        Map<GradeDto, List<UserDto>> usersOfMap = response.usersOfGrade();
        Assertions.assertEquals(usersOfMap.size(), 2);
        for(GradeDto gradeDto : usersOfMap.keySet()) {
            if(gradeDto.name().equals(Ward.DEFAULT_GRADE)) {
                Assertions.assertEquals(usersOfMap.get(gradeDto).size(), 3);
            } else {
                Assertions.assertTrue(usersOfMap.get(gradeDto).isEmpty());
            }
        }
    }
}
