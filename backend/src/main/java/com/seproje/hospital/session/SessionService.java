package com.seproje.hospital.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public Session createSession(Integer userId) {
        Session session = Session.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .active(true)
                .build();

        return sessionRepository.save(session);
    }

    public Optional<Session> getByToken(String token) {
        return sessionRepository.findByToken(token);
    }

    public Boolean isValid(Session session) {
        if (session == null) {
            return false;
        }
        if (!session.getActive()) {
            return false;
        }
        return session.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public void invalidate(Session session) {
        session.setActive(false);
        sessionRepository.save(session);
    }

    public void invalidateAllUserSessions(Integer userId) {
        List<Session> sessions = sessionRepository.findAllByUserIdAndActiveTrue(userId);
        for (Session session : sessions) {
            session.setActive(false);
        }
        sessionRepository.saveAll(sessions);

    }
}