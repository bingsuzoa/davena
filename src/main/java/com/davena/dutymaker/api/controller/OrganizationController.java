package com.davena.dutymaker.api.controller;

import com.davena.dutymaker.api.dto.member.ChargeRequest;
import com.davena.dutymaker.api.dto.member.MemberAllowedShiftRequest;
import com.davena.dutymaker.api.dto.member.MemberRequest;
import com.davena.dutymaker.api.dto.skillGrade.GradeDistributionRequest;
import com.davena.dutymaker.api.dto.skillGrade.GradeUpdateRequest;
import com.davena.dutymaker.api.dto.team.TeamDistributionRequest;
import com.davena.dutymaker.api.dto.team.TeamUpdateRequest;
import com.davena.dutymaker.api.dto.ward.WardRequest;
import com.davena.dutymaker.service.GradeDistributionService;
import com.davena.dutymaker.service.MemberService;
import com.davena.dutymaker.service.TeamDistributionService;
import com.davena.dutymaker.service.WardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class OrganizationController {

    private final MemberService memberService;
    private final WardService wardService;
    private final TeamDistributionService teamDistributionService;
    private final GradeDistributionService gradeDistributionService;

    @PostMapping("/member")
    public void createMember(MemberRequest memberRequest) {
        memberService.createMember(memberRequest);
    }

    @PostMapping("/{memberId}/ward")
    public void createWard(@PathVariable Long memberId, WardRequest wardRequest) {
        wardService.createWardAndOffType(memberId, wardRequest);
    }

    @PostMapping("/ward/{wardId}/allowedShifts")
    public void updateAllowedShifts(@PathVariable Long wardId, @RequestBody MemberAllowedShiftRequest request) {
        memberService.updateAllowedShifts(request);
    }

    @GetMapping("/{wardId}/charge")
    public void getMembersForCharge(@PathVariable Long wardId) {
        wardService.getMembersForCharge(wardId);
    }

    @PostMapping("/charge")
    public void updateChargeOfMember(@RequestBody ChargeRequest chargeRequest) {
        memberService.updateChargeOfMember(chargeRequest);
    }

    @PutMapping("/{wardId}/teams/distribution")
    public void distributeTeams(@PathVariable Long wardId, @Valid @RequestBody TeamDistributionRequest request) {
        teamDistributionService.updateTeamDistribution(wardId, request);
    }

    @DeleteMapping("/{wardId}/{teamId}")
    public void deleteTeam(@PathVariable Long wardId, @PathVariable Long teamId) {
        teamDistributionService.deleteTeam(wardId, teamId);
    }

    @PutMapping("/{wardId}/{teamId}")
    public void updateTeam(@PathVariable Long wardId, @PathVariable Long teamId, TeamUpdateRequest request) {
        teamDistributionService.updateTeam(wardId, teamId, request);
    }

    @PutMapping("/{wardId}/grades/distribution")
    public void distributeSkillGrades(@PathVariable Long wardId, GradeDistributionRequest request) {
        gradeDistributionService.createSkillGrades(wardId, request);
    }

    @DeleteMapping("/{wardId}/{gradeId}")
    public void deleteGrade(@PathVariable Long wardId, @PathVariable Long gradeId) {
        gradeDistributionService.deleteGrade(wardId, gradeId);
    }

    @PutMapping("/{wardId}/{gradeId}")
    public void updateGrade(@PathVariable Long wardId, @PathVariable Long gradeId, GradeUpdateRequest request) {
        gradeDistributionService.updateGrade(wardId, gradeId, request);
    }
}
