package com.seproje.hospital.personel.doktor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.hasta.HastaService;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastaTedaviDTO;
import com.seproje.hospital.hasta.mapper.HastaMapper;
import com.seproje.hospital.hasta.mapper.HastaTedaviMapper;
import com.seproje.hospital.personel.doktor.dto.DoktorCreateDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.randevu.Rapor;
import com.seproje.hospital.randevu.RandevuRepository;
import com.seproje.hospital.randevu.RaporRepository;
import com.seproje.hospital.randevu.Reçete;
import com.seproje.hospital.randevu.Tedavi;
import com.seproje.hospital.randevu.dto.RaporRequestDTO;
import com.seproje.hospital.randevu.dto.RaporResponseDTO;
import com.seproje.hospital.randevu.dto.ReceteDTO;
import com.seproje.hospital.randevu.dto.ReceteRequestDTO;
import com.seproje.hospital.randevu.dto.TedaviRequestDTO;
import com.seproje.hospital.randevu.mapper.RaporMapper;
import com.seproje.hospital.randevu.mapper.ReceteMapper;
import com.seproje.hospital.randevu.ReceteRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;
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
    private final RaporRepository raporRepository;
    private final RaporMapper raporMapper;
    private final ReceteMapper receteMapper;
    private final ReceteRepository receteRepository;
    private final HastaService hastaService;

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
    @Transactional(readOnly = true)
    public List<Randevu> getActiveReservations(Long doctorId) {
        return new ArrayList<>(getDoctorById(doctorId).getActiveReservations());
    }

    // ─────────────────────────────────────────────
    // BUSINESS LOGIC MOVED HERE
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public void addReservation(Long doctorId, LocalDateTime randevuZamani, Hasta hasta) {
        addReservation(doctorId, randevuZamani, hasta, 30);
    }

    @Override
    @Transactional
    public void addReservation(Long doctorId, LocalDateTime randevuZamani, Hasta hasta, Integer sureDakika) {

        Doktor doktor = getDoctorById(doctorId);

        if (!checkAvailability(doctorId, randevuZamani)) {
            throw new IllegalArgumentException("The doctor is not available at the desired time.");
        }

        Randevu newRandevu = Randevu.builder()
                .randevuZamani(randevuZamani)
                .doktor(doktor)
                .hasta(hasta)
                .sureDakika(sureDakika)
                .build();

        doktor.getActiveReservations().add(newRandevu);

        doktorRepository.save(doktor);
    }

    @Override
    @Transactional
    public void removeReservation(Long doctorId, Long randevuId) {

        Doktor doktor = getDoctorById(doctorId);

        doktor.getActiveReservations()
                .removeIf(r -> r.getId().equals(randevuId));

        doktorRepository.save(doktor);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<LocalDateTime> isAvailable(Long doctorId, LocalDateTime start, LocalDateTime end, Duration interval) {
        if (start == null || end == null || interval == null || interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException("Start, end and interval must be valid.");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start must be before end.");
        }

        List<LocalDateTime> availableTimes = new ArrayList<>();
        LocalDateTime current = start;
        while (current.isBefore(end)) {
            if (checkAvailability(doctorId, current)) {
                availableTimes.add(current);
            }
            current = current.plus(interval);
        }
        return availableTimes;
    }

    @Override
    @Transactional(readOnly = true)
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

        Tedavi tedavi = Tedavi.builder()
                .tedaviTipi(dto.getTedaviTipi())
                .açıklama(dto.getAciklama())
                .randevu(randevu)
                .build();
        randevu.getTedaviler().add(tedavi);
        randevuRepository.save(randevu);

        return hastaTedaviMapper.toDTO(tedavi);
    }

    @Override
    @Transactional
    public DoktorRandevuDTO updateRandevuSuresi(Long doktorId, Long randevuId, Integer sureDakika) {
        Randevu randevu = randevuRepository.findByIdAndDoktorId(randevuId, doktorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Randevu bulunamadı veya bu randevu size ait değil: " + randevuId));

        if (Boolean.TRUE.equals(randevu.getOdendi())) {
            throw new IllegalArgumentException("Ödemesi alınmış randevunun süresi güncellenemez.");
        }

        randevu.setSureDakika(sureDakika);
        return doktorRandevuMapper.toDTO(randevuRepository.save(randevu));
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

    @Override
    @Transactional
    public ReceteDTO addRecete(Long doktorId, Long randevuId, Long tedaviId, ReceteRequestDTO dto) {
        Randevu randevu = randevuRepository.findByIdAndDoktorId(randevuId, doktorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Randevu bulunamadı veya bu randevu size ait değil: " + randevuId));

        Tedavi tedavi = randevu.getTedaviler().stream()
                .filter(t -> t.getId().equals(tedaviId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Tedavi bulunamadı: " + tedaviId));

        String generatedBarkod;
        do {
            generatedBarkod = UUID.randomUUID().toString();
        } while (receteRepository.existsByBarkod(generatedBarkod));

        final String barkod = generatedBarkod;
        Reçete recete = new Reçete(barkod, tedavi);
        dto.getIlaclar().forEach(recete::ilaçEkle);
        tedavi.getReçeteler().add(recete);
        Randevu saved = randevuRepository.save(randevu);

        Reçete savedRecete = saved.getTedaviler().stream()
                .filter(t -> t.getId().equals(tedaviId))
                .flatMap(t -> t.getReçeteler().stream())
                .filter(r -> r.getBarkod().equals(barkod))
                .findFirst()
                .orElse(recete);

        return receteMapper.toDTO(savedRecete);
    }

    @Override
    @Transactional
    public void removeRecete(Long doktorId, Long randevuId, Long tedaviId, Long receteId) {
        Randevu randevu = randevuRepository.findByIdAndDoktorId(randevuId, doktorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Randevu bulunamadı veya bu randevu size ait değil: " + randevuId));

        Tedavi tedavi = randevu.getTedaviler().stream()
                .filter(t -> t.getId().equals(tedaviId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Tedavi bulunamadı: " + tedaviId));

        boolean removed = tedavi.getReçeteler().removeIf(r -> r.getId().equals(receteId));
        if (!removed) {
            throw new EntityNotFoundException("Reçete bulunamadı: " + receteId);
        }
        randevuRepository.save(randevu);
    }

    @Override
    @Transactional
    public RaporResponseDTO addRapor(Long doktorId, Long randevuId, RaporRequestDTO dto) {
        Randevu randevu = randevuRepository.findByIdAndDoktorId(randevuId, doktorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Randevu bulunamadı veya bu randevu size ait değil: " + randevuId));

        Rapor rapor = new Rapor(dto.getIcerik(), randevu);
        return raporMapper.toDTO(raporRepository.save(rapor));
    }

    @Override
    @Transactional
    public void removeRapor(Long doktorId, Long randevuId, Long raporId) {
        Rapor rapor = raporRepository.findByIdAndRandevuDoktorId(raporId, doktorId)
                .filter(r -> r.getRandevu().getId().equals(randevuId))
                .orElseThrow(() -> new EntityNotFoundException("Rapor bulunamadı: " + raporId));
        raporRepository.delete(rapor);
    }

    // ─── Hasta Bilgilerinin Yönetimi ─────────────────────────────────────────

    private void doktorHastaYetkisiKontrol(Long doktorId, Long hastaId) {
        if (!randevuRepository.existsByHastaIdAndDoktorId(hastaId, doktorId)) {
            throw new EntityNotFoundException(
                    "Bu hastaya erişim yetkiniz yok veya hasta bulunamadı: " + hastaId);
        }
    }

    @Override
    @Transactional
    public void updateHastaBoy(Long doktorId, Long hastaId, Double boy) {
        doktorHastaYetkisiKontrol(doktorId, hastaId);
        hastaService.updateBoy(hastaId, boy);
    }

    @Override
    @Transactional
    public void updateHastaKilo(Long doktorId, Long hastaId, Double kilo) {
        doktorHastaYetkisiKontrol(doktorId, hastaId);
        hastaService.updateKilo(hastaId, kilo);
    }

    @Override
    @Transactional
    public void addHastalik(Long doktorId, Long hastaId, String detay) {
        doktorHastaYetkisiKontrol(doktorId, hastaId);
        hastaService.createHastalik(hastaId, detay);
    }

    @Override
    @Transactional
    public void updateHastalik(Long doktorId, Long hastaId, Long hastalikId, String detay) {
        doktorHastaYetkisiKontrol(doktorId, hastaId);
        hastaService.updateHastalik(hastaId, hastalikId, detay);
    }

    @Override
    @Transactional
    public void deleteHastalik(Long doktorId, Long hastaId, Long hastalikId) {
        doktorHastaYetkisiKontrol(doktorId, hastaId);
        hastaService.deleteHastalik(hastaId, hastalikId);
    }
}
