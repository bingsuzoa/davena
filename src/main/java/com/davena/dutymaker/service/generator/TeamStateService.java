package com.davena.dutymaker.service.generator;

import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.organization.team.TeamState;
import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Service
@RequiredArgsConstructor
public class TeamStateService {

    private final RequirementRuleRepository ruleRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final WardRepository wardRepository;
    private final ShiftTypeRepository shiftTypeRepository;

    public Map<Long, Map<Integer, TeamState>> initTeamState(Long scheduleId, Long wardId) {
        Ward ward = getWardWithTeams(wardId);
        Schedule schedule = getSchedule(scheduleId);
        YearMonth thisMonth = YearMonth.parse(schedule.getYearMonth());

        Map<Long, Map<Integer, TeamState>> teamStates = new HashMap<>();
        List<ShiftType> shiftTypes = shiftTypeRepository.findByWardId(ward.getId());

        for (Team team : ward.getTeams()) {
            Map<Integer, TeamState> teamStateOfDay = new HashMap<>();

            for (int day = 1; day <= thisMonth.lengthOfMonth(); day++) {
                Map<ShiftType, Integer> remain = new HashMap<>();
                DayType dayType = DayType.from(LocalDate.of(thisMonth.getYear(), thisMonth.getMonth(), day));

                List<RequirementRule> rules = ruleRepository.findByTeamIdAndDayType(team.getId(), dayType);
                /// /// 추후 수정 필요(OFF 우선 0으로 배정)
                for (ShiftType shift : shiftTypes) {
                    remain.put(shift, 0);
                }

                for (RequirementRule rule : rules) {
                    remain.put(rule.getShiftType(), rule.getRequired());
                }

                teamStateOfDay.put(day, new TeamState(remain, shiftTypes, getChargeOrder(team.getId())));
            }
            teamStates.put(team.getId(), teamStateOfDay);
        }
        return teamStates;
    }


    private PriorityQueue<Member> getChargeOrder(Long teamId) {
        List<Member> members = memberRepository.findMembersWithTeamByTeamId(teamId);
        return new PriorityQueue<>(members);
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new IllegalArgumentException(Schedule.NOT_EXIST_THIS_MONTH_SCHEDULE));
    }

    private Ward getWardWithTeams(Long wardId) {
        return wardRepository.getWardWithTeams(wardId).orElseThrow(() ->
                new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }
}
