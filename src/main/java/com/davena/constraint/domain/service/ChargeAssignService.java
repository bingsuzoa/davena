package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.wardCharge.ChargeMemberDto;
import com.davena.constraint.application.dto.wardCharge.TeamChargeDto;
import com.davena.constraint.application.dto.wardCharge.WardChargeDto;
import com.davena.constraint.application.dto.wardCharge.WardChargeRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargeAssignService {

    private final ExistenceService existenceService;
    private final MemberService memberService;

    public static final String IMPOSSIBLE_EMPTY_CHARGE_OF_TEAM = "팀에 차지가 최소 한 명은 배정되어야 합니다.";

    public WardChargeDto getWardCharges(WardChargeRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        return getWardChargeDto(memberService.getAllMembersOfWard(ward.getId()), ward);
    }

    public WardChargeDto updateWardCharges(WardChargeDto request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        List<TeamChargeDto> teamCharges = request.teamChargeDto();
        for (TeamChargeDto teamCharge : teamCharges) {
            updateCanChargeOfMember(teamCharge);
        }
        return getWardChargeDto(memberService.getAllMembersOfWard(ward.getId()), ward);
    }

    private void updateCanChargeOfMember(TeamChargeDto teamCharge) {
        existsChargeMember(teamCharge);
        for (ChargeMemberDto chargeDto : teamCharge.chargeMembersDto()) {
            Member member = memberService.getMember(chargeDto.memberId());
            member.updateCanCharge(chargeDto.canCharge());
            updateRankIfCanCharge(member, chargeDto);
        }
    }

    private void existsChargeMember(TeamChargeDto teamChargeDto) {
        boolean isAllFalse = teamChargeDto.chargeMembersDto()
                .stream()
                .noneMatch(ChargeMemberDto::canCharge);

        if (isAllFalse) {
            throw new IllegalArgumentException(IMPOSSIBLE_EMPTY_CHARGE_OF_TEAM);
        }
    }

    private void updateRankIfCanCharge(Member member, ChargeMemberDto chargeDto) {
        if (chargeDto.canCharge()) {
            member.updateRank(chargeDto.rank());
        }
    }

    private WardChargeDto getWardChargeDto(List<Member> members, Ward ward) {
        Map<UUID, List<Member>> membersByTeam = members.stream()
                .collect(Collectors.groupingBy(Member::getTeamId));

        List<TeamChargeDto> teamChargeDtos = ward.getTeams().stream()
                .map(team -> new TeamChargeDto(
                        team.getId(),
                        team.getName(),
                        membersByTeam.getOrDefault(team.getId(), List.of()).stream()
                                .map(m -> new ChargeMemberDto(m.getUserId(), m.getName(), m.isCanCharge(), m.getRank()))
                                .toList()
                ))
                .toList();

        return new WardChargeDto(ward.getId(), ward.getSupervisorId(), teamChargeDtos);
    }
}
