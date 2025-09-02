package com.davena.organization.domain.model.hospital;

import lombok.Getter;

@Getter
public class Hospital {

    public Hospital(
            HospitalId id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    private HospitalId id;
    private String name;
}
