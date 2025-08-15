package com.davena.dutymaker.domain;

import com.davena.dutymaker.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "assignment_id"))
public class Assignment {

    protected Assignment() {

    }

    public Assignment(Member member, LocalDate day, ShiftType shift) {
        this.member = member;
        this.day = day;
        this.shift = shift;
        member.addAssignment(this);
        shift.addAssignment(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private LocalDate day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id")
    private ShiftType shift;

    @Column
    private boolean isCharge = false;
}
