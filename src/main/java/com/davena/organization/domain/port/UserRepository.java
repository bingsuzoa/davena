package com.davena.organization.domain.port;

import com.davena.organization.domain.model.user.User;

public interface UserRepository {

    User save(User user);
}
