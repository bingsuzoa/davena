package com.davena.dutymaker.domain.schedule;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.member.MemberState;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
public class Candidate extends BaseEntity {

    public Candidate() {

    }

    private int rank;

    private int score;

    private boolean selected;

    @Column(name = "signature", length = 64) // SHA-256 같은 해시 저장 가정
    private String signature;

    public static final String NOT_FILL_CANDIDATE = "해당 근무표가 채워지지 않았습니다.";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateAssignment> assignments = new ArrayList<>();

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void addAssignment(CandidateAssignment assignment) {
        assignments.add(assignment);
        assignment.setCandidate(this);
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<CandidateAssignment> getLastWeekAssignmentsFor(MemberState member) {
        return getLastWeekAssignments().stream()
                .filter(a -> a.getMember().getId() == member.getMemberId())
                .toList();
    }

    public List<CandidateAssignment> getLastWeekAssignments() {
        if (assignments.isEmpty()) {
            throw new IllegalArgumentException(NOT_FILL_CANDIDATE);
        }

        YearMonth ym = YearMonth.parse(schedule.getYearMonth());
        LocalDate endOfMonth = ym.atEndOfMonth();
        LocalDate startOfLastWeek = endOfMonth.minusDays(6);

        return assignments.stream()
                .filter(a -> !a.getWorkDate().isBefore(startOfLastWeek)
                        && !a.getWorkDate().isAfter(endOfMonth))
                .sorted(Comparator.comparing(CandidateAssignment::getWorkDate))
                .toList();
    }
}
