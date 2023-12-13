package com.skillup.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generate getters and setters
@NoArgsConstructor // Generates constructors
@AllArgsConstructor
@Builder
public class UserDomain {
    private String userName;
    private String userId;
    private String password;
}
