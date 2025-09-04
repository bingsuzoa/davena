package com.davena.organization.application.dto.ward;

import com.davena.organization.domain.model.ward.ShiftId;
import com.davena.organization.domain.model.ward.WardId;

import java.util.UUID;

public record ShiftResponse(
        UUID wardId,
        UUID shiftId,
        String name
) {
    public static ShiftResponse from(WardId wardId, ShiftId shiftId, String name) {
        return new ShiftResponse(wardId.id(), shiftId.id(), name);
    }
}
