package com.seproje.hospital.auth;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.personel.kayit.KayitGorevlisi;
import com.seproje.hospital.personel.randevu.RandevuGorevlisi;
import com.seproje.hospital.personel.vezne.Veznedar;

public enum UserType {
    HASTA(Hasta.class),
    DOKTOR(Doktor.class),
    KAYIT_GOREVLISI(KayitGorevlisi.class),
    RANDEVU_GOREVLISI(RandevuGorevlisi.class),
    VEZNEDAR(Veznedar.class);

    private final Class<? extends AuthUser> clazz;

    private UserType(Class<? extends AuthUser> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends AuthUser> getEntityClass() {
        return clazz;
    }
}