package com.davena.dutymaker.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "shift_type_id"))
public class ShiftType extends BaseEntity {

    public ShiftType(
            String name,
            LocalTime startTime,
            LocalTime endTime,
            boolean isWorking
    ) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWorking = isWorking;
    }

    @Column(nullable = false, length = 10)
    private String name;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isWorking = true;

    @OneToMany(mappedBy = "shift")
    private List<Assignment> assignments = new ArrayList<>();

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public boolean isNight() {
        if(!isWorking || !endTime.isAfter(startTime) || startTime.equals(LocalTime.MIDNIGHT)) {
            return true;
        }
        return false;
    }
}
