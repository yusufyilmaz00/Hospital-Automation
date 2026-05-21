package com.seproje.hospital.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public class CookieUtil {

    public static Optional<String> getCookie(HttpServletRequest request, String name) {

        if (request.getCookies() == null) return Optional.empty();

        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}