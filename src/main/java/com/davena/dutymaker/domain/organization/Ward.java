package com.davena.dutymaker.domain.organization;


import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.Hibernate;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_ward_hospital_name", columnNames = {"hospital_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "ward_id"))
public class Ward extends BaseEntity {

    protected Ward() {

    }

    public Ward(Hospital hospital, String name) {
        this.hospital = hospital;
        this.name = name;
        hospital.addWard(this);
    }

    public static final String NOT_EXIST_WARD = "존재하지 않는 병동입니다.";

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "ward")
    private Set<Team> teams = new HashSet<>();

    @OneToMany(mappedBy = "ward")
    private Set<Member> members = new HashSet<>();

    @OneToMany(mappedBy = "ward")
    private Set<SkillGrade> skillGrades = new HashSet<>();

    public void addMember(Member member) {
        members.add(member);
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void addSkillGrade(SkillGrade grade) {
        skillGrades.add(grade);
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
