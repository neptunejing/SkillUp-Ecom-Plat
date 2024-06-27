package com.skillup.domain.user;

public interface UserRepository {
    void createUser(UserDomain userDomain);

    UserDomain getUserById(String id);

    UserDomain getUserByName(String name);

    void updateUser(UserDomain userDomain);
}
