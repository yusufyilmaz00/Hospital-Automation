package com.seproje.hospital.personel;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Entity
@Table(name = "kayit_gorevlisi")
public class KayitGorevlisi extends Personel {

    protected KayitGorevlisi() {}

    public KayitGorevlisi(IletisimBilgisi contactInformation, String personelID, String username, String password) {
        super(contactInformation, personelID, username, password);
    }

    public void kayitIslemiYap() {
        // Kayıt işlemi yapmak için gerekli kodlar buraya gelecek
    }
}
