package com.seproje.hospital.personel.doktor;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastaTedaviDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorCreateDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.randevu.dto.RaporRequestDTO;
import com.seproje.hospital.randevu.dto.RaporResponseDTO;
import com.seproje.hospital.randevu.dto.ReceteRequestDTO;
import com.seproje.hospital.randevu.dto.ReceteDTO;
import com.seproje.hospital.randevu.dto.TedaviRequestDTO;

public interface DoctorService {

    Doktor createDoktor(DoktorCreateDTO dto);

    List<Doktor> getAllDoctors();

    Doktor getDoctorById(Long id);

    Doktor saveDoctor(Doktor doktor);

    void deleteDoctor(Long id);

    List<Randevu> getActiveReservations(Long doctorId);

    Randevu addReservation(Long doctorId, LocalDateTime randevuZamani, Hasta hasta);

    Randevu addReservation(Long doctorId, LocalDateTime randevuZamani, Hasta hasta, Integer sureDakika);

    void removeReservation(Long doctorId, Long randevuId);

    boolean checkAvailability(Long doctorId, LocalDateTime desiredTime);

    List<LocalDateTime> isAvailable(Long doctorId, LocalDateTime start, LocalDateTime end, Duration interval);

    Double calculateSalary(Long doctorId);

    List<DoktorRandevuDTO> getMyRandevular(Long doktorId);

    HastaResponseDTO getHastaByRandevuId(Long doktorId, Long randevuId);

    DoktorRandevuDTO updateRandevuSuresi(Long doktorId, Long randevuId, Integer sureDakika);

    HastaTedaviDTO addTedavi(Long doktorId, Long randevuId, TedaviRequestDTO dto);

    void removeTedavi(Long doktorId, Long randevuId, Long tedaviId);

    ReceteDTO addRecete(Long doktorId, Long randevuId, Long tedaviId, ReceteRequestDTO dto);

    void removeRecete(Long doktorId, Long randevuId, Long tedaviId, Long receteId);

    RaporResponseDTO addRapor(Long doktorId, Long randevuId, RaporRequestDTO dto);

    void removeRapor(Long doktorId, Long randevuId, Long raporId);

    void updateHastaBoy(Long doktorId, Long hastaId, Double boy);

    void updateHastaKilo(Long doktorId, Long hastaId, Double kilo);

    void addHastalik(Long doktorId, Long hastaId, String detay);

    void updateHastalik(Long doktorId, Long hastaId, Long hastalikId, String detay);

    void deleteHastalik(Long doktorId, Long hastaId, Long hastalikId);
}
