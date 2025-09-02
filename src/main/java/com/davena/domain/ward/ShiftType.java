package com.davena.domain.ward;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;


@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_shift_ward_name", columnNames = {"ward_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "shift_id"))
public class ShiftType {

    protected ShiftType() {

    }

    public ShiftType(
            Ward ward,
            String name,
            WorkTime workTime,
            boolean isWorking
    ) {
        this.ward = ward;
        this.name = name;
        this.workTime = workTime;
        this.isWorking = isWorking;
    }

    public static final String off = "Off";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false)
    private WorkTime workTime;

    @Column(nullable = false)
    private boolean isWorking;

    public static ShiftType createOFF(Ward ward) {
        WorkTime offTime = new WorkTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);
        return new ShiftType(ward, off, offTime, false);
    }

    public static ShiftType createShift(Ward ward, String name, WorkTime workTime, boolean isWorking) {
        return new ShiftType(ward, name, workTime, isWorking);
    }
}
