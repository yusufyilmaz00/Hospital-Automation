package com.seproje.hospital.personel.doktor;

import java.time.LocalDateTime;
import java.util.List;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastaTedaviDTO;
import com.seproje.hospital.hasta.mapper.HastaMapper;
import com.seproje.hospital.hasta.mapper.HastaTedaviMapper;
import com.seproje.hospital.personel.doktor.dto.DoktorCreateDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.randevu.RandevuRepository;
import com.seproje.hospital.randevu.Tedavi;
import com.seproje.hospital.randevu.dto.TedaviRequestDTO;
import jakarta.persistence.EntityNotFoundException;
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
    private final RandevuRepository randevuRepository;
    private final DoktorRandevuMapper doktorRandevuMapper;
    private final HastaMapper hastaMapper;
    private final HastaTedaviMapper hastaTedaviMapper;

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

    @Override
    @Transactional(readOnly = true)
    public List<DoktorRandevuDTO> getMyRandevular(Long doktorId) {
        return getActiveReservations(doktorId).stream()
                .map(doktorRandevuMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HastaResponseDTO getHastaByRandevuId(Long doktorId, Long randevuId) {
        Randevu randevu = randevuRepository.findByIdAndDoktorId(randevuId, doktorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Randevu bulunamadı veya bu randevu size ait değil: " + randevuId));
        return hastaMapper.toDTO(randevu.getHasta());
    }

    @Override
    @Transactional
    public HastaTedaviDTO addTedavi(Long doktorId, Long randevuId, TedaviRequestDTO dto) {
        Randevu randevu = randevuRepository.findByIdAndDoktorId(randevuId, doktorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Randevu bulunamadı veya bu randevu size ait değil: " + randevuId));

        Tedavi tedavi = new Tedavi(dto.getTedaviTipi(), dto.getAciklama(), randevu);
        randevu.getTedaviler().add(tedavi);
        randevuRepository.save(randevu);

        return hastaTedaviMapper.toDTO(tedavi);
    }

    @Override
    @Transactional
    public void removeTedavi(Long doktorId, Long randevuId, Long tedaviId) {
        Randevu randevu = randevuRepository.findByIdAndDoktorId(randevuId, doktorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Randevu bulunamadı veya bu randevu size ait değil: " + randevuId));

        boolean removed = randevu.getTedaviler().removeIf(t -> t.getId().equals(tedaviId));
        if (!removed) {
            throw new EntityNotFoundException("Tedavi bulunamadı: " + tedaviId);
        }
        randevuRepository.save(randevu);
    }
}