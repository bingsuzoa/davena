package com.davena.dutymaker.api.dto.schedule.payload.draft;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "draft_id"))
public class Draft extends BaseEntity {

    protected Draft() {

    }

    public Draft(Schedule schedule) {
        this.schedule = schedule;
        schedule.updateDraft(this);
    }

    @OneToOne(mappedBy = "draft")
    private Schedule schedule;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = DraftPayloadConverter.class)
    private DraftPayload payload;

    public void updatePayload(DraftPayload payload) {
        this.payload = payload;
    }
}
