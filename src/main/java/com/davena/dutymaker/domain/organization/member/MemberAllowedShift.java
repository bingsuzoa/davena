package com.davena.dutymaker.domain.organization.member;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import jakarta.persistence.*;
import lombok.Getter;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "allowed_shift_id"))
public class MemberAllowedShift extends BaseEntity {

    protected MemberAllowedShift() {

    }

    public MemberAllowedShift(Member member, ShiftType shiftType) {
        this.member = member;
        this.shiftType = shiftType;
        member.addAllowedShifts(this);
        shiftType.addAllowedShift(this);
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shift_type_id")
    private ShiftType shiftType;

}
