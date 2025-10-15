package com.davena.dutymaker.service;

import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.constraint.domain.model.Member;
import com.davena.dutymaker.application.dto.AssignmentDto;
import com.davena.dutymaker.application.dto.CandidateDto;
import com.davena.dutymaker.application.dto.ScheduleDto;
import com.davena.dutymaker.application.dto.request.CreateScheduleRequest;
import com.davena.dutymaker.application.dto.request.UpdateCellRequest;
import com.davena.dutymaker.domain.model.schedule.Candidate;
import com.davena.dutymaker.domain.model.schedule.Cell;
import com.davena.dutymaker.domain.model.schedule.Schedule;
import com.davena.dutymaker.domain.port.CandidateRepository;
import com.davena.dutymaker.domain.port.CellRepository;
import com.davena.dutymaker.domain.port.ScheduleRepository;
import com.davena.dutymaker.domain.service.ScheduleCreateService;
import com.davena.dutymaker.domain.service.ScheduleReadService;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleCreateServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private CellRepository cellRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private WardService wardService;

    @InjectMocks
    private ScheduleCreateService scheduleCreateService;

    private ScheduleReadService scheduleReadService;

    @BeforeEach
    void setUp() {
        scheduleReadService = new ScheduleReadService(scheduleRepository, memberService, wardService);
        ReflectionTestUtils.setField(scheduleCreateService, "scheduleReadService", scheduleReadService);
    }

    @Test
    @DisplayName("Schedule 객체 생성 확인")
    public void createNewSchedule() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        CreateScheduleRequest request = new CreateScheduleRequest(ward.getId(), 2025, 11);

        Schedule schedule = new Schedule(ward.getId(), 2025, 11);
        Candidate candidate = new Candidate(schedule.getId());

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());

        when(scheduleRepository.saveSchedule(any())).thenReturn(schedule);
        when(candidateRepository.saveCandidate(any())).thenReturn(candidate);
        when(memberService.getAllMembersOfWard(any())).thenReturn(List.of(member1, member2));
        when(wardService.getWard(any())).thenReturn(ward);

        when(cellRepository.saveCell(any(Cell.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(memberService.getMember(member1.getUserId())).thenReturn(member1);
        when(memberService.getMember(member2.getUserId())).thenReturn(member2);

        ScheduleDto scheduleDto = scheduleCreateService.createNewSchedule(request);

        Assertions.assertEquals(scheduleDto.lastDate(), 30);

        Map<Integer, List<AssignmentDto>> cells = scheduleDto.candidates().getFirst().dailyAssignments();
        Assertions.assertEquals(30, cells.size());

        for (List<AssignmentDto> assignments : cells.values()) {
            Assertions.assertEquals(2, assignments.size());
        }

        long totalCells = cells.values().stream().mapToLong(List::size).sum();
        Assertions.assertEquals(60, totalCells);
    }

    @Test
    @DisplayName("지난달 근무 직접 입력하기")
    public void saveCustomSchedule() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        Schedule schedule = new Schedule(ward.getId(), 2025, 10);
        Candidate candidate = new Candidate(schedule.getId());
        schedule.addCandidate(candidate);

        User user1 = User.create("name1", "loginId1", "password", "01011112222");
        Member member1 = new Member(user1.getId(), ward.getId(), user1.getName());
        User user2 = User.create("name2", "loginId2", "password", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());

        for (int day = 1; day <= 3; day++) {
            candidate.addCell(new Cell(candidate.getId(), member1.getUserId(), day, null));
            candidate.addCell(new Cell(candidate.getId(), member2.getUserId(), day, null));
        }

        Shift shift = ward.getShifts().getFirst();

        Map<Integer, List<AssignmentDto>> assignmentDtos = new HashMap<>();
        assignmentDtos.put(2, List.of(
                new AssignmentDto(candidate.getCells().get(2).getId(), member1.getUserId(), shift.getId(), "name1", "day"),
                new AssignmentDto(candidate.getCells().get(3).getId(), member2.getUserId(), shift.getId(), "name2", "day")
        ));


        when(scheduleRepository.findById(any())).thenReturn(Optional.of(schedule));
        when(wardService.getWard(any())).thenReturn(ward);
        when(memberService.getMember(member1.getUserId())).thenReturn(member1);
        when(memberService.getMember(member2.getUserId())).thenReturn(member2);

        ScheduleDto dto = scheduleCreateService.saveCustomSchedule(
                new UpdateCellRequest(ward.getId(), schedule.getId(), candidate.getId(), assignmentDtos)
        );

        List<Cell> updatedCells = schedule.getCandidates().getFirst().getCells();
        Cell target1 = updatedCells.stream()
                .filter(c -> c.getMemberId().equals(member1.getUserId()) && c.getWorkDay() == 2)
                .findFirst().orElseThrow();

        Assertions.assertEquals(shift.getId(), target1.getShiftId(), "member1의 2일차 근무가 올바르게 주입되어야 함");

        Cell target2 = updatedCells.stream()
                .filter(c -> c.getMemberId().equals(member2.getUserId()) && c.getWorkDay() == 2)
                .findFirst().orElseThrow();

        Assertions.assertEquals(shift.getId(), target2.getShiftId(), "member2의 2일차 근무가 올바르게 주입되어야 함");
    }
}
