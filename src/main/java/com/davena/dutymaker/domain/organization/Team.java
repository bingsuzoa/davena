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
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_team_ward_name", columnNames = {"ward_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "team_id"))
public class Team extends BaseEntity {

    protected Team() {

    }

    public Team(Ward ward, String name) {
        this.ward = ward;
        this.name = name;
        ward.addTeam(this);
    }

    public static final String NOT_MATCH_WARD_MEMBERS_COUNT = "팀에 분류되지 않은 근무자가 존재합니다.";

    @Column(nullable = false, length = 10)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @OneToMany(mappedBy = "team")
    private Set<Member> members = new HashSet<>();

    public void removeMember(Member member) {
        if(members.contains(member)) {
            members.remove(member);
        }
    }

    public void addMember(Member member) {
        members.add(member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Team other = (Team) o;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return (getId() == null) ? 0 : getId().hashCode();
    }
}