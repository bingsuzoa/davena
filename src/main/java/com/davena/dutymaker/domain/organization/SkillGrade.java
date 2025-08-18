package com.davena.dutymaker.domain.organization;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_skill_grade_ward_name", columnNames = {"ward_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "skill_grade_id"))
public class SkillGrade extends BaseEntity {

    protected SkillGrade() {

    }

    public SkillGrade(
            Ward ward,
            String name
    ) {
        this.ward = ward;
        this.name = name;
        ward.addSkillGrade(this);
    }

    public static final String NOT_MATCH_GRADE_WITH_WARD_MEMBERS_COUNT = "숙련도에 분류되지 않은 근무자가 존재합니다.";
    public static final String NOT_EXIST_GRADE = "존재하지 않는 숙련도 등급입니다.";
    public static final String DEFAULT_SKILL_GRADE = "1단계";
    public static final String CANNOT_DELETE_DEFAULT_SKILL_GRADE = "기본 숙련도는 삭제할 수 없습니다.";

    @Column
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @OneToMany(mappedBy = "skillGrade")
    private Set<Member> members = new HashSet<>();

    public static SkillGrade ofDefault(Ward ward) {
        SkillGrade skillGrade =  new SkillGrade(ward, DEFAULT_SKILL_GRADE);
        skillGrade.makeDefault(skillGrade);
        return skillGrade;
    }

    public String updateName(String name) {
        this.name = name;
        return name;
    }

    private void makeDefault(SkillGrade skillGrade) {
        skillGrade.isDefault = true;
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void removeMember(Member member) {
        if (!members.contains(member)) {
            members.remove(member);
        }
    }
}
