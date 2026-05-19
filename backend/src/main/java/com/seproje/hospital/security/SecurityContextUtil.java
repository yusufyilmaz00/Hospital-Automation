package com.seproje.hospital.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {

    public static void set(AuthUserPrincipal principal) {

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        null
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}