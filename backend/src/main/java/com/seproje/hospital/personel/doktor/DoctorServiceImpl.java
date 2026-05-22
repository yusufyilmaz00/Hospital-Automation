package com.seproje.hospital.personel.doktor;

import java.time.LocalDateTime;
import java.util.List;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.personel.doktor.dto.DoktorCreateDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.randevu.Randevu;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doktorRepository;
    private final IletisimBilgisiMapper iletisimMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Doktor createDoktor(DoktorCreateDTO dto) {
        Doktor doktor = new Doktor();
        doktor.setEmail(dto.getEmail());
        doktor.setPassword(passwordEncoder.encode(dto.getPassword()));
        doktor.setDepartment(dto.getBolum());
        doktor.setUnvan(dto.getUnvan());
        doktor.setContactInformation(iletisimMapper.toEntity(dto.getIletisimBilgisi()));
        return doktorRepository.save(doktor);
    }

    @Override
    public List<Doktor> getAllDoctors() {
        return doktorRepository.findAll();
    }

    @Override
    public Doktor getDoctorById(Long id) {
        return doktorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @Override
    public Doktor saveDoctor(Doktor doktor) {
        return doktorRepository.save(doktor);
    }

    @Override
    public void deleteDoctor(Long id) {
        doktorRepository.deleteById(id);
    }

    @Override
    public List<Randevu> getActiveReservations(Long doctorId) {
        return getDoctorById(doctorId).getActiveReservations();
    }

    // ─────────────────────────────────────────────
    // BUSINESS LOGIC MOVED HERE
    // ─────────────────────────────────────────────

    @Override
    public void addReservation(Long doctorId, LocalDateTime randevuZamani, Hasta hasta) {

        Doktor doktor = getDoctorById(doctorId);

        if (!checkAvailability(doctorId, randevuZamani)) {
            throw new IllegalArgumentException("The doctor is not available at the desired time.");
        }

        Randevu newRandevu = new Randevu(
                randevuZamani,
                hasta,
                doktor,
                calculateSalary(doctorId)
        );

        doktor.getActiveReservations().add(newRandevu);

        doktorRepository.save(doktor);
    }

    @Override
    public void removeReservation(Long doctorId, Long randevuId) {

        Doktor doktor = getDoctorById(doctorId);

        doktor.getActiveReservations()
                .removeIf(r -> r.getId().equals(randevuId));

        doktorRepository.save(doktor);
    }

    @Override
    public boolean checkAvailability(Long doctorId, LocalDateTime desiredTime) {

        Doktor doktor = getDoctorById(doctorId);

        List<Randevu> reservations = doktor.getActiveReservations();

        if (reservations == null) {
            return true;
        }

        for (Randevu r : reservations) {
            if (r.getRandevuZamani().equals(desiredTime)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Double calculateSalary(Long doctorId) {
        Doktor doktor = getDoctorById(doctorId);
        if (doktor.getUnvan() == null) return 0.0;
        double baseSalary = doktor.getUnvan().getHourlyRate() * 160;
        double bonus = doktor.getActiveReservations().size() * 20;
        return baseSalary + bonus;
    }
}