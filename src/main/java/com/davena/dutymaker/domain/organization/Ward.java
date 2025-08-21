package com.davena.dutymaker.domain.organization;


import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_ward_hospital_name", columnNames = {"hospital_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "ward_id"))
public class Ward extends BaseEntity {

    protected Ward() {

    }

    public Ward(
            Hospital hospital,
            Member supervisor,
            String name
    ) {
        this.hospital = hospital;
        this.supervisor = supervisor;
        this.name = name;
        hospital.addWard(this);
        addTeam(Team.ofDefault(this));
        addSkillGrade(SkillGrade.ofDefault(this));
    }

    public static final String NOT_EXIST_WARD = "존재하지 않는 병동입니다.";
    public static final String NOT_EXIST_WARD_SHIFT_RULES = "현재 병동에 적용된 근무 규칙이 없습니다.";
    public static final String NOT_EXIST_WARD_SHIFT_TYPES = "현재 병동에 적용된 근무 타입이 없습니다.";
    public static final String NOT_EXIST_DEFAULT_TEAM = "병동의 기본 팀이 존재하지 않습니다.";
    public static final String NOT_EXIST_DEFAULT_GRADE = "병동의 기본 숙련도가 존재하지 않습니다.";

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", unique = true)
    private Member supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "ward", cascade = CascadeType.PERSIST)
    private Set<Team> teams = new HashSet<>();

    @OneToMany(mappedBy = "ward")
    private Set<Member> members = new HashSet<>();

    @OneToMany(mappedBy = "ward", cascade = CascadeType.PERSIST)
    private Set<SkillGrade> skillGrades = new HashSet<>();

    @OneToMany(mappedBy = "ward")
    private Set<ShiftType> shiftTypes = new HashSet<>();

    @OneToMany(mappedBy = "ward")
    private List<Schedule> schedules = new ArrayList<>();

    public void addShiftType(ShiftType type) {
        shiftTypes.add(type);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void addSkillGrade(SkillGrade grade) {
        skillGrades.add(grade);
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ward other = (Ward) o;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return (getId() == null) ? 0 : getId().hashCode();
    }
}
