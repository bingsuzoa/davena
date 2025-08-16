package com.davena.dutymaker.domain.organization.member;

import com.davena.dutymaker.domain.Assignment;
import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.SkillGrade;
import com.davena.dutymaker.domain.organization.Team;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "member_id"))
public class Member extends BaseEntity {

    protected Member() {

    }

    public Member(String name) {
        this.name = name;
    }

    public static final String NOT_EXIST_MEMBER = "존재하지 않는 근무자입니다.";

    @Column
    private String name;

    @Column
    private String phoneNumber;

    @OneToMany(mappedBy = "member")
    private List<Assignment> assignments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Ward ward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_grade_id")
    private SkillGrade skillGrade;

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void changeTeam(Team newTeam) {
        if(team == null) {
            team = newTeam;
            newTeam.addMember(this);
            return;
        }
        if(!Objects.equals(team, newTeam)) {
            team.removeMember(this);
            team = newTeam;
            newTeam.addMember(this);
        }
    }

    public void initTeam() {
        if(team != null) {
            team.removeMember(this);
            team = null;
        }
    }

    public String updateWard(Ward newWard) {
        if(ward == null || !Objects.equals(ward, newWard)) {
            ward = newWard;
            ward.addMember(this);
        }
        return ward.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Member other = (Member) o;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return (getId() == null) ? 0 : getId().hashCode();
    }

}
