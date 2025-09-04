package com.davena.organization.domain.service;

import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.model.ward.WardId;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.davena.organization.domain.service.JoinService.NOT_SUPERVISOR;

@Service
@RequiredArgsConstructor
public class ExistenceService {

    private final UserRepository userRepository;
    private final WardRepository wardRepository;

    public static final String NOT_EXIST_USER = "존재하지 않는 회원입니다.";
    public static final String NOT_EXIST_WARD = "존재하지 않는 병동입니다.";

    public User getUser(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));
    }

    public Ward getWard(WardId wardId) {
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_WARD));
    }

    public boolean verifySupervisor(Ward ward, UserId supervisorId) {
        if(!ward.isSupervisor(supervisorId)) {
            throw new IllegalArgumentException(NOT_SUPERVISOR);
        }
        return true;
    }
}
