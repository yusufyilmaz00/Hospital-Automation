package com.seproje.hospital.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByToken(String token);
    List<Session> findAllByUserIdAndActiveTrue(Integer userId);
}