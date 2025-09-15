package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.constraint.application.dto.holidayRequest.*;
import com.davena.constraint.domain.model.HolidayRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.port.HolidayRepository;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final ExistenceService existenceService;

    public static final String ALREADY_EXIST_HOLIDAY_REQUEST = "이미 신청된 휴가신청 내역이 있습니다.";

    public WardHolidayResponse getWardHolidays(WardHolidayRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        List<HolidayRequest> requests = holidayRepository.findByWardIdAndYearAndMonth(ward.getId(), request.year(), request.month());
        return getWardHolidayResponse(ward, requests);
    }

    public MemberHolidayResponse getMemberHolidays(MemberHolidayRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        Member member = existenceService.getMember(request.memberId());
        List<HolidayRequest> requests = holidayRepository.findByMemberIdAndWardIdAndYearAndMonth(ward.getId(), member.getUserId(), request.year(), request.month());
        return getMemberHolidayResponse(ward, member, requests);
    }

    public MemberHolidayResponse addMemberHoliday(CreateHolidayRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        Member member = existenceService.getMember(request.memberId());
        Optional<HolidayRequest> optionalRequest = holidayRepository.findByMemberIdAndWardIdAndRequestDay(ward.getId(), member.getUserId(), request.requestDay());
        if(optionalRequest.isPresent()) {
            throw new IllegalArgumentException(ALREADY_EXIST_HOLIDAY_REQUEST);
        }
        holidayRepository.save(HolidayRequest.create(member.getUserId(), request.requestDay(), request.reason()));

        LocalDate requestDay = request.requestDay();
        List<HolidayRequest> requests = holidayRepository.findByMemberIdAndWardIdAndYearAndMonth(ward.getId(), member.getUserId(), requestDay.getYear(), requestDay.getMonthValue());
        return getMemberHolidayResponse(ward, member, requests);
    }

    public MemberHolidayResponse deleteMemberHoliday(DeleteHolidayRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        Member member = existenceService.getMember(request.memberId());
        Optional<HolidayRequest> optionalRequest = holidayRepository.findByMemberIdAndWardIdAndRequestDay(ward.getId(), member.getUserId(), request.requestDay());
        if(optionalRequest.isPresent()) {
            holidayRepository.delete(optionalRequest.get().getId());
        }
        LocalDate requestDay = request.requestDay();
        List<HolidayRequest> requests = holidayRepository.findByMemberIdAndWardIdAndYearAndMonth(ward.getId(), member.getUserId(), requestDay.getYear(), requestDay.getMonthValue());
        return getMemberHolidayResponse(ward, member, requests);
    }

    private MemberHolidayResponse getMemberHolidayResponse(Ward ward, Member member, List<HolidayRequest> requests) {
        List<HolidayRequestDto> memberRequests = new ArrayList<>();

        for (HolidayRequest request : requests) {
            memberRequests.add(new HolidayRequestDto(request.getId(), request.getRequestDay()));
        }
        return new MemberHolidayResponse(ward.getId(), member.getUserId(), member.getName(), memberRequests);
    }

    private WardHolidayResponse getWardHolidayResponse(Ward ward, List<HolidayRequest> requests) {
        Map<UUID, List<HolidayRequestDto>> memberRequests = new HashMap<>();
        Map<UUID, MemberHolidayResponse> allMembersRequests = new HashMap<>();

        for (HolidayRequest request : requests) {
            Member member = existenceService.getMember(request.getMemberId());
            memberRequests.computeIfAbsent(member.getUserId(), k -> new ArrayList<>())
                    .add(new HolidayRequestDto(request.getId(), request.getRequestDay()));
        }

        for(UUID memberId : memberRequests.keySet()) {
            Member member = existenceService.getMember(memberId);
            List<HolidayRequestDto> memberRequestDtos = memberRequests.containsKey(memberId) ? memberRequests.get(memberId) : new ArrayList<>();
            allMembersRequests.putIfAbsent(memberId, new MemberHolidayResponse(ward.getId(), memberId, member.getName(), memberRequestDtos));
        }
        return new WardHolidayResponse(allMembersRequests);
    }
}
