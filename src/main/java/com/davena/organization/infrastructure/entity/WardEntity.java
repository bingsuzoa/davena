//package com.davena.organization.infrastructure.entity;
//
//import com.davena.organization.domain.model.ward.Ward;
//import jakarta.persistence.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
////@Entity
////@Table(name = "ward")
////public class WardEntity {
////
////    public WardEntity(
////            UUID hospitalId,
////            UUID id,
////            UUID memberId,
////            String name
////    ) {
////        this.hospitalId = hospitalId;
////        this.id = id;
////        this.memberId = memberId;
////        this.name = name;
////    }
////
////    @Id
////    private UUID id;
////
////    private UUID hospitalId;
////
////    private UUID memberId;
////
////    private String name;
////
////    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, orphanRemoval = true)
////    private List<TeamEntity> teams = new ArrayList<>();
////
////    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, orphanRemoval = true)
////    private List<GradeEntity> grades = new ArrayList<>();
////
////    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, orphanRemoval = true)
////    private List<ShiftEntity> shifts = new ArrayList<>();
////
////    public static WardEntity from(Ward ward) {
////        WardEntity entity = new WardEntity(
////                ward.getHospitalId().id(),
////                ward.getId().id(),
////                ward.getSupervisorId().id(),
////                ward.getName()
////        );
////
////        entity.teams = ward.getTeams().stream()
////                .map(team -> TeamEntity.from(entity, team))
////                .toList();
////
////        entity.grades = ward.getGrades().stream()
////                .map(grade -> GradeEntity.from(entity, grade))
////                .toList();
////
////        entity.shifts = ward.getShifts().stream()
////                .map(shift -> ShiftEntity.from(entity, shift))
////                .toList();
////
////        return entity;
////    }
//}
