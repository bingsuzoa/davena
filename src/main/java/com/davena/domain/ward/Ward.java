package com.davena.domain.ward;

import com.davena.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.davena.domain.ward.Team.NOT_EXIST_TEAM;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_ward_hospital_name", columnNames = {"hospital_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "ward_id"))
public class Ward extends BaseEntity {

    private Ward() {

    }

    public Ward(
            Long hospitalId,
            Long supervisorId,
            String name
    ) {
        this.hospitalId = hospitalId;
        this.supervisorId = supervisorId;
        this.name = name;
        initDefaults();
    }

    private static final String ALREADY_APPROVED_MEMBER = "이미 승인된 멤버입니다.";

    @Column(nullable = false)
    private Long hospitalId;

    @Column(nullable = false)
    private Long supervisorId;

    @Column(nullable = false, length = 10)
    private String name;

    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades = new ArrayList<>();

    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShiftType> shifts = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "ward_members", joinColumns = @JoinColumn(name = "ward_id"))
    @Column(name = "member_id")
    private Set<Long> members = new HashSet<>();

    private void initDefaults() {
        teams.add(Team.createDefault(this));
        grades.add(Grade.createDefault(this));
        shifts.add(ShiftType.createOFF(this));
    }

    public Team addTeam(String name) {
        Team team = Team.createNormal(this, name);
        teams.add(team);
        return team;
    }

    public Grade addGrade(String name) {
        Grade grade = Grade.createNormal(this, name);
        grades.add(grade);
        return grade;
    }

    public ShiftType addShift(String name, WorkTime workTime, boolean isWorking) {
        ShiftType shift = ShiftType.createShift(this, name, workTime, isWorking);
        shifts.add(shift);
        return shift;
    }

    public void approveMember(Long memberId) {
        if(members.contains(memberId)) {
            throw new IllegalArgumentException(ALREADY_APPROVED_MEMBER);
        }
        members.add(memberId);
    }

    public void assignMemberToTeam(Long teamId, Long memberId) {
        Team team = getTeam(teamId);

    }

    private Team getTeam(Long teamId) {
        return teams.stream()
                .filter(t -> t.getId() == teamId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_TEAM));
    }


}
