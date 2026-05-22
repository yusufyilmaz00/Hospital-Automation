package com.seproje.hospital.security;

import com.seproje.hospital.auth.AuthUser;
import com.seproje.hospital.auth.AuthUserService;
import com.seproje.hospital.auth.UserType;
import com.seproje.hospital.session.Session;
import com.seproje.hospital.session.SessionConstants;
import com.seproje.hospital.session.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    private final SessionService sessionService;
    private final AuthUserService authUserService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        CookieUtil.getCookie(request, SessionConstants.COOKIE_NAME).
                flatMap(this::getSession)
                .ifPresent(this::setAuth);

        filterChain.doFilter(request, response);
    }

    private Optional<Session> getSession(String token) {
        return sessionService.getByToken(token);
    }

    private void setAuth(Session session) {
        SecurityContextUtil.set(session.getUserId(), session.getUserType());
    }
}