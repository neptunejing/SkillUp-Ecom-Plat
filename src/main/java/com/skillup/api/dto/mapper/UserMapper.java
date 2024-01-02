package com.skillup.api.dto.mapper;

import com.skillup.api.dto.in.UserInDto;
import com.skillup.api.dto.out.UserOutDto;
import com.skillup.domain.user.UserDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userId", expression = "java(java.util.UUID.randomUUID().toString())")
    UserDomain toDomain(UserInDto userInDto);
    UserOutDto toOutDto(UserDomain userDomain);
}
