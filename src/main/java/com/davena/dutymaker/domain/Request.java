package com.davena.dutymaker.domain;

import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
public class Request extends BaseEntity {

    protected Request() {
    }

    public Request(Member member,
                   ShiftType shiftType,
                   LocalDate start,
                   LocalDate end
    ) {
        this.member = member;
        this.shiftType = shiftType;
        this.startDate = start;
        this.endDate = end;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id")
    private ShiftType shiftType;

    private LocalDate startDate;

    private LocalDate endDate;
}
