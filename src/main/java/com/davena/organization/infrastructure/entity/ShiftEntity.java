package com.davena.organization.infrastructure.entity;

import com.davena.organization.domain.model.shift.Shift;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "shift")
public class ShiftEntity {

    private ShiftEntity(
            UUID id,
            WardEntity ward,
            String name
    ) {
        this.id = id;
        this.ward = ward;
        this.name = name;
    }

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private WardEntity ward;

    private String name;

    public static ShiftEntity from(WardEntity ward, Shift shift) {
        return new ShiftEntity(shift.getId().id(), ward, shift.getName());
    }
}
