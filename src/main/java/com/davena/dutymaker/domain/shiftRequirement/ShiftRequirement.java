package com.davena.dutymaker.domain.shiftRequirement;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.schedule.Schedule;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "shift_requirement_id"))
public class ShiftRequirement extends BaseEntity {

    private ShiftRequirement() {

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    private LocalDate workDate;

    @ManyToOne
    @JoinColumn(name = "shift_type_id")
    private ShiftType shiftType;

    private int required;
}
