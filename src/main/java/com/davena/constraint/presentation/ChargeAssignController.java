package com.davena.constraint.presentation;

import com.davena.constraint.application.dto.wardCharge.WardChargeDto;
import com.davena.constraint.application.dto.wardCharge.WardChargeRequest;
import com.davena.constraint.domain.service.ChargeAssignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("charge")
public class ChargeAssignController {
    private final ChargeAssignService chargeAssignService;

    @GetMapping
    public WardChargeDto getWardCharges(@RequestBody WardChargeRequest request) {
        return chargeAssignService.getWardCharges(request);
    }

    @PutMapping
    public WardChargeDto updateWardCharges(@RequestBody WardChargeDto request) {
        return chargeAssignService.updateWardCharges(request);
    }

}
