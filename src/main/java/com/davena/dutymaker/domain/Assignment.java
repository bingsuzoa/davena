package com.davena.dutymaker.domain;

import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "assignment_id"))
@Table(uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_date_shift",
                        columnNames = {"member_id", "duty_date", "shift_type_id"}
                )
        }
)
public class Assignment extends BaseEntity {

    protected Assignment() {

    }

    public Assignment(Member member, LocalDate dutyDate, ShiftType shiftType, boolean isCharge) {
        this.member = member;
        this.dutyDate = dutyDate;
        this.shiftType = shiftType;
        this.isCharge = isCharge;
        member.addAssignment(this);
        shiftType.addAssignment(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "duty_date")
    private LocalDate dutyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id", nullable = false)
    private ShiftType shiftType;

    @Column(name = "is_charge", nullable = false)
    private boolean isCharge = false;
}
