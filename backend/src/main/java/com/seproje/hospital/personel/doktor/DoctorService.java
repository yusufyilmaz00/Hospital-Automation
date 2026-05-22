package com.seproje.hospital.personel.doktor;

import java.time.LocalDateTime;
import java.util.List;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.randevu.Randevu;

public interface DoctorService {

    List<Doktor> getAllDoctors();

    Doktor getDoctorById(Long id);

    Doktor saveDoctor(Doktor doktor);

    void deleteDoctor(Long id);

    List<Randevu> getActiveReservations(Long doctorId);

    void addReservation(Long doctorId, LocalDateTime randevuZamani, Hasta hasta);

    void removeReservation(Long doctorId, Long randevuId);

    boolean checkAvailability(Long doctorId, LocalDateTime desiredTime);

    Double calculateSalary(Long doctorId);
}