package com.seproje.hospital.personel;

import java.time.LocalDateTime;

import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.randevu.Randevu;

import jakarta.persistence.*;

@Entity
@Table(name = "randevu_gorevlisi")
public class RandevuGorevlisi extends Personel {

    protected RandevuGorevlisi() {}

    public RandevuGorevlisi(IletisimBilgisi contactInformation, String personelID, String username, String password) {
        super(contactInformation, personelID, username, password);
    }

    public void randevuIslemiYap(Hasta hasta, Doktor doktor, LocalDateTime randevuZamani) {
        doktor.addReservation(randevuZamani, hasta);
    }

    public void randevuIptalEt(Doktor doktor, Randevu randevu) {
        doktor.removeReservation(randevu);
    }

    public boolean checkDoctorAvailability(Doktor doktor, LocalDateTime desiredTime) {
        return doktor.checkAvailability(desiredTime);
    }
}
