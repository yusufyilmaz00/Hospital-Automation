package com.seproje.hospital.personel.randevu;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.personel.doktor.DoctorService;
import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.personel.randevu.dto.AlternatifTarihDTO;
import com.seproje.hospital.personel.randevu.dto.RandevuGorevlisiCreateDTO;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.HastaService;
import com.seproje.hospital.randevu.Randevu;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RandevuGorevlisiServiceImpl implements RandevuGorevlisiService {

    private final RandevuGorevlisiRepository repository;
    private final HastaService hastaService;
    private final DoctorService doctorService;
    private final IletisimBilgisiMapper iletisimMapper;
    private final PasswordEncoder passwordEncoder;
    private final AlternatifTarihMapper alternatifTarihMapper;

    // ─── CRUD ─────────────────────────────

    @Override
    @Transactional
    public RandevuGorevlisi create(RandevuGorevlisiCreateDTO dto) {
        RandevuGorevlisi entity = new RandevuGorevlisi();
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setContactInformation(iletisimMapper.toEntity(dto.getContactInformation()));
        return repository.save(entity);
    }

    @Override
    public RandevuGorevlisi getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Randevu görevlisi bulunamadı: " + id));
    }

    @Override
    public List<RandevuGorevlisi> getAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ─── Randevu işlemleri ─────────────────────────────

    @Override
    @Transactional
    public Randevu randevuIslemiYap(Long hastaId, Long doktorId, LocalDateTime randevuZamani) {
        return randevuIslemiYap(hastaId, doktorId, randevuZamani, 30);
    }

    @Override
    @Transactional
    public Randevu randevuIslemiYap(Long hastaId, Long doktorId, LocalDateTime randevuZamani, Integer sureDakika) {

        Optional<Hasta> hastaOptional = hastaService.findById(hastaId);

        if (hastaOptional.isEmpty()) {
            throw new EntityNotFoundException("Hasta not found: " + hastaId);
        }

        Hasta hasta = hastaOptional.get();

        doctorService.getDoctorById(doktorId);

        if (!doctorService.checkAvailability(doktorId, randevuZamani)) {
            throw new IllegalArgumentException("Doctor not available at: " + randevuZamani);
        }

        return doctorService.addReservation(doktorId, randevuZamani, hasta, sureDakika);
    }

    @Override
    @Transactional
    public void randevuIptalEt(Long doktorId, Long randevuId) {
        doctorService.removeReservation(doktorId, randevuId);
    }

    @Override
    public boolean checkDoctorAvailability(Long doktorId, LocalDateTime desiredTime) {
        return doctorService.checkAvailability(doktorId, desiredTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlternatifTarihDTO> alternatifTarihleriListele(LocalDate haftaBaslangic) {
        if (haftaBaslangic == null) {
            throw new IllegalArgumentException("Hafta başlangıcı zorunludur.");
        }

        LocalDateTime start = haftaBaslangic.atStartOfDay();
        LocalDateTime end = haftaBaslangic.plusWeeks(1).atStartOfDay();
        Duration interval = Duration.ofMinutes(30);

        return doctorService.getAllDoctors().stream()
                .map(doktor -> alternatifTarihMapper.toDTO(
                        doktor,
                        doctorService.isAvailable(doktor.getId(), start, end, interval)
                ))
                .filter(dto -> !dto.getTarihler().isEmpty())
                .toList();
    }
}
