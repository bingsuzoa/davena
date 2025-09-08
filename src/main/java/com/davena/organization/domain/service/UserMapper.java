package com.davena.organization.domain.service;

import com.davena.organization.application.dto.user.UserDto;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;

    public List<UserDto> toUserDtos(List<UUID> userIds, Map<UUID, User> userMap) {
        return userIds.stream()
                .map(userId -> {
                    User user = userMap.get(userId);
                    return UserDto.from(user);
                })
                .toList();
    }

    public Map<UUID, User> getUserMap(Map<?, List<UUID>> groupUsers) {
        List<UUID> allUserIds = collectAllUserIds(groupUsers);
        List<User> users = userRepository.findAllById(allUserIds);
        return users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
    }

    private List<UUID> collectAllUserIds(Map<?, List<UUID>> groupUsers) {
        return groupUsers.values().stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
    }
}
