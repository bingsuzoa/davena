package com.davena.dutymaker.api.dto.ward;

public record WardRequest(
        Long hospitalId,
        String name
) {
}
