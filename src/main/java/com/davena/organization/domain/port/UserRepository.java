package com.davena.organization.domain.port;

import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.user.UserId;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId userId);
}
