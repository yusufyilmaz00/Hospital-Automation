package com.seproje.hospital.personel.kayit;

import com.seproje.hospital.auth.UserType;
import com.seproje.hospital.common.IletisimBilgisi;

import com.seproje.hospital.personel.personel.Personel;
import jakarta.persistence.*;

@Entity
@Table(name = "kayit_gorevlisi")
public class KayitGorevlisi extends Personel {

    @Override
    public UserType getUserType() {
        return UserType.KAYIT_GOREVLISI;
    }
}
