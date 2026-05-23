package com.seproje.hospital.personel.randevu;

import com.seproje.hospital.personel.randevu.dto.RandevuGorevlisiCreateDTO;
import com.seproje.hospital.personel.randevu.dto.AlternatifTarihDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RandevuGorevlisiService {

    // ─── CRUD ─────────────────────────────

    RandevuGorevlisi create(RandevuGorevlisiCreateDTO dto);

    RandevuGorevlisi getById(Long id);

    List<RandevuGorevlisi> getAll();

    void delete(Long id);

    // ─── Randevu işlemleri ─────────────────

    void randevuIslemiYap(Long hastaId, Long doktorId, LocalDateTime randevuZamani);

    void randevuIslemiYap(Long hastaId, Long doktorId, LocalDateTime randevuZamani, Integer sureDakika);

    void randevuIptalEt(Long doktorId, Long randevuId);

    boolean checkDoctorAvailability(Long doktorId, LocalDateTime desiredTime);

    List<AlternatifTarihDTO> alternatifTarihleriListele(LocalDate haftaBaslangic);
}
