package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.JoinRequest;
import com.davena.organization.application.dto.user.JoinResponse;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.model.ward.WardId;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final ExistenceService existenceCheck;
    private final WardRepository wardRepository;

    public static final String NOT_EXIST_WARD_BY_TOKEN = "입력하신 토큰을 가지는 병동이 존재하지 않습니다.";

    public JoinResponse applyForWard(JoinRequest request) {
        User user = existenceCheck.getUser(new UserId(request.userId()));
        Ward ward = existenceCheck.getWard(new WardId(request.wardId()));
        user.applyForWard(ward.getId());
        return JoinResponse.from(user.getId(), ward.getId(), ward.getName(), user.getStatus());
    }


    public WardResponse findWardByToken(String token) {
        Optional<Ward> optionalWard = wardRepository.findByToken(token);
        if(optionalWard.isEmpty()) {
            throw new IllegalArgumentException(NOT_EXIST_WARD_BY_TOKEN);
        }
        return WardResponse.from(optionalWard.get());
    }
}
