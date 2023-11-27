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
        UserDomain userDomain = new UserDomain();
        userDomain.setUserName(userInDto.getUserName());
        userDomain.setPassword(userInDto.getPassword());
        userDomain.setUserId(UUID.randomUUID().toString());
        return userDomain;
    }

    private UserOutDto toOutDto(UserDomain userDomain) {
        UserOutDto userOutDto = new UserOutDto();
        userOutDto.setUserId(userDomain.getUserId());
        userOutDto.setUserName(userDomain.getUserName());
        return userOutDto;
    }
}
