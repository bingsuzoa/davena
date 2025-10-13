package com.davena.constraint.domain.service;

import com.davena.common.WardService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.holidayRequest.*;
import com.davena.constraint.domain.model.HolidayRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.UnavailShiftRequest;
import com.davena.constraint.domain.port.HolidayRepository;
import com.davena.constraint.domain.port.UnavailShiftRepository;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final WardService wardService;
    private final MemberService memberService;
    private final UnavailShiftRepository unavailShiftRepository;

    public static final String ALREADY_EXIST_HOLIDAY_REQUEST = "이미 신청된 휴가신청 내역이 있습니다.";
    public static final String NOT_EXIST_HOLIDAY = "해당 날짜에 휴가를 신청한 내역이 없습니다.";
    public static final String ALREADY_EXIST_SHIFT_REQUEST = "해당 일에 이미 신청된 불가능 근무 내역이 있습니다.";

    public WardHolidayResponse getWardHolidays(WardHolidayRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, request.supervisorId());
        List<HolidayRequest> requests = holidayRepository.findByWardIdAndYearAndMonth(ward.getId(), request.year(), request.month());
        return getWardHolidayResponse(ward, requests);
    }

    public MemberHolidayResponse getMemberHolidays(MemberHolidayRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Member member = memberService.getMember(request.memberId());
        List<HolidayRequest> requests = holidayRepository.findByMemberIdAndYearAndMonth(member.getUserId(), request.year(), request.month());
        return getMemberHolidayResponse(ward, member, requests);
    }

    public Set<UUID> getMemberHolidaysByYearAndMonth(UUID memberId, int year, int month) {
        List<HolidayRequest> requests = holidayRepository.findByMemberIdAndYearAndMonth(memberId, year, month);
        Set<UUID> holidaysSet = new HashSet<>();
        for(HolidayRequest request : requests) {
            holidaysSet.add(request.getId());
        }
        return holidaysSet;
    }

    public MemberHolidayResponse addMemberHoliday(CreateHolidayRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Member member = memberService.getMember(request.memberId());
        holidayRepository.save(HolidayRequest.create(member.getUserId(), request.requestDay(), request.reason()));
        validateRequestCondition(request, member);
        LocalDate requestDay = request.requestDay();
        List<HolidayRequest> requests = holidayRepository.findByMemberIdAndYearAndMonth(member.getUserId(), requestDay.getYear(), requestDay.getMonthValue());
        return getMemberHolidayResponse(ward, member, requests);
    }

    private void validateRequestCondition(CreateHolidayRequest request, Member member) {
        Optional<HolidayRequest> optionalRequest = holidayRepository.findByMemberIdAndRequestDay(member.getUserId(), request.requestDay());
        if(optionalRequest.isPresent()) {
            throw new IllegalArgumentException(ALREADY_EXIST_HOLIDAY_REQUEST);
        }
        List<UnavailShiftRequest> shiftRequests = unavailShiftRepository.findByMemberIdAndRequestDay(member.getUserId(), request.requestDay());
        if(!shiftRequests.isEmpty()) {
            throw new IllegalArgumentException(ALREADY_EXIST_SHIFT_REQUEST);
        }
    }

    public MemberHolidayResponse deleteMemberHoliday(DeleteHolidayRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Member member = memberService.getMember(request.memberId());
        Optional<HolidayRequest> optionalRequest = holidayRepository.findByMemberIdAndRequestDay(member.getUserId(), request.requestDay());
        if(optionalRequest.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_HOLIDAY);
        }
        holidayRepository.delete(optionalRequest.get().getId());
        LocalDate requestDay = request.requestDay();
        List<HolidayRequest> requests = holidayRepository.findByMemberIdAndYearAndMonth(member.getUserId(), requestDay.getYear(), requestDay.getMonthValue());
        return getMemberHolidayResponse(ward, member, requests);
    }

    private MemberHolidayResponse getMemberHolidayResponse(Ward ward, Member member, List<HolidayRequest> requests) {
        List<HolidayRequestDto> memberRequests = new ArrayList<>();

        for (HolidayRequest request : requests) {
            LocalDate requestDay = request.getRequestDay();
            int year = requestDay.getYear();
            int month = requestDay.getMonthValue();
            int day = requestDay.getDayOfMonth();
            memberRequests.add(new HolidayRequestDto(request.getId(), year, month, day));
        }
        return new MemberHolidayResponse(ward.getId(), member.getUserId(), member.getName(), memberRequests);
    }

    private WardHolidayResponse getWardHolidayResponse(Ward ward, List<HolidayRequest> requests) {
        Map<UUID, List<HolidayRequestDto>> memberRequests = new HashMap<>();
        Map<UUID, MemberHolidayResponse> allMembersRequests = new HashMap<>();

        for (HolidayRequest request : requests) {
            Member member = memberService.getMember(request.getMemberId());

            List<HolidayRequestDto> requestDtos =
                    memberRequests.computeIfAbsent(member.getUserId(), k -> new ArrayList<>());

            LocalDate requestDay = request.getRequestDay();
            int year = requestDay.getYear();
            int month = requestDay.getMonthValue();
            int day = requestDay.getDayOfMonth();
            requestDtos.add(new HolidayRequestDto(request.getId(), year, month, day));

            allMembersRequests.putIfAbsent(
                    member.getUserId(),
                    new MemberHolidayResponse(ward.getId(), member.getUserId(), member.getName(), requestDtos)
            );
        }

        return new WardHolidayResponse(allMembersRequests);
    }
}
