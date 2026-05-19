package com.seproje.hospital.auth;

import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.session.Session;
import com.seproje.hospital.session.SessionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final String dummyHash;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SessionService sessionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;

        this.dummyHash = passwordEncoder.encode("dummy");
    }

    public Session login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            // handle time based attacks
            passwordEncoder.matches(request.getPassword(), this.dummyHash);
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return sessionService.createSession(user.getInternalId());
    }


    public User register(String email, Integer internalId, UserType userType, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email zaten kayıtlı");
        }

        User user = User.builder()
                .internalId(internalId)
                .type(userType)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        return userRepository.save(user);
    }

}