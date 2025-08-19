package com.davena.dutymaker.api.dto.schedule.payload.draft;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.schedule.Schedule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
public class Draft extends BaseEntity {

    protected Draft() {

    }

    public Draft(Schedule schedule) {
        this.schedule = schedule;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private DraftPayload payload;

    public void updatePayload(DraftPayload payload) {
        this.payload = payload;
    }
}
