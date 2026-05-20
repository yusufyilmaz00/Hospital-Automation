package com.seproje.hospital.security;

import com.seproje.hospital.auth.AuthUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

public class SecurityContextUtil {

    public static void set(AuthUser authUser) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authUser, null, List.of())
        );
    }

    public static Optional<AuthUser> getRaw() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return Optional.ofNullable((AuthUser) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    public static <T extends AuthUser> Optional<T> currentUser(Class<T> type) {
        return getRaw()
                .filter(type::isInstance)
                .map(type::cast);
    }}