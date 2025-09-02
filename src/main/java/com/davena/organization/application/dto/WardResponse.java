package com.davena.organization.application.dto;

import com.davena.organization.domain.model.ward.Ward;

import java.util.UUID;

public record WardResponse(
        UUID wardId,
        String wardName
) {
    public static WardResponse from(Ward ward) {
        return new WardResponse(ward.getId().id(), ward.getName());
    }
}
