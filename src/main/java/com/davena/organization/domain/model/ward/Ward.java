package com.davena.organization.domain.model.ward;

import com.davena.organization.application.dto.ward.grade.GradeDto;
import com.davena.organization.application.dto.ward.shift.ShiftDto;
import com.davena.organization.application.dto.ward.team.TeamDto;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public static final String DEFAULT_TEAM = "A팀";
    public static final String DEFAULT_GRADE = "1단계";
    public static final String OFF = "off";
    public static final String ALREADY_EXIST_TEAM_NAME = "팀 이름이 중복입니다. 구분해주세요.";
    public static final String ALREADY_EXIST_GRADE_NAME = "숙련도 이름이 중복입니다. 구분해주세요.";
    public static final String ALREADY_EXIST_SHIFT_NAME = "근무명이 중복입니다. 구분해주세요.";

    public static final String NOT_EXIST_TEAM = "존재하지 않는 팀 입니다.";
    public static final String NOT_EXIST_GRADE = "존재하지 않는 숙련도입니다.";
    public static final String NOT_EXIST_SHIFT = "존재하지 않는 근무입니다.";
    public static final String NOT_EXIST_USER_OF_WARD = "병동에 승인되지 않은 사용자가 포함되어 있습니다.";

    public static final String CAN_NOT_DELETE_DEFAULT = "기본 설정 값은 삭제할 수 없습니다.";

    private UUID hospitalId;
    private UUID id;
    private UUID supervisorId;
    private String name;

    @Getter(AccessLevel.NONE)
    private List<Team> teams = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private List<Grade> grades = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private Map<DayType, List<Shift>> shifts = new HashMap<>();

    private Set<UUID> users = new HashSet<>();

    private String token;

    public static Ward create(UUID hospitalId, UUID supervisorId, String name, String token) {
        Ward ward = new Ward(hospitalId, UUID.randomUUID(), supervisorId, name, token);
        ward.createDefault();
        return ward;
    }

    private void createDefault() {
        teams.add(Team.createDefaultTeam(DEFAULT_TEAM, this.getId()));
        grades.add(Grade.createDefaultGrade(DEFAULT_GRADE, this.getId()));
        shifts = Shift.getDefaultShifts(this.getId());
    }

    public List<TeamDto> getTeams() {
        return teams.stream()
                .map(team -> new TeamDto(team.getId(), team.getName(), team.isDefault()))
                .toList();
    }

    public UUID addNewTeam(String name) {
        if (teams.stream().anyMatch(t -> t.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_TEAM_NAME);
        }
        Team newTeam = Team.createTeam(name, this.getId());
        teams.add(newTeam);
        return newTeam.getId();
    }

    public UUID deleteTeam(UUID teamId) {
        Team team = teams.stream()
                .filter(t -> t.getId().equals(teamId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_TEAM));
        team.validateRemovableTeam();
        teams.remove(team);
        return teamId;
    }

    public List<GradeDto> getGrades() {
        return grades.stream()
                .map(grade -> new GradeDto(grade.getId(), grade.getName(), grade.isDefault()))
                .toList();
    }

    public UUID addNewGrade(String name) {
        if (grades.stream().anyMatch(g -> g.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_GRADE_NAME);
        }
        Grade newGrade = Grade.createGrade(name, this.getId());
        grades.add(newGrade);
        return newGrade.getId();
    }

    public UUID deleteGrade(UUID gradeId) {
        Grade grade = grades.stream()
                .filter(g -> g.getId().equals(gradeId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_GRADE));
        grade.validateRemovableGrade();
        grades.remove(grade);
        return gradeId;
    }

    public Map<DayType, List<ShiftDto>> getShifts() {
        return shifts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(Shift::toDto)
                                .toList()
                ));
    }

    public UUID addNewShift(DayType dayType, String name, LocalTime start, LocalTime end) {
        List<Shift> shifts = this.shifts.get(dayType);
        if (shifts.stream().anyMatch(shift -> shift.getName().equals(name))) {
            throw new IllegalArgumentException(ALREADY_EXIST_SHIFT_NAME);
        }

        Shift newShift = Shift.addNewShift(this.getId(), name, start, end);
        shifts.add(newShift);
        return newShift.getId();
    }

    public UUID deleteShift(DayType dayType, UUID shiftId) {
        List<Shift> shifts = this.shifts.get(dayType);
        if (shifts == null || shifts.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_SHIFT);
        }
        Iterator<Shift> iterator = shifts.iterator();
        while (iterator.hasNext()) {
            Shift shift = iterator.next();
            if (shift.getId().equals(shiftId)) {
                if (shift.isDefault()) {
                    throw new IllegalArgumentException(CAN_NOT_DELETE_DEFAULT);
                }
                iterator.remove();
                return shiftId;
            }
        }
        throw new IllegalArgumentException(NOT_EXIST_SHIFT);
    }

    public UUID updateShift(UUID shiftId, DayType dayType, String name, LocalTime start, LocalTime end) {
        List<Shift> shiftList = shifts.get(dayType);
        Optional<Shift> optionalShift = shiftList.stream().filter(s -> s.getId().equals(shiftId)).findFirst();
        if(optionalShift.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_SHIFT);
        }
        Shift shift = optionalShift.get();
        shift.update(name, start, end);
        return shiftId;
    }

    public List<UUID> getUsersOfTeam(UUID teamId) {
        return teams.stream()
                .filter(team -> team.getId().equals(teamId))
                .findFirst()
                .map(Team::getUsers)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_TEAM));
    }

    public boolean isSupervisor(UUID supervisorId) {
        return this.supervisorId == supervisorId ? true : false;
    }

    public UUID addNewUser(UUID userId) {
        users.add(userId);
        addUserToDefaultTeam(userId);
        return userId;
    }

    private void addUserToDefaultTeam(UUID userId) {
        Team defaultTeam = teams.getFirst();
        defaultTeam.addNewUser(userId);
    }

    public UUID setUsersToTeam(UUID teamId, List<UUID> users) {
        Team team = teams.stream()
                .filter(t -> t.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_TEAM));
        if (!users.stream().allMatch(this.users::contains)) {
            throw new IllegalArgumentException(NOT_EXIST_USER_OF_WARD);
        }

        team.updateUsers(users);
        return teamId;
    }

    public UUID setUsersToGrade(UUID gradeId, List<UUID> users) {
        Grade grade = grades.stream()
                .filter(g -> g.getId().equals(gradeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_GRADE));
        if (!users.stream().allMatch(this.users::contains)) {
            throw new IllegalArgumentException(NOT_EXIST_USER_OF_WARD);
        }
        grade.updateUsers(users);
        return gradeId;
    }

    public void clearAllTeamMembers() {
        teams.forEach(team -> team.clearUsers());
    }

    public void clearAllGradeMembers() {
        grades.forEach(grade -> grade.clearUsers());
    }

    public Map<TeamDto, List<UUID>> getTeamUsers() {
        Map<TeamDto, List<UUID>> teamUsers = new HashMap<>();
        for (Team team : teams) {
            teamUsers.put(new TeamDto(team.getId(), team.getName(), team.isDefault()), team.getUsers());
        }
        return teamUsers;
    }

    public Map<GradeDto, List<UUID>> getGradeUsers() {
        Map<GradeDto, List<UUID>> gradeUsers = new HashMap<>();
        for (Grade grade : grades) {
            gradeUsers.put(new GradeDto(grade.getId(), grade.getName(), grade.isDefault()), grade.getUsers());
        }
        return gradeUsers;
    }
}
