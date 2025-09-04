package com.davena.organization.presentation;

import com.davena.organization.application.dto.ward.WardRequest;
import com.davena.organization.domain.service.WardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/organization")
public class WardController {
    private final WardService wardService;

    @PostMapping("/ward")
    public void createWard(@RequestBody WardRequest request) {
        wardService.createWard(request);
    }

    @GetMapping("/wards/by-token")
    public void findWardByToken(@RequestParam String token) {

    }

    @PostMapping("/members/apply")
    public void apply() {

    }

    @PostMapping("/members/approve")
    public void approve() {

    }

    @PatchMapping("/members/{id}/team")
    public void patchTeam() {

    }

    @PatchMapping("/members/{id}/grade")
    public void patchGrade() {

    }

    @PatchMapping("/members/{id}/shifts")
    public void patchShifts() {

    }

    @PatchMapping("/members/{id}/charge")
    public void patchCharge() {

    }
}
