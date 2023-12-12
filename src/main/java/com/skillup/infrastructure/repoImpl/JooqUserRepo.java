package com.skillup.infrastructure.repoImpl;

import com.skillup.domain.user.UserDomain;
import com.skillup.domain.user.UserRepository;
import com.skillup.infrastructure.jooq.tables.User;
import com.skillup.infrastructure.jooq.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.impl.DSL.lag;
import static org.jooq.impl.DSL.selectFrom;

@Repository
public class JooqUserRepo implements UserRepository {
    @Autowired
    DSLContext dslContext;

    @Override
    public void createUser(UserDomain userDomain) {
        dslContext.executeInsert(toRecord(userDomain));
    }

    public static final User USER_T = new User();

    @Override
    public UserDomain getUserById(String id) {
        Optional<UserDomain> userDomainOptional = dslContext.selectFrom(USER_T).where(USER_T.USER_ID.eq(id)).fetchOptional(this::toDomain);
        return userDomainOptional.orElse(null);
    }

    @Override
    public UserDomain getUserByName(String name) {
        Optional<UserDomain> userDomainOptional = dslContext.selectFrom(USER_T).where(USER_T.USER_NAME.eq(name)).fetchOptional(this::toDomain);
        return userDomainOptional.orElse(null);
    }

    @Override
    public void updateUser(UserDomain userDomain) {
        dslContext.executeUpdate(toRecord(userDomain));
    }

    private UserRecord toRecord(UserDomain userDomain) {
        UserRecord userRecord = new UserRecord();
        userRecord.setUserId(userDomain.getUserId());
        userRecord.setUserName(userDomain.getUserName());
        userRecord.setPassword(userDomain.getPassword());
        return userRecord;
    }

    private UserDomain toDomain(UserRecord userRecord) {
        return UserDomain.builder()
                .userId(userRecord.getUserId())
                .userName(userRecord.getUserName())
                .password(userRecord.getPassword())
                .build();
    }
}
