package com.davena.dutymaker.domain.schedule;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_cand_member_day",
        columnNames = {"candidate_id", "member_id", "work_date"}
))
public class CandidateAssignment extends BaseEntity {

    protected CandidateAssignment() {

    }

    public CandidateAssignment(
            Member member,
            LocalDate workDate,
            ShiftType shiftType,
            boolean isCharge
    ) {
        this.member = member;
        this.workDate = workDate;
        this.shiftType = shiftType;
        this.isCharge = isCharge;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id", nullable = false)
    private ShiftType shiftType;

    @Column(name = "is_charge", nullable = false)
    private boolean isCharge;

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
}
