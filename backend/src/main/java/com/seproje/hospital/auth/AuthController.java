package com.seproje.hospital.auth;

import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.session.Session;
import com.seproje.hospital.session.SessionConstants;
import com.seproje.hospital.session.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest request, HttpServletResponse response) {
        Session session = authService.login(request);

        Cookie cookie = new Cookie(SessionConstants.COOKIE_NAME, session.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);

        response.addCookie(cookie);

        return ResponseEntity.ok("ok");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(c -> SessionConstants.COOKIE_NAME.equals(c.getName()))
                    .findFirst()
                    .flatMap(c -> sessionService.getByToken(c.getValue()))
                    .ifPresent(sessionService::invalidate);
        }

        Cookie expired = new Cookie(SessionConstants.COOKIE_NAME, "");
        expired.setHttpOnly(true);
        expired.setPath("/");
        expired.setMaxAge(0);
        response.addCookie(expired);

        return ResponseEntity.ok().build();
    }
}