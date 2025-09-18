package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.wardCharge.ChargeMemberDto;
import com.davena.constraint.application.dto.wardCharge.TeamChargeDto;
import com.davena.constraint.application.dto.wardCharge.WardChargeDto;
import com.davena.constraint.application.dto.wardCharge.WardChargeRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Team;
import com.davena.organization.domain.model.ward.Ward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.davena.constraint.domain.model.Member.LOWEST_RANK;
import static com.davena.constraint.domain.service.ChargeAssignService.IMPOSSIBLE_EMPTY_CHARGE_OF_TEAM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChargeAssignServiceTest {

    @Mock
    private ExistenceService existenceCheck;
    @Mock
    private MemberService memberService;
    @InjectMocks
    private ChargeAssignService chargeAssignService;

    /// ///해피 테스트

    @Test
    @DisplayName("병동 차지 조회 : 처음에는 모두 true로 지정되어 있는 상태")
    void getWardCharges() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID supervisorId = ward.getSupervisorId();
        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        Team defaultTeam = ward.getTeams().getFirst();
        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateTeam(defaultTeam.getId());

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateTeam(defaultTeam.getId());
        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2));

        WardChargeDto wardChargeDto = chargeAssignService.getWardCharges(new WardChargeRequest(ward.getId(), supervisorId));
        TeamChargeDto team = wardChargeDto.teamChargeDto().getFirst();
        for(ChargeMemberDto member: team.chargeMembersDto()) {
            Assertions.assertEquals(true, member.canCharge());
            Assertions.assertEquals(LOWEST_RANK, member.rank());
        }
    }

    @Test
    @DisplayName("병동의 차지 정보 업데이트하기")
    void updateWardCharges() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("bTeam");

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateTeam(aTeamId);
        when(memberService.getMember(user1.getId())).thenReturn(member1);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateTeam(aTeamId);
        when(memberService.getMember(user2.getId())).thenReturn(member2);

        User user3 = User.create("name3", "loginId3", "password", "01011112224");
        Member member3 = new Member(user3.getId(), ward.getId(), user3.getName());
        member3.updateTeam(bTeamId);
        when(memberService.getMember(user3.getId())).thenReturn(member3);

        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);
        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2, member3));

        ChargeMemberDto charge1 = new ChargeMemberDto(user1.getId(), user1.getName(), true, 1);
        ChargeMemberDto charge2 = new ChargeMemberDto(user3.getId(), user3.getName(), true, 1);
        ChargeMemberDto charge3 = new ChargeMemberDto(user2.getId(), user2.getName(), false, Member.LOWEST_RANK);

        TeamChargeDto teamCharge1 = new TeamChargeDto(aTeamId, "aTeam", List.of(charge1, charge2));
        TeamChargeDto teamCharge2 = new TeamChargeDto(bTeamId, "bTeam", List.of(charge3));

        WardChargeDto wardChargeDto = new WardChargeDto(ward.getId(), ward.getSupervisorId(), List.of(teamCharge1, teamCharge2));

        WardChargeDto response = chargeAssignService.updateWardCharges(wardChargeDto);

        List<TeamChargeDto> responseTeamCharges = response.teamChargeDto();
        for (TeamChargeDto teamChargeDto : responseTeamCharges) {
            UUID teamId = teamChargeDto.teamId();
            List<ChargeMemberDto> chargeMembers = teamChargeDto.chargeMembersDto();
            if (teamId.equals(aTeamId)) {
                Assertions.assertEquals(2, chargeMembers.size());
            } else {
                Assertions.assertEquals(1, chargeMembers.size());
            }
        }
        Assertions.assertEquals(member1.getRank(), 1);
        Assertions.assertTrue(member3.isCanCharge());
        Assertions.assertEquals(member2.getRank(), Member.LOWEST_RANK);
    }

    /// ///예외 테스트
    @Test
    @DisplayName("병동의 차지 정보 업데이트할 때, 팀에 차지 배정이 한 명도 없으면 예외")
    void updateWardCharges_팀에_차지_배정_한_명도_없으면_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        UUID aTeamId = ward.getTeams().getFirst().getId();
        UUID bTeamId = ward.addNewTeam("bTeam");

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        member1.updateTeam(aTeamId);

        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateTeam(aTeamId);

        User user3 = User.create("name3", "loginId3", "password", "01011112224");
        Member member3 = new Member(user3.getId(), ward.getId(), user3.getName());
        member3.updateTeam(bTeamId);

        when(existenceCheck.getWard(any())).thenReturn(ward);
        when(existenceCheck.verifySupervisor(any(), any())).thenReturn(true);

        ChargeMemberDto charge1 = new ChargeMemberDto(user1.getId(), user1.getName(), false, Member.LOWEST_RANK);
        ChargeMemberDto charge2 = new ChargeMemberDto(user3.getId(), user3.getName(), false, LOWEST_RANK);
        ChargeMemberDto charge3 = new ChargeMemberDto(user2.getId(), user2.getName(), false, Member.LOWEST_RANK);

        TeamChargeDto teamCharge1 = new TeamChargeDto(aTeamId, "aTeam", List.of(charge1, charge2));
        TeamChargeDto teamCharge2 = new TeamChargeDto(bTeamId, "bTeam", List.of(charge3));

        WardChargeDto wardChargeDto = new WardChargeDto(ward.getId(), ward.getSupervisorId(), List.of(teamCharge1, teamCharge2));

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> chargeAssignService.updateWardCharges(wardChargeDto)
        );
        Assertions.assertEquals(IMPOSSIBLE_EMPTY_CHARGE_OF_TEAM, e.getMessage());
    }
}
