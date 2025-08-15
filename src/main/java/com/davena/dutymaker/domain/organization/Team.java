package com.davena.dutymaker.domain.organization;

import com.davena.dutymaker.domain.organization.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_team_group_name", columnNames = {"group_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "team_id"))
public class Team {

    protected Team() {

    }

    public Team(Group group, String name) {
        this.group = group;
        this.name = name;
        group.addTeam(this);
    }

    @Column(nullable = false, length = 10)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

}