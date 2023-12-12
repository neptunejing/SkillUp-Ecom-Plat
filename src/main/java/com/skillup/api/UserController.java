package com.skillup.api;

import com.skillup.api.dto.in.UserInDto;
import com.skillup.api.dto.in.UserPin;
import com.skillup.api.dto.out.UserOutDto;
import com.skillup.api.util.SkillUpCommon;
import com.skillup.api.util.SkillUpResponse;
import com.skillup.domain.user.UserDomain;
import com.skillup.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/account")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping()
    public ResponseEntity<SkillUpResponse> createUser(@RequestBody UserInDto userInDto) {
        try {
            UserDomain userDomain = userService.createUser(toDomain(userInDto));
            return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(userDomain)).build());
        } catch (Exception e) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(SkillUpCommon.USER_EXISTS).build());
        }
    }

    @GetMapping(value = "/id/{id}")
    public ResponseEntity<SkillUpResponse> getUserById(@PathVariable("id") String id) {
        UserDomain userDomain = userService.getUserById(id);
        if (Objects.isNull(userDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(String.format(SkillUpCommon.USER_ID_WRONG, id)).build());
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(userDomain)).build());
    }

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<SkillUpResponse> getUserByName(@PathVariable("name") String name) {
        UserDomain userDomain = userService.getUserByName(name);
        if (Objects.isNull(userDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(String.format(SkillUpCommon.USER_NAME_WRONG, name)).build());
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(userDomain)).build());
    }

    @PostMapping(value = "/login")
    public ResponseEntity<SkillUpResponse> login(@RequestBody UserInDto userInDto) {
        UserDomain userDomain = userService.getUserByName(userInDto.getUserName());
        if (Objects.isNull(userDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(String.format(SkillUpCommon.USER_NAME_WRONG, userInDto.getUserName())).build());
        }
        if (!userDomain.getPassword().equals(userInDto.getPassword())) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(SkillUpCommon.PASSWORD_NOT_MATCH).build());
        }
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(userDomain)).build());
    }

    @PutMapping(value = "/password")
    public ResponseEntity<SkillUpResponse> login(@RequestBody UserPin userPin) {
        UserDomain userDomain = userService.getUserByName(userPin.getUserName());
        if (Objects.isNull(userDomain)) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(String.format(SkillUpCommon.USER_NAME_WRONG, userPin.getUserName())).build());
        }
        if (!userDomain.getPassword().equals(userPin.getOldPassword())) {
            return ResponseEntity.status(SkillUpCommon.BAD_REQUEST).body(SkillUpResponse.builder().msg(SkillUpCommon.PASSWORD_NOT_MATCH).build());
        }
        userDomain.setPassword(userPin.getNewPassword());
        userService.updateUser(userDomain);
        return ResponseEntity.status(SkillUpCommon.SUCCESS).body(SkillUpResponse.builder().result(toOutDto(userDomain)).build());
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
