package com.skillup.infrastructure.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skillup.domain.user.UserDomain;
import com.skillup.domain.user.UserRepository;
import com.skillup.infrastructure.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Repository
public class batisUserRepo implements UserRepository {
    @Autowired
    UserMapper userMapper;

    @Override
    @Transactional
    public void createUser(UserDomain userDomain) {
        userMapper.insert(toUser(userDomain));
    }

    @Override
    @Transactional
    public UserDomain getUserById(String id) {
        User user = userMapper.selectById(id);
        if (Objects.isNull(user)) {
            return null;
        }
        return toDomain(user);
    }

    @Override
    @Transactional
    public UserDomain getUserByName(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", name);
        User user = userMapper.selectOne(queryWrapper);
        if (Objects.isNull(user)) {
            return null;
        }
        return toDomain(user);
    }

    @Override
    @Transactional
    public void updateUser(UserDomain userDomain) {
        User user = toUser(userDomain);
        userMapper.updateById(user);
    }

    private User toUser(UserDomain userDomain) {
        return User.builder()
                .userId(userDomain.getUserId())
                .userName(userDomain.getUserName())
                .password(userDomain.getPassword())
                .build();
    }

    private UserDomain toDomain(User user) {
        return UserDomain.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .build();
    }
}
