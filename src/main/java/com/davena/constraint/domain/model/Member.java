package com.davena.constraint.domain.model;

import com.davena.organization.domain.model.ward.Shift;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Member implements Comparable<Member> {

    public Member(
            UUID userId,
            UUID wardId,
            String name
    ) {
        this.userId = userId;
        this.wardId = wardId;
        this.name = name;
    }

    private UUID userId;
    private UUID wardId;
    private String name;
    private UUID teamId;
    private UUID gradeId;

    private boolean canCharge = true;
    private int rank = LOWEST_RANK;
    private List<PossibleShift> shifts = new ArrayList<>();

    public static final String NOT_EXIST_SHIFT = "해당 멤버에게 등록되지 않은 근무입니다.";
    public static final int LOWEST_RANK = 100;

    public void updateGrade(UUID gradeId) {
        this.gradeId = gradeId;
    }

    public void updateTeam(UUID teamId) {
        this.teamId = teamId;
    }

    public void initDefaultTeam(UUID defaultTeamId) {
        this.teamId = defaultTeamId;
    }


    public void updateCanCharge(boolean canCharge) {
        this.canCharge = canCharge;
    }

    public void updateRank(int rank) {
        this.rank = rank;
    }

    public void addWardNewShift(UUID shiftId, String shiftName) {
        shifts.add(new PossibleShift(shiftId, shiftName));
    }

    public void deleteWardShift(UUID shiftId) {
        PossibleShift shift = getShift(shiftId);
        shifts.remove(shift);
    }


    private PossibleShift getShift(UUID shiftId) {
        return shifts.stream()
                .filter(s -> s.getShiftId().equals(shiftId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_SHIFT));
    }

    public void initPossibleShifts(List<Shift> wardShifts) {
        for (Shift shift : wardShifts) {
            shifts.add(new PossibleShift(shift.getId(), shift.getName()));
        }
    }

    public void updateIsPossibleOfShift(UUID shiftId, boolean isPossible) {
        PossibleShift shift = getShift(shiftId);
        shift.updateIsPossible(isPossible);
    }

    public void updateWardShifts(UUID shiftId, String shiftName) {
        PossibleShift shift = getShift(shiftId);
        shift.updateName(shiftName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return userId.equals(member.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public int compareTo(Member other) {
        return this.rank - other.rank;
    }


}
