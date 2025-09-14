package com.davena.constraint.presentation;

import com.davena.constraint.application.dto.availabiltyRequest.GetWardAvailabilityRequest;
import com.davena.constraint.application.dto.availabiltyRequest.WardAvailabilityResponse;
import com.davena.constraint.domain.service.AvailabilityRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("request")
public class AvailabilityRequestController {

    private final AvailabilityRequestService requestService;

    public WardAvailabilityResponse getAllMembersRequests(@RequestBody GetWardAvailabilityRequest request) {

    }
}
