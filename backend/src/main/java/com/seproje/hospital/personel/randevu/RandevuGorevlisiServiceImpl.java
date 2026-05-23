package com.seproje.hospital.personel.randevu;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.personel.doktor.DoctorService;
import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.personel.randevu.dto.RandevuGorevlisiCreateDTO;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.HastaService;
import com.seproje.hospital.personel.randevu.RandevuGorevlisi;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void randevuIslemiYap(Long hastaId, Long doktorId, LocalDateTime randevuZamani) {

        Optional<Hasta> hastaOptional = hastaService.findById(hastaId);

        if (hastaOptional.isEmpty()) {
            throw new EntityNotFoundException("Hasta not found: " + hastaId);
        }

        Hasta hasta = hastaOptional.get();

        Doktor doktor = doctorService.getDoctorById(doktorId);

        if (!doctorService.checkAvailability(doktorId, randevuZamani)) {
            throw new IllegalArgumentException("Doctor not available at: " + randevuZamani);
        }

        doctorService.addReservation(doktorId, randevuZamani, hasta);
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
}