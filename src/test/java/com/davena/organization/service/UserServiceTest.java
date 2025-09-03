package com.davena.organization.service;

import com.davena.organization.application.dto.user.UserRequest;
import com.davena.organization.application.dto.user.UserResponse;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("User 객체 생성 확인")
    void user_객체_생성_확인() {
        UserRequest request = new UserRequest("name", "loginId", "password", "phone");
        User user = User.create("name", "loginId", "password", "phone");
        when(userRepository.save(any())).thenReturn(user);
        UserResponse response = userService.createUser(request);
        Assertions.assertNotNull(response.userId());
    }
}
