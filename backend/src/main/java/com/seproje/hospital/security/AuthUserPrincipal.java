package com.seproje.hospital.security;

import com.seproje.hospital.auth.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserPrincipal {

    private final Integer userId;
    private final String email;
    private final String userType;

    public static AuthUserPrincipal from(User user) {
        return new AuthUserPrincipal(
                user.getInternalId(),
                user.getEmail(),
                user.getType().name()
        );
    }
}