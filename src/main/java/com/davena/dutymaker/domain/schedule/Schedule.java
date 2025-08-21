package com.davena.dutymaker.domain.schedule;

import com.davena.dutymaker.api.dto.schedule.payload.draft.Draft;
import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.shiftRequirement.ShiftRequirement;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_schedule_ward_month", columnNames = {"ward_id", "year_month"}
        )
})
public class Schedule extends BaseEntity {

    protected Schedule() {

    }

    public Schedule(Ward ward,
                    String yearMonth
    ) {
        this.ward = ward;
        this.yearMonth = yearMonth;
        ward.addSchedule(this);
    }

    public static final String NOT_EXIST_THIS_MONTH_SCHEDULE = "해당 월의 스케줄이 존재하지 않습니다.";
    public static final String NOT_DRAFT_STATE = "이미 헤당 월 근무표가 생성되었거나 확정된 상태입니다. 다시 만들고 싶으시면 초기화 해주세요.";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(length = 7, nullable = false)
    private String yearMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ScheduleStatus status = ScheduleStatus.DRAFT;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_candidate_id")
    private Candidate selectedCandidate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_id")
    private Draft draft;

    @OneToMany(mappedBy = "schedule")
    @OrderBy("selected DESC, rank ASC")
    private List<Candidate> candidates = new ArrayList<>();

    @OneToMany(mappedBy = "schedule")
    private List<ShiftRequirement> requirements = new ArrayList<>();

    public void finalizeStatus() {
        this.status = ScheduleStatus.FINALIZED;
    }

    public void updateDraft(Draft draft) {
        this.draft = draft;
    }
}
