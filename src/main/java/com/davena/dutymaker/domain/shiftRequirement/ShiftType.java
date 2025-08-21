package com.davena.dutymaker.domain.shiftRequirement;

import com.davena.dutymaker.domain.Assignment;
import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.Request;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.organization.member.MemberAllowedShift;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "shift_type_id"))
public class ShiftType extends BaseEntity {

    protected ShiftType() {

    }

    public ShiftType(
            Ward ward,
            String name,
            LocalTime startTime,
            LocalTime endTime,
            boolean isWorking
    ) {
        this.ward = ward;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWorking = isWorking;
        ward.addShiftType(this);
    }

    public static final String NOT_EXIST_SHIFT_TYPE = "존재하지 않는 근무 유형입니다.";
    public static final String OFF = "OFF";

    @Column(nullable = false, length = 10)
    private String name;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isWorking = true;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @OneToMany(mappedBy = "shiftType")
    private List<Assignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "shiftType")
    private List<MemberAllowedShift> allowedShifts = new ArrayList<>();

    @OneToMany(mappedBy = "shiftType")
    private List<RequirementRule> requirementRules = new ArrayList<>();

    @OneToMany(mappedBy = "shiftType")
    private List<Request> requests = new ArrayList<>();

    public void addAllowedShift(MemberAllowedShift allowedShift) {
        allowedShifts.add(allowedShift);
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public boolean isNight() {
        if (!isWorking || !endTime.isAfter(startTime) || startTime.equals(LocalTime.MIDNIGHT)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShiftType)) return false;
        ShiftType other = (ShiftType) o;
        return this.getId() != null && this.getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
