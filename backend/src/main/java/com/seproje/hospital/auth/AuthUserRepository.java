package com.seproje.hospital.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface AuthUserRepository<T extends AuthUser> extends JpaRepository<T, Long> {
    Optional<T> findByEmail(String email);
}