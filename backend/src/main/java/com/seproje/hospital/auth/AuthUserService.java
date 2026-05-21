package com.seproje.hospital.auth;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.HastaRepository;
import com.seproje.hospital.personel.doktor.DoctorRepository;
import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.personel.kayit.KayitGorevlisi;
import com.seproje.hospital.personel.kayit.KayitGorevlisiRepository;
import com.seproje.hospital.personel.randevu.RandevuGorevlisi;
import com.seproje.hospital.personel.randevu.RandevuGorevlisiRepository;
import com.seproje.hospital.personel.vezne.Veznedar;
import com.seproje.hospital.personel.vezne.VeznedarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final HastaRepository hastaRepository;
    private final DoctorRepository doctorRepository;
    private final KayitGorevlisiRepository kayitGorevlisiRepository;
    private final RandevuGorevlisiRepository randevuGorevlisiRepository;
    private final VeznedarRepository veznedarRepository;

    public AuthUserRepository<? extends AuthUser> getRepository(UserType userType) {
        return switch (userType) {
            case HASTA -> hastaRepository;
            case DOKTOR -> doctorRepository;
            case KAYIT_GOREVLISI -> kayitGorevlisiRepository;
            case RANDEVU_GOREVLISI -> randevuGorevlisiRepository;
            case VEZNEDAR -> veznedarRepository;
        };
    }

    public Class<? extends AuthUser> getClass(UserType userType) {
        return switch (userType) {
            case HASTA -> Hasta.class;
            case DOKTOR -> Doktor.class;
            case KAYIT_GOREVLISI -> KayitGorevlisi.class;
            case RANDEVU_GOREVLISI -> RandevuGorevlisi.class;
            case VEZNEDAR -> Veznedar.class;
        };
    }
}