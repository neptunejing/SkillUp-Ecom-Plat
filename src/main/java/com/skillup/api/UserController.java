package com.skillup.api;

import com.skillup.api.dto.in.UserInDto;
import com.skillup.api.dto.out.UserOutDto;
import com.skillup.domain.user.UserDomain;
import com.skillup.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/account")
    public UserOutDto createUser(@RequestBody UserInDto userInDto) {
        UserDomain userDomain = userService.createUser(toDomain(userInDto));
        return toOutDto(userDomain);
    }

    private UserDomain toDomain(UserInDto userInDto) {
        return UserDomain.builder()
                .userId(UUID.randomUUID().toString())
                .userName(userInDto.getUserName())
                .password(userInDto.getPassword())
                .build();
    }

    private UserOutDto toOutDto(UserDomain userDomain) {
        return UserOutDto.builder()
                .userId(userDomain.getUserId())
                .userName(userDomain.getUserName())
                .build();
    }
}
