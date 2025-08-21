package com.davena.dutymaker.service.generator;


import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.MemberState;
import com.davena.dutymaker.domain.organization.team.TeamState;
import com.davena.dutymaker.domain.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeneratorService {

    private final MemberStateService memberStateService;
    private final TeamStateService teamStateService;

    private Map<Long, MemberState> initMemberState(Long wardId, Long scheduleId) {
        return memberStateService.initMemberState(wardId, scheduleId);
    }

    private Map<Long, TeamState> initTeamState(Schedule schedule, Ward ward) {
        return teamStateService.initTeamState(schedule, ward);
    }

}
