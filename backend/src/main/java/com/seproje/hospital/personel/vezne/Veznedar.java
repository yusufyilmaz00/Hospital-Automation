package com.seproje.hospital.personel.vezne;
import com.seproje.hospital.auth.UserType;
import com.seproje.hospital.common.IletisimBilgisi;

import com.seproje.hospital.personel.personel.Personel;
import jakarta.persistence.*;

@Entity
@Table(name = "veznedar")
public class Veznedar extends Personel {

    @Override
    public UserType getUserType() {
        return UserType.VEZNEDAR;
    }
}
