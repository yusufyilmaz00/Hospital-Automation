package com.seproje.hospital.auth;

public interface AuthUser {
    Long getId();
    String getEmail();
    String getPassword();
    UserType getUserType();
}
