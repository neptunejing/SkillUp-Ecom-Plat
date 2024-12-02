package com.skillup.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    @Qualifier("batisUserRepo")
    UserRepository userRepository;

    public UserDomain createUser(UserDomain userDomain) {
        userRepository.createUser(userDomain);
        return userDomain;
    }

    public UserDomain getUserById(String id) {
        // Return UserDomain or null
        return userRepository.getUserById(id);
    }

    public UserDomain getUserByName(String name) {
        // Return UserDomain or null
        return userRepository.getUserByName(name);
    }

    public UserDomain updateUser(UserDomain userDomain) {
        userRepository.updateUser(userDomain);
        return userDomain;
    }

}
