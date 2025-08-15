package com.davena.dutymaker.domain.member;

import com.davena.dutymaker.domain.Assignment;
import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.ShiftType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "member_id"))
public class Member extends BaseEntity {

    protected Member() {

    }

    public Member(String name) {
        this.name = name;
    }

    private String name;

    private String phoneNumber;

    @OneToMany(mappedBy = "member")
    private List<Assignment> assignments = new ArrayList<>();

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }
}
