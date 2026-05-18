package com.seproje.hospital.personel;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Entity
@Table(name = "veznedar")
public class Veznedar extends Personel {

    protected Veznedar() {}

    public Veznedar(IletisimBilgisi contactInformation, String personelID, String username, String password) {
        super(contactInformation, personelID, username, password);
    }

}
