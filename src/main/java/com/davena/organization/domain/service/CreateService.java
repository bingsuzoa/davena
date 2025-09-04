package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserRequest;
import com.davena.organization.application.dto.user.UserResponse;
import com.davena.organization.application.dto.ward.WardRequest;
import com.davena.organization.application.dto.ward.WardResponse;
import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.port.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateService {

    private final UserRepository userRepository;
    private final WardRepository wardRepository;

    public UserResponse createUser(UserRequest request) {
        User user = User.create(request.name(), request.loginId(), request.password(), request.phoneNumber());
        return UserResponse.from(userRepository.save(user));
    }

    public WardResponse createWard(WardRequest request) {
        Ward ward = Ward.create(
                new HospitalId(request.hospitalId()),
                new UserId(request.supervisorId()),
                request.wardName(),
                createToken()
        );
        return WardResponse.from(wardRepository.save(ward));
    }

    private String createToken() {
        return UUID.randomUUID().toString();
    }



}
