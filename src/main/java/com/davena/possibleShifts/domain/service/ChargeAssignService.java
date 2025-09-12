package com.davena.possibleShifts.domain.service;

import com.davena.common.ExistenceService;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.possibleShifts.application.dto.wardCharge.ChargeMemberDto;
import com.davena.possibleShifts.application.dto.wardCharge.TeamChargeDto;
import com.davena.possibleShifts.application.dto.wardCharge.WardChargeDto;
import com.davena.possibleShifts.application.dto.wardCharge.WardChargeRequest;
import com.davena.possibleShifts.domain.model.Member;
import com.davena.possibleShifts.domain.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargeAssignService {

    private final MemberRepository memberRepository;
    private final ExistenceService existenceService;

    public WardChargeDto getWardCharges(WardChargeRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        return getWardChargeDto(existenceService.getAllMembersOfWard(ward.getId()), ward);
    }

    public WardChargeDto updateWardCharges(WardChargeDto request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        List<TeamChargeDto> teamCharges = request.teamChargeDto();
        for (TeamChargeDto teamCharge : teamCharges) {
            updateCanChargeOfMember(teamCharge);
        }
        return getWardChargeDto(existenceService.getAllMembersOfWard(ward.getId()), ward);
    }

    private void updateCanChargeOfMember(TeamChargeDto teamCharge) {
        for (ChargeMemberDto chargeDto : teamCharge.chargeMembersDto()) {
            Member member = existenceService.getMember(chargeDto.memberId());
            member.updateCanCharge(chargeDto.canCharge());
            updateRankIfCanCharge(member, chargeDto);
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
