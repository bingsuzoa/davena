package com.davena.dutymaker.domain.organization;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_skill_grade_ward_name", columnNames = {"ward_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "skill_grade_id"))
public class SkillGrade extends BaseEntity {

    protected SkillGrade() {

    }

    public SkillGrade(Ward ward, String name) {
        this.ward = ward;
        this.name = name;
        ward.addSkillGrade(this);
    }

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @OneToMany(mappedBy = "skillGrade")
    private Set<Member> members = new HashSet<>();
}
