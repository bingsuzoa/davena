package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserRequest;
import com.davena.organization.application.dto.user.UserResponse;
import com.davena.organization.application.dto.ward.WardRequest;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.port.WardRepository;
import com.davena.common.ExistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateService {

    private final UserRepository userRepository;
    private final WardRepository wardRepository;

    public UserResponse createUser(UserRequest request) {
        User user = userRepository.save(User.create(
                request.name(),
                request.loginId(),
                request.password(),
                request.phoneNumber()));
        return new UserResponse(user.getId(), user.getName());
    }

    public WardResponse createWard(WardRequest request) {
        Ward ward = wardRepository.save(Ward.create(
                request.hospitalId(),
                request.supervisorId(),
                request.wardName(),
                createToken()
        ));
        return new WardResponse(ward.getId(), ward.getName());
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }
}
