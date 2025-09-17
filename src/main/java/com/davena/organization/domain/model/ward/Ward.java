package com.davena.organization.domain.model.ward;

import com.davena.constraint.domain.model.Member;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalTime;
import java.util.*;

import static com.davena.organization.domain.model.ward.Grade.DEFAULT_GRADE;
import static com.davena.organization.domain.model.ward.Team.DEFAULT_TEAM;

@Getter
public class Ward {

    private Ward(
            UUID hospitalId,
            UUID id,
            UUID supervisorId,
            String name,
            String token
    ) {
        this.hospitalId = hospitalId;
        this.id = id;
        this.supervisorId = supervisorId;
        this.name = name;
        this.token = token;
    }

    public static final String ALREADY_EXIST_TEAM_NAME = "팀 이름이 중복입니다. 구분해주세요.";
    public static final String ALREADY_EXIST_GRADE_NAME = "숙련도 이름이 중복입니다. 구분해주세요.";
    public static final String ALREADY_EXIST_SHIFT_NAME = "근무명이 중복입니다. 구분해주세요.";

    public static final String NOT_EXIST_TEAM = "존재하지 않는 팀 입니다.";
    public static final String NOT_EXIST_GRADE = "존재하지 않는 숙련도입니다.";
    public static final String NOT_EXIST_SHIFT = "존재하지 않는 근무입니다.";
    public static final String NOT_EXIST_USER_OF_WARD = "병동에 승인되지 않은 사용자가 포함되어 있습니다.";

    private UUID hospitalId;
    private UUID id;
    private UUID supervisorId;
    private String name;

    @Getter(AccessLevel.NONE)
    private List<Team> teams = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private List<Grade> grades = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private List<Shift> shifts = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private List<Member> members = new ArrayList<>();

    private Set<UUID> users = new HashSet<>();

    private Map<UUID, Map<UUID, Integer>> requirements = new HashMap<>();

    private String token;

    public static Ward create(UUID hospitalId, UUID supervisorId, String name, String token) {
        Ward ward = new Ward(hospitalId, UUID.randomUUID(), supervisorId, name, token);
        ward.createDefault();
        return ward;
    }

    private void createDefault() {
        Team defaultTeam = new Team(UUID.randomUUID(), this.getId(), DEFAULT_TEAM, true);
        teams.add(defaultTeam);
        grades.add(new Grade(UUID.randomUUID(), this.getId(), DEFAULT_GRADE, true));

        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKDAY, Shift.Day, false, new WorkTime(LocalTime.of(07, 30), LocalTime.of(16, 30))));
        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKDAY, Shift.Eve, false, new WorkTime(LocalTime.of(16, 00), LocalTime.of(0, 0))));
        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKDAY, Shift.Nig, false, new WorkTime(LocalTime.of(0, 0), LocalTime.of(8, 0))));
        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKDAY, Shift.Off, true, null));
        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKEND, Shift.Day, false, new WorkTime(LocalTime.of(8, 00), LocalTime.of(16, 00))));
        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKEND, Shift.Eve, false, new WorkTime(LocalTime.of(16, 00), LocalTime.of(0, 0))));
        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKEND, Shift.Nig, false, new WorkTime(LocalTime.of(0, 0), LocalTime.of(8, 0))));
        shifts.add(new Shift(UUID.randomUUID(), this.getId(), DayType.WEEKEND, Shift.Off, true, null));

        initRequirementsOfNewTeam(defaultTeam.getId());
    }

    private void initRequirementsOfNewTeam(UUID teamId) {
        requirements.put(teamId, new HashMap<>());
        Map<UUID, Integer> requirementsOfTeam = new HashMap<>();
        for (Shift shift : shifts) {
            requirementsOfTeam.putIfAbsent(shift.getId(), 0);
        }
    }

    public UUID addNewTeam(String name) {
        if (teams.stream().anyMatch(t -> t.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_TEAM_NAME);
        }
        Team newTeam = new Team(UUID.randomUUID(), this.getId(), name, false);
        teams.add(newTeam);
        initRequirementsOfNewTeam(newTeam.getId());
        return newTeam.getId();
    }

    public UUID addNewGrade(String name) {
        if (grades.stream().anyMatch(g -> g.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_GRADE_NAME);
        }
        Grade newGrade = new Grade(UUID.randomUUID(), this.getId(), name, false);
        grades.add(newGrade);
        return newGrade.getId();
    }

    public UUID addNewShift(DayType dayType, String name, int startHour, int startMinute, int endHour, int endMinute) {
        if (shifts.stream().anyMatch(shift -> shift.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_SHIFT_NAME);
        }
        LocalTime start = Shift.toLocalTime(startHour, startMinute);
        LocalTime end = Shift.toLocalTime(endHour, endMinute);
        Shift newShift = new Shift(UUID.randomUUID(), this.getId(), dayType, name, false, new WorkTime(start, end));
        shifts.add(newShift);
        return newShift.getId();
    }

    public UUID deleteTeam(UUID teamId) {
        Team team = teams.stream()
                .filter(t -> t.getId().equals(teamId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_TEAM));
        team.validateDefaultTeam();
        teams.remove(team);
        return teamId;
    }

    public UUID deleteGrade(UUID gradeId) {
        Grade grade = grades.stream()
                .filter(g -> g.getId().equals(gradeId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_GRADE));
        grade.validateDefaultGrade();
        grades.remove(grade);
        return gradeId;
    }

    public UUID deleteShift(UUID shiftId) {
        Shift shift = getShift(shiftId);
        shifts.remove(shift);
        return shiftId;
    }

    public UUID updateShift(UUID shiftId, DayType dayType, String name, int startHour, int startMinute, int endHour, int endMinute) {
        Shift shift = getShift(shiftId);
        shift.updateShift(dayType, name, startHour, startMinute, endHour, endMinute);
        return shiftId;
    }

    public void updateRequirement(UUID teamId, UUID shiftId, int updatedCount) {
        Team team = getTeam(teamId);
        Map<UUID, Integer> shiftsRequirement = requirements.get(team.getId());

        Shift shift = getShift(shiftId);
        shiftsRequirement.put(shift.getId(), updatedCount);
    }

    public boolean isSupervisor(UUID supervisorId) {
        return this.supervisorId == supervisorId ? true : false;
    }


    public Team getTeam(UUID teamId) {
        return teams.stream()
                .filter(t -> t.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_TEAM));
    }

    public Grade getGrade(UUID gradeId) {
        return grades.stream()
                .filter(g -> g.getId().equals(gradeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_GRADE));
    }

    public Shift getShift(UUID shiftId) {
        return shifts.stream()
                .filter(s -> s.getId().equals(shiftId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SHIFT));
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public List<Grade> getGrades() {
        return Collections.unmodifiableList(grades);
    }

    public List<Shift> getShifts() {
        return Collections.unmodifiableList(shifts);
    }

    public String getShiftName(UUID shiftId) {
        return this.shifts.stream()
                .filter(shift -> shift.getId().equals(shiftId))
                .map(Shift::getName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SHIFT));
    }
}
