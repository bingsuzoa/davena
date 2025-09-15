package com.davena.constraint.domain.service;

import com.davena.common.ExistenceService;
import com.davena.constraint.application.dto.shiftRequest.*;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.UnavailShiftRequest;
import com.davena.constraint.domain.port.UnavailShiftRepository;
import com.davena.organization.domain.model.ward.Ward;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UnavailShiftService {

    private final UnavailShiftRepository unavailShiftRepository;
    private final ExistenceService existenceService;

    public static final String ALREADY_EXIST_SHIFT_REQUEST = "해당 일에 같은 리퀘스트 신청이 존재합니다.";

    public WardUnavailShiftResponse getWardUnavailShiftRequests(WardUnavailShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        existenceService.verifySupervisor(ward, request.supervisorId());
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByWardIdAndYearAndMonth(ward.getId(), request.year(), request.month());
        return getWardUnavailShiftResponse(ward, unavailShiftRequests);
    }

    public MemberUnavailShiftsResponse getMemberUnavailShiftRequest(MemberUnavailShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        Member member = existenceService.getMember(request.memberId());
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByMemberIdAndWardIdAndYearAndMonth(ward.getId(), member.getUserId(), request.year(), request.month());
        return getMemberUnavailShiftResponse(ward, member, unavailShiftRequests);
    }

    public MemberUnavailShiftsResponse addMemberUnavailShift(CreateShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        Member member = existenceService.getMember(request.memberId());
        Optional<UnavailShiftRequest> optionalShiftRequest = unavailShiftRepository.findByMemberIdAndWardIdAndShiftIdAndRequestDay(ward.getId(), member.getUserId(), request.shiftId(), request.requestDay());
        if (optionalShiftRequest.isPresent()) {
            throw new IllegalArgumentException(ALREADY_EXIST_SHIFT_REQUEST);
        }
        unavailShiftRepository.save(UnavailShiftRequest.create(member.getUserId(), request.requestDay(), request.shiftId(), request.reason()));
        int year = request.requestDay().getYear();
        int month = request.requestDay().getMonthValue();
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByMemberIdAndWardIdAndYearAndMonth(ward.getId(), member.getUserId(), year, month);
        return getMemberUnavailShiftResponse(ward, member, unavailShiftRequests);
    }

    public MemberUnavailShiftsResponse deleteMemberUnavailShift(DeleteShiftRequest request) {
        Ward ward = existenceService.getWard(request.wardId());
        Member member = existenceService.getMember(request.memberId());
        Optional<UnavailShiftRequest> optionalShiftRequest = unavailShiftRepository.findByMemberIdAndWardIdAndShiftIdAndRequestDay(ward.getId(), member.getUserId(), request.shiftId(), request.requestDay());
        if (optionalShiftRequest.isPresent()) {
            unavailShiftRepository.delete(optionalShiftRequest.get().getId());
        }
        int year = request.requestDay().getYear();
        int month = request.requestDay().getMonthValue();
        List<UnavailShiftRequest> unavailShiftRequests = unavailShiftRepository.findByMemberIdAndWardIdAndYearAndMonth(ward.getId(), member.getUserId(), year, month);
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
        Map<UUID, List<UnavailableShiftDto>> memberRequests = new HashMap<>();
        Map<UUID, MemberUnavailShiftsResponse> allMembersRequests = new HashMap<>();

        for (UnavailShiftRequest request : requests) {
            Member member = existenceService.getMember(request.getId());
            memberRequests.computeIfAbsent(member.getUserId(), k -> new ArrayList<>())
                    .add(new UnavailableShiftDto(request.getShiftId(), ward.getShiftName(request.getShiftId())));
        }

        for (UUID memberId : memberRequests.keySet()) {
            Member member = existenceService.getMember(memberId);
            List<UnavailableShiftDto> memberRequestsDto = memberRequests.containsKey(memberId) ? memberRequests.get(memberId) : new ArrayList<>();
            allMembersRequests.putIfAbsent(memberId, new MemberUnavailShiftsResponse(ward.getId(), memberId, member.getName(), memberRequestsDto));
        }
        return new WardUnavailShiftResponse(allMembersRequests);
    }
}
