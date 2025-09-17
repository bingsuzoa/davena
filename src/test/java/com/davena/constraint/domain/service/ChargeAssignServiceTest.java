//package com.davena.constraint.domain.service;
//
//import com.davena.common.ExistenceService;
//import com.davena.constraint.application.dto.wardCharge.ChargeMemberDto;
//import com.davena.constraint.application.dto.wardCharge.TeamChargeDto;
//import com.davena.constraint.application.dto.wardCharge.WardChargeDto;
//import com.davena.constraint.domain.model.Member;
//import com.davena.organization.domain.model.user.User;
//import com.davena.organization.domain.model.ward.Ward;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class ChargeAssignServiceTest {
//
//    @Mock
//    private ExistenceService existenceCheck;
//    @InjectMocks
//    private ChargeAssignService chargeAssignService;
//
//    /// ///해피 테스트
//
//    @Test
//    @DisplayName("병동의 차지 정보 업데이트하기")
//    void updateWardCharges() {
//        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
//        UUID aTeamId = ward.getTeams().getFirst().getId();
//        UUID bTeamId = ward.addNewTeam("bTeam");
//
//        User user1 = User.create("name1", "loginId1", "password", "01011112222");
//        ward.addNewUser(user1.getId());
//        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
//        member1.updateTeam(aTeamId);
//        when(existenceCheck.getMember(user1.getId())).thenReturn(member1);
//
//        User user2 = User.create("name2", "loginId2", "password", "01011112223");
//        ward.addNewUser(user2.getId());
//        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
//        member2.updateTeam(aTeamId);
//        when(existenceCheck.getMember(user2.getId())).thenReturn(member2);
//
//        User user3 = User.create("name3", "loginId3", "password", "01011112224");
//        ward.addNewUser(user3.getId());
//        Member member3 = new Member(user3.getId(), ward.getId(), user3.getName());
//        member3.updateTeam(bTeamId);
//        when(existenceCheck.getMember(user3.getId())).thenReturn(member3);
//
//        ward.setUsersToTeam(aTeamId, List.of(user1.getId(), user2.getId()));
//        ward.setUsersToTeam(bTeamId, List.of(user3.getId()));
//
//        when(existenceCheck.getWard(any())).thenReturn(ward);
//        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
//        when(existenceCheck.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2, member3));
//
//        ChargeMemberDto charge1 = new ChargeMemberDto(user1.getId(), user1.getName(), true, 1);
//        ChargeMemberDto charge2 = new ChargeMemberDto(user3.getId(), user3.getName(), true, 1);
//        ChargeMemberDto charge3 = new ChargeMemberDto(user2.getId(), user2.getName(), false, Member.LOWEST_RANK);
//
//        TeamChargeDto teamCharge1 = new TeamChargeDto(aTeamId, "aTeam", List.of(charge1, charge2));
//        TeamChargeDto teamCharge2 = new TeamChargeDto(bTeamId, "bTeam", List.of(charge3));
//
//        WardChargeDto wardChargeDto = new WardChargeDto(ward.getId(), ward.getSupervisorId(), List.of(teamCharge1, teamCharge2));
//
//        WardChargeDto response = chargeAssignService.updateWardCharges(wardChargeDto);
//
//        List<TeamChargeDto> responseTeamCharges = response.teamChargeDto();
//        for (TeamChargeDto teamChargeDto : responseTeamCharges) {
//            UUID teamId = teamChargeDto.teamId();
//            List<ChargeMemberDto> chargeMembers = teamChargeDto.chargeMembersDto();
//            if (teamId.equals(aTeamId)) {
//                Assertions.assertEquals(2, chargeMembers.size());
//            } else {
//                Assertions.assertEquals(1, chargeMembers.size());
//            }
//        }
//        Assertions.assertEquals(member1.getRank(), 1);
//        Assertions.assertTrue(member3.isCanCharge());
//        Assertions.assertEquals(member2.getRank(), Member.LOWEST_RANK);
//    }
//}
