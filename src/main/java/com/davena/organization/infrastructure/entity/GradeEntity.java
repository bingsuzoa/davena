package com.davena.organization.infrastructure.entity;

import com.davena.organization.domain.model.grade.Grade;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@Table(name = "grade")
public class GradeEntity {

    private GradeEntity(
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

    public static GradeEntity from(WardEntity entity, Grade grade) {
        return new GradeEntity(grade.getId().id(), entity, grade.getName());
    }
}
