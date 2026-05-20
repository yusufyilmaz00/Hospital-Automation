package com.seproje.hospital.auth;

import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.session.Session;
import com.seproje.hospital.session.SessionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final String dummyHash;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final AuthUserService authUserService;

    public AuthService(PasswordEncoder passwordEncoder, SessionService sessionService, AuthUserService authUserService) {
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
        this.authUserService = authUserService;

        this.dummyHash = passwordEncoder.encode("dummy");
    }

    public Session login(AuthRequest request) {
        Optional<AuthUser> userOpt = this.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            // handle time based attacks
            passwordEncoder.matches(request.getPassword(), this.dummyHash);
            throw new RuntimeException("Invalid credentials");
        }

        AuthUser user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return sessionService.createSession(user);
    }

    private Optional<AuthUser> findByEmail(String email) {
        for (UserType type : UserType.values()) {
            AuthUserRepository<?> repository = authUserService.getRepository(type);
            Optional<? extends AuthUser> user = repository.findByEmail(email);
            if (user.isPresent()) {
                return Optional.of(user.get());
            }
        }
        return Optional.empty();
    }

}