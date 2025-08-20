package com.davena.dutymaker.api.dto.member;

import java.util.Map;

public record ChargeRequest(Map<Long, ChargeBox> chargeMap) {
}
