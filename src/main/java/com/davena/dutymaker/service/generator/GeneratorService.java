package com.davena.dutymaker.service.generator;


import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.schedule.Candidate;
import com.davena.dutymaker.domain.schedule.Schedule;
import com.davena.dutymaker.repository.ScheduleRepository;
import com.davena.dutymaker.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneratorService {

    private final ScheduleRepository scheduleRepository;
    private final WardRepository wardRepository;
    private final CandidateService candidateService;


    public void generateCandidates(Long wardId, Long scheduleId) {
        Schedule schedule = getScheduleWithCandidates(scheduleId);
        Ward ward = wardRepository.getWardWithTeams(wardId).orElseThrow();

        int maxCandidates = 10;
        int maxAttempts = 100;
        int attempts = 0;

        while (schedule.getCandidates().size() < maxCandidates && attempts < maxAttempts) {
            try {
                Candidate c = candidateService.generateCandidate(schedule, ward);
                schedule.addCandidate(c);
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("Candidate 생성 실패: " + e.getMessage());
            }
            attempts++;
        }

        System.out.println("🎉🎉🎉두구두구 " + schedule.getCandidates().size());
        if (schedule.getCandidates().isEmpty()) {
            throw new IllegalStateException("Candidate 생성 실패: 가능한 경우 아예 없음");
        }

    }

    private Schedule getScheduleWithCandidates(Long scheduleId) {
        return scheduleRepository.findWithCandidates(scheduleId).orElseThrow(() ->
                new IllegalArgumentException(Schedule.NOT_EXIST_THIS_MONTH_SCHEDULE));
    }

}
