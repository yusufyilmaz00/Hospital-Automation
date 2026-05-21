package com.seproje.hospital.auth;

import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.session.Session;
import com.seproje.hospital.session.SessionConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid AuthRequest request, HttpServletResponse response) {

        Session session = authService.login(request);

        Cookie cookie = new Cookie(SessionConstants.COOKIE_NAME, session.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7); // 7 gün

        response.addCookie(cookie);

        return ResponseEntity.status(200).body("ok");
    }
}