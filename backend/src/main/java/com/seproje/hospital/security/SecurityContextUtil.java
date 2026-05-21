package com.seproje.hospital.security;

import com.seproje.hospital.auth.AuthUser;
import com.seproje.hospital.auth.UserType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

public class SecurityContextUtil {

    private record AuthRecord(Long userId, UserType userType) {}

    public static void set(Long userId, UserType userType) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new AuthRecord(userId, userType),
                        null,
                        List.of()
                )
        );
    }

    private static Optional<AuthRecord> getRaw() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof AuthRecord auth) {
            return Optional.of(auth);
        }

        return Optional.empty();
    }

    public static <T extends AuthUser> Optional<Long> currentUser(Class<T> type) {
        return getRaw()
                .filter(record -> isAssignable(record, type))
                .map(AuthRecord::userId);
    }

    private static <T extends AuthUser> boolean isAssignable(AuthRecord record, Class<T> type) {
        return type.isAssignableFrom(record.userType().getEntityClass());
    }
}