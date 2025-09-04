package com.davena.organization.domain.model.ward;

import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.UserId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Ward {

    private Ward(
            HospitalId hospitalId,
            WardId id,
            UserId supervisorId,
            String name,
            String token
    ) {
        this.hospitalId = hospitalId;
        this.id = id;
        this.supervisorId = supervisorId;
        this.name = name;
        this.token = token;
    }

    public static final String DEFAULT_TEAM = "A팀";
    public static final String DEFAULT_GRADE = "1단계";
    public static final String OFF = "off";
    public static final String ALREADY_EXIST_TEAM_NAME = "팀 이름이 중복입니다. 구분해주세요.";
    public static final String ALREADY_EXIST_GRADE_NAME = "숙련도 이름이 중복입니다. 구분해주세요.";
    public static final String ALREADY_EXIST_SHIFT_NAME = "근무명이 중복입니다. 구분해주세요.";

    private HospitalId hospitalId;
    private WardId id;
    private UserId supervisorId;
    private String name;
    private List<Team> teams = new ArrayList<>();
    private List<Grade> grades = new ArrayList<>();
    private List<Shift> shifts = new ArrayList<>();

    private String token;

    public static Ward create(HospitalId hospitalId, UserId supervisorId, String name, String token) {
        Ward ward = new Ward(hospitalId, new WardId(UUID.randomUUID()), supervisorId, name, token);
        ward.createDefault();
        return ward;
    }

    private void createDefault() {
        teams.add(Team.createDefaultTeam(DEFAULT_TEAM, this.getId()));
        grades.add(Grade.createDefaultGrade(DEFAULT_GRADE, this.getId()));
        shifts.add(Shift.createDefaultOff(OFF, this.getId()));
    }

    public boolean isSupervisor(UserId supervisorId) {
        return this.supervisorId == supervisorId ? true : false;
    }

    public TeamId addNewTeam(String name) {
        if(teams.stream().anyMatch(t -> t.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_TEAM_NAME);
        }
        Team newTeam = Team.createTeam(name, this.getId());
        teams.add(newTeam);
        return newTeam.getId();
    }

    public GradeId addNewGrade(String name) {
        if(grades.stream().anyMatch(g -> g.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_GRADE_NAME);
        }
        Grade newGrade = Grade.createGrade(name, this.getId());
        grades.add(newGrade);
        return newGrade.getId();
    }

    public ShiftId addNewShift(String name) {
        if(shifts.stream().anyMatch(s -> s.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_SHIFT_NAME);
        }
        Shift newShift = Shift.createDefaultOff(name, this.getId());
        shifts.add(newShift);
        return newShift.getId();
    }
}
