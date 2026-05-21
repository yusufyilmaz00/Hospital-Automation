package com.seproje.hospital.personel.randevu;
import java.time.LocalDateTime;

import com.seproje.hospital.auth.UserType;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.personel.personel.Personel;
import com.seproje.hospital.randevu.Randevu;

import jakarta.persistence.*;

@Entity
@Table(name = "randevu_gorevlisi")
public class RandevuGorevlisi extends Personel {

    @Override
    public UserType getUserType() {
        return UserType.RANDEVU_GOREVLISI;
    }
}
