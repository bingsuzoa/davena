package com.davena.organization.domain.model.ward;

import com.davena.organization.domain.model.grade.Grade;
import com.davena.organization.domain.model.grade.GradeId;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.member.MemberId;
import com.davena.organization.domain.model.shift.Shift;
import com.davena.organization.domain.model.shift.ShiftId;
import com.davena.organization.domain.model.team.Team;
import com.davena.organization.domain.model.team.TeamId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Ward {

    private Ward(
            HospitalId hospitalId,
            WardId id,
            MemberId memberId,
            String name
    ) {
        this.hospitalId = hospitalId;
        this.id = id;
        this.memberId = memberId;
        this.name = name;
    }

    public static final String DEFAULT_TEAM = "A팀";
    public static final String DEFAULT_GRADE = "1단계";
    public static final String OFF = "off";

    private HospitalId hospitalId;
    private WardId id;
    private MemberId memberId;
    private String name;
    private List<Team> teams = new ArrayList<>();
    private List<Grade> grades = new ArrayList<>();
    private List<Shift> shifts = new ArrayList<>();

    public static Ward create(HospitalId hospitalId, MemberId memberId, String name) {
        Ward ward = new Ward(hospitalId, new WardId(UUID.randomUUID()), memberId, name);
        ward.addTeam(DEFAULT_TEAM, true);
        ward.addGrade(DEFAULT_GRADE, true);
        ward.addShift(OFF, true);
        return ward;
    }

    public void addTeam(String name, boolean isDefault) {
        teams.add(new Team(new TeamId(UUID.randomUUID()), this.id, name, isDefault));
    }

    public void addGrade(String name, boolean isDefault) {
        grades.add(new Grade(new GradeId(UUID.randomUUID()), this.id, name, isDefault));
    }

    public void addShift(String name, boolean isDefault) {
        shifts.add(new Shift(new ShiftId(UUID.randomUUID()), this.id, name, isDefault));
    }
}
