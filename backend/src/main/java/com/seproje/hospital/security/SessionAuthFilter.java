package com.seproje.hospital.security;

import com.seproje.hospital.auth.UserRepository;
import com.seproje.hospital.session.Session;
import com.seproje.hospital.session.SessionConstants;
import com.seproje.hospital.session.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Optional<String> tokenOpt = CookieUtil.getCookie(request, SessionConstants.COOKIE_NAME);

        if (tokenOpt.isPresent()) {
            String token = tokenOpt.get();
            Optional<Session> sessionOpt = sessionService.getByToken(token);

            if (sessionOpt.isPresent() && sessionService.isValid(sessionOpt.get())) {

                userRepository.findById(sessionOpt.get().getUserId())
                        .ifPresent(user -> {
                            AuthUserPrincipal principal = AuthUserPrincipal.from(user);
                            SecurityContextUtil.set(principal);
                        });
            }
        }

        filterChain.doFilter(request, response);
    }
}