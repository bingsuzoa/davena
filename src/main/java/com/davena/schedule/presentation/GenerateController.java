package com.davena.schedule.presentation;

import com.davena.schedule.application.dto.GenerateRequest;
import com.davena.schedule.domain.model.canididate.Candidate;
import com.davena.schedule.domain.service.GenerateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("schedule")
public class GenerateController {

    private final GenerateService generateService;

    private void generate(@RequestBody GenerateRequest generateRequest) {
        List<Candidate> candidates = generateService.generate(generateRequest);
    }
}
