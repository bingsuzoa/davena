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

    public Member(
            String name,
            String nickName,
            String phoneNumber,
            String password) {
        this.name = name;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public static final String NOT_EXIST_MEMBER = "존재하지 않는 근무자입니다.";

    @Column(nullable = false)
    private String name;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Ward ward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_grade_id")
    private SkillGrade skillGrade;

    @OneToMany(mappedBy = "member")
    private List<Assignment> assignments = new ArrayList<>();

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void joinWard(Ward ward, Team defaultTeam, SkillGrade defaultGrade) {
        this.ward = ward;
        this.team = defaultTeam;
        this.skillGrade = defaultGrade;
    }

    public void updateSkillGrade(SkillGrade newSkillGrade) {
        if(newSkillGrade == null) {
            this.skillGrade = newSkillGrade;
            newSkillGrade.addMember(this);
        }
        if(!Objects.equals(this.skillGrade, newSkillGrade)) {
            newSkillGrade.removeMember(this);
            this.skillGrade = newSkillGrade;
            newSkillGrade.addMember(this);
        }
    }

    public void updateTeam(Team newTeam) {
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
