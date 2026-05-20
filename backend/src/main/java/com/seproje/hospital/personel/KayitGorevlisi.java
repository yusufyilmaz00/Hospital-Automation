package com.seproje.hospital.personel;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("KAYIT_GOREVLISI")
public class KayitGorevlisi extends Personel {

    protected KayitGorevlisi() {
        super();
    }

    public KayitGorevlisi(IletisimBilgisi contactInformation, String personelID, String username,
            String password) {
        super(contactInformation, personelID, username, password);
    }

    public void kayitIslemiYap() {
        // Kayıt işlemi yapmak için gerekli kodlar buraya gelecek
    }

}
