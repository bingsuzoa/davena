package com.davena.dutymaker.domain.organization;


import com.davena.dutymaker.domain.organization.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_group_hospital_name", columnNames = {"hospital_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "group_id"))
public class Group {

    protected Group() {

    }

    public Group(Hospital hospital, String name) {
        this.hospital = hospital;
        this.name = name;
        hospital.addGroup(this);
    }

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "group")
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    private List<Member> members = new ArrayList<>();

    public void addTeam(Team team) {
        teams.add(team);
    }
}
