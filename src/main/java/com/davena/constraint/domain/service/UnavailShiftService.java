package com.davena.constraint.domain.service;

import com.davena.common.WardService;
import com.davena.common.MemberService;
import com.davena.constraint.application.dto.shiftRequest.*;
import com.davena.constraint.domain.model.HolidayRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.UnavailShiftRequest;
import com.davena.constraint.domain.port.HolidayRepository;
import com.davena.constraint.domain.port.UnavailShiftRepository;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnavailShiftService {

    private final UnavailShiftRepository unavailShiftRepository;
    private final WardService wardService;
    private final MemberService memberService;
    private final HolidayRepository holidayRepository;

    public static final String ALREADY_EXIST_SHIFT_REQUEST = "해당 일에 같은 리퀘스트 신청이 존재합니다.";
    public static final String ALREADY_EXIST_HOLIDAY_REQUEST = "해당 일에 휴가 신청 내역이 존재합니다.";

    public WardUnavailShiftResponse getWardUnavailShiftRequests(WardUnavailShiftRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        wardService.verifySupervisorOfWard(ward, request.supervisorId());
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByWardIdAndYearAndMonth(ward.getId(), request.year(), request.month());
        return getWardUnavailShiftResponse(ward, unavailShiftRequests);
    }

    public MemberUnavailShiftsResponse getMemberUnavailShiftRequest(MemberUnavailShiftRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Member member = memberService.getMember(request.memberId());
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByMemberIdAndYearAndMonth(member.getUserId(), request.year(), request.month());
        return getMemberUnavailShiftResponse(ward, member, unavailShiftRequests);
    }

    public MemberUnavailShiftsResponse addMemberUnavailShift(CreateShiftRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Member member = memberService.getMember(request.memberId());
        validateRequestCondition(request, member);
        unavailShiftRepository.save(UnavailShiftRequest.create(member.getUserId(), request.requestDay(), request.shiftId(), request.reason()));
        int year = request.requestDay().getYear();
        int month = request.requestDay().getMonthValue();
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByMemberIdAndYearAndMonth(member.getUserId(), year, month);
        return getMemberUnavailShiftResponse(ward, member, unavailShiftRequests);
    }

    private void validateRequestCondition(CreateShiftRequest request, Member member) {
        Optional<UnavailShiftRequest> optionalShiftRequest = unavailShiftRepository.findByMemberIdAndShiftIdAndRequestDay(member.getUserId(), request.shiftId(), request.requestDay());
        if (optionalShiftRequest.isPresent()) {
            throw new IllegalArgumentException(ALREADY_EXIST_SHIFT_REQUEST);
        }
        Optional<HolidayRequest> optionalHolidayRequest = holidayRepository.findByMemberIdAndRequestDay(member.getUserId(), request.requestDay());
        if(optionalHolidayRequest.isPresent()) {
            throw new IllegalArgumentException(ALREADY_EXIST_HOLIDAY_REQUEST);
        }
    }

    public MemberUnavailShiftsResponse deleteMemberUnavailShift(DeleteShiftRequest request) {
        Ward ward = wardService.getWard(request.wardId());
        Member member = memberService.getMember(request.memberId());
        Optional<UnavailShiftRequest> optionalShiftRequest = unavailShiftRepository.findByMemberIdAndShiftIdAndRequestDay(member.getUserId(), request.shiftId(), request.requestDay());
        if (optionalShiftRequest.isPresent()) {
            unavailShiftRepository.delete(optionalShiftRequest.get().getId());
        }
        int year = request.requestDay().getYear();
        int month = request.requestDay().getMonthValue();
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByMemberIdAndYearAndMonth(member.getUserId(), year, month);
        return getMemberUnavailShiftResponse(ward, member, unavailShiftRequests);
    }

    private MemberUnavailShiftsResponse getMemberUnavailShiftResponse(Ward ward, Member member, List<UnavailShiftRequest> requests) {
        List<UnavailableShiftDto> memberRequests = new ArrayList<>();

        for (UnavailShiftRequest request : requests) {
            memberRequests.add(new UnavailableShiftDto(request.getShiftId(), ward.getShiftName(request.getShiftId())));
        }
        return new MemberUnavailShiftsResponse(ward.getId(), member.getUserId(), member.getName(), memberRequests);
    }


    private WardUnavailShiftResponse getWardUnavailShiftResponse(Ward ward, List<UnavailShiftRequest> requests) {
        Map<UUID, List<UnavailableShiftDto>> memberRequests = requests.stream()
                .collect(Collectors.groupingBy(
                        UnavailShiftRequest::getMemberId,
                        Collectors.mapping(req ->
                                        new UnavailableShiftDto(req.getShiftId(), ward.getShiftName(req.getShiftId())),
                                Collectors.toList()
                        )
                ));

        Map<UUID, MemberUnavailShiftsResponse> allMembersRequests = memberRequests.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            Member member = memberService.getMember(entry.getKey());
                            return new MemberUnavailShiftsResponse(ward.getId(), entry.getKey(), member.getName(), entry.getValue());
                        }
                ));

        return new WardUnavailShiftResponse(allMembersRequests);
    }
}
