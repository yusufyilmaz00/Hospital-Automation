package com.seproje.hospital.auth;

import com.seproje.hospital.hasta.HastaRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {

    private final HastaRepository hastaRepository;

    public AuthUserService(
            HastaRepository hastaRepository
    ) {
        this.hastaRepository = hastaRepository;
    }

    public AuthUserRepository<? extends AuthUser> getRepository(UserType userType) {
        return switch (userType) {
            case HASTA -> hastaRepository;
            case DOKTOR -> null;
            case KAYIT_GOREVLISI -> null;
            case PERSONEL -> null;
            case RANDEVU_GOREVLISI -> null;
            case VEZNEDAR -> null;
        };
    }
}