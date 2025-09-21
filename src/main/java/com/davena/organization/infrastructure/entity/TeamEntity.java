//package com.davena.organization.infrastructure.entity;
//
//import com.davena.organization.domain.model.ward.Team;
//import jakarta.persistence.*;
//import lombok.Getter;
//
//import java.util.UUID;
//
//@Entity
//@Getter
//@Table(name = "team")
//public class TeamEntity {
//
//    private TeamEntity(
//            UUID id,
//            WardEntity ward,
//            String name
//    ) {
//        this.id = id;
//        this.ward = ward;
//        this.name = name;
//    }
//
//    @Id
//    private UUID id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ward_id")
//    private WardEntity ward;
//
//    private String name;
//
//    public static TeamEntity from(WardEntity ward, Team team) {
//        return new TeamEntity(team.getId().id(), ward, team.getName());
//    }
//}
