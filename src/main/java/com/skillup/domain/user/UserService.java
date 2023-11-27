package com.skillup.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public UserDomain createUser(UserDomain userDomain) {
        userRepository.createUser(userDomain);
        return userDomain;
    }
}
