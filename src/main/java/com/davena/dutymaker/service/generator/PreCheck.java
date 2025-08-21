package com.davena.dutymaker.service.generator;


import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftCell;
import com.davena.dutymaker.api.dto.schedule.payload.draft.DraftPayload;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.policy.DayType;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.schedule.ScheduleStatus;
import com.davena.dutymaker.domain.shiftRequirement.RequirementRule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.ScheduleRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreCheck {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final WardRepository wardRepository;

    public boolean preCheckWard(Long wardId, Long scheduleId) {
        Ward ward = getWardWithTeamsAndRules(wardId);
        for (Team team : ward.getTeams()) {
            return preCheckTeam(team, scheduleId);
        }
        return true;
    }

    private boolean preCheckTeam(Team team, Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        if (!schedule.getStatus().equals(ScheduleStatus.DRAFT)) {
            throw new IllegalArgumentException(Schedule.NOT_DRAFT_STATE);
        }

        DraftPayload payload = schedule.getDraft().getPayload();
        YearMonth ym = YearMonth.parse(schedule.getYearMonth());
        int days = ym.lengthOfMonth();

        for (int d = 1; d <= days; d++) {
            final int today = d;
            LocalDate day = ym.atDay(d);

            boolean hasCharge = payload.getCells().stream()
                    .filter(cell -> cell.day() == today)
                    .anyMatch(cell -> cell.teamId() == team.getId() && cell.isCharge());

            if (!hasCharge) {
                throw new IllegalArgumentException(d + "일에 " + team.getName() + "의 차지 수가 부족해서 근무표를 생성할 수 없어요.");
            }

            int supervisorCount = 1;
            int teamStaffCount = (int) memberRepository.countByTeamId(team.getId()) - supervisorCount;
            int required = getRequiredStaffCountOfDay(day, team);
            int requestOff = payload.getCells().stream()
                    .filter(cell -> cell.day() == today)
                    .filter(cell -> Objects.equals(cell.shiftType(), ShiftType.OFF))
                    .map(DraftCell::memberId)
                    .collect(Collectors.toSet())
                    .size();

            if (required > teamStaffCount - requestOff) {
                int needCount = required - (teamStaffCount - requestOff);
                throw new IllegalArgumentException(d + "일에 " + team.getName() + "에 휴가자 신청이 너무 많습니다. " +
                        needCount + "명은 다른 날로 휴가 변경하세요."
                );
            }
        }
        return true;
    }

    private int getRequiredStaffCountOfDay(LocalDate today, Team team) {
        List<RequirementRule> rules = team.getRequirementRules();
        DayType dayType = DayType.from(today);
        return rules.stream()
                .filter(r -> r.getDayType().equals(dayType))
                .mapToInt(RequirementRule::getRequired)
                .sum();
    }

    private Ward getWardWithTeamsAndRules(Long wardId) {
        return wardRepository.getWardWithTeamsAndRules(wardId).orElseThrow(() ->
                new IllegalArgumentException(Ward.NOT_EXIST_WARD));
    }


    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findByIdWithDraft(scheduleId).orElseThrow(() ->
                new IllegalArgumentException(Schedule.NOT_EXIST_THIS_MONTH_SCHEDULE)
        );
    }
}
