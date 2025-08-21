package com.davena.dutymaker.domain.schedule;

import com.davena.dutymaker.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Candidate extends BaseEntity {

    public static final String NOT_EXIST_CANDIDATE = "존재하지 않는 근무표 초안입니다.";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    private int rank;

    private int score;

    private boolean selected;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateAssignments> assignments = new ArrayList<>();
}
