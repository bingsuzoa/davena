package com.davena.organization.domain.service.util;

import com.davena.organization.application.dto.ward.grade.GradeDto;
import com.davena.organization.application.dto.ward.shift.ShiftDto;
import com.davena.organization.application.dto.ward.shiftRequirement.RequirementDto;
import com.davena.organization.application.dto.ward.shiftRequirement.RequirementShiftDto;
import com.davena.organization.application.dto.ward.team.TeamDto;
import com.davena.organization.domain.model.ward.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public List<TeamDto> getTeamsDto(List<Team> teams) {
        return teams.stream()
                .map(team -> new TeamDto(team.getId(), team.getName(), team.isDefault()))
                .toList();
    }

    public List<GradeDto> getGradesDto(List<Grade> grades) {
        return grades.stream()
                .map(grade -> new GradeDto(grade.getId(), grade.getName(), grade.isDefault()))
                .toList();
    }

    public Map<DayType, List<ShiftDto>> getShiftsDto(Map<DayType, List<Shift>> shifts) {
        return shifts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(Shift::toDto)
                                .toList()
                ));
    }

    public Map<TeamDto, List<UUID>> getTeamUsers(List<Team> teams) {
        Map<TeamDto, List<UUID>> teamUsers = new HashMap<>();
        for (Team team : teams) {
            teamUsers.put(new TeamDto(team.getId(), team.getName(), team.isDefault()), team.getUsers());
        }
        return teamUsers;
    }

    public Map<GradeDto, List<UUID>> getGradeUsers(List<Grade> grades) {
        Map<GradeDto, List<UUID>> gradeUsers = new HashMap<>();
        for (Grade grade : grades) {
            gradeUsers.put(new GradeDto(grade.getId(), grade.getName(), grade.isDefault()), grade.getUsers());
        }
        return gradeUsers;
    }

    public List<RequirementDto> getRequirementDtos(
            Map<UUID, Map<DayType, Map<UUID, ShiftRequirement>>> wardRequirements,
            List<Team> teams
    ) {
        return teams.stream()
                .map(team -> {
                    Map<DayType, Map<UUID, ShiftRequirement>> teamReqs =
                            wardRequirements.getOrDefault(team.getId(), Map.of());

                    Map<DayType, List<RequirementShiftDto>> requirementDtos = new HashMap<>();

                    for (Map.Entry<DayType, Map<UUID, ShiftRequirement>> entry : teamReqs.entrySet()) {
                        DayType dayType = entry.getKey();

                        List<RequirementShiftDto> shifts = entry.getValue().values().stream()
                                .map(req -> new RequirementShiftDto(
                                        req.getShiftId(),
                                        req.getShiftName(),
                                        req.getRequiredCount()
                                ))
                                .toList();

                        requirementDtos.put(dayType, shifts);
                    }

                    return new RequirementDto(team.getId(), team.getName(), requirementDtos);
                })
                .toList();
    }
}
