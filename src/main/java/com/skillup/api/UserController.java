package com.skillup.api;

import com.skillup.api.dto.in.UserInDto;
import com.skillup.api.dto.out.UserOutDto;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.api.util.SkillUpResponse;
import com.skillup.domain.user.UserDomain;
import com.skillup.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/account")
    public ResponseEntity<SkillUpResponse> createUser(@RequestBody UserInDto userInDto) {
        try {
            UserDomain userDomain = userService.createUser(toDomain(userInDto));
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(userDomain)).build());
        } catch (Exception e) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(SkillUpCommon.USER_EXISTS).build());
        }
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
