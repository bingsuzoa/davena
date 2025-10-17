package com.davena.organization.domain.port;

import com.davena.organization.domain.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID userId);

    List<User> findAllById(List<UUID> userIds);
}
