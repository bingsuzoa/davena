package com.davena.organization.domain.model.hospital;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Hospital {

    public Hospital(
            UUID id,
            String name
    ) {
        this.id = id;
        this.name = name;
    }

    private UUID id;
    private String name;
}
