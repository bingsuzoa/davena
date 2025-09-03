package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserRequest;
import com.davena.organization.application.dto.user.UserResponse;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(UserRequest request) {
        User user = User.create(request.name(), request.loginId(), request.password(), request.phoneNumber());
        return UserResponse.from(userRepository.save(user));
    }
}
