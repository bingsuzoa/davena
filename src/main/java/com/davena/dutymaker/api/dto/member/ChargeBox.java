package com.davena.dutymaker.api.dto.member;

import com.davena.dutymaker.domain.organization.member.Member;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ChargeBox(
        Long teamId,
        String teamName,
        String memberName,
        boolean isCharge,
        @Min(Member.MIN_RANKING)
        @Max(Member.MAX_RANKING)
        int ranking
) {
}
