package com.davena.dutymaker.api.dto.member;

public record ChargeBox(
        Long teamId,
        String teamName,
        String memberName,
        boolean isCharge,
        int ranking
) {
}
