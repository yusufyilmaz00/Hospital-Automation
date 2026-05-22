package com.seproje.hospital.hasta;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.common.IletisimBilgisiRepository;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastalikDTO;
import com.seproje.hospital.hasta.mapper.HastaMapper;
import com.seproje.hospital.hasta.mapper.HastalikMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HastaServiceImpl implements HastaService {

    private final HastaRepository hastaRepository;
    private final HastalikRepository hastalikRepository;
    private final IletisimBilgisiRepository iletisimRepository;
    private final IletisimBilgisiMapper iletisimMapper;
    private final HastaMapper hastaMapper;
    private final PasswordEncoder passwordEncoder;
    private final HastalikMapper hastalikMapper;

    private Hasta getHasta(Long hastaId) {
        return hastaRepository.findById(hastaId)
                .orElseThrow(() -> new EntityNotFoundException("Hasta bulunamadı: " + hastaId));
    }

    @Override
    @Transactional
    public HastaResponseDTO create(HastaRequestDTO dto) {
        IletisimBilgisi iletisim = iletisimMapper.toEntity(dto.getIletisimBilgisi());
        iletisim = iletisimRepository.save(iletisim);

        Hasta hasta = Hasta.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .iletisimBilgisi(iletisim)
                .boy(dto.getBoy())
                .kilo(dto.getKilo())
                .hastaliklar(
                        dto.getHastaliklar() != null
                                ? dto.getHastaliklar().stream()
                                .map(Hastalik::new)
                                .collect(Collectors.toList())
                                : new java.util.ArrayList<>()
                )
                .build();

        hasta.getHastaliklar().forEach(h -> h.setHasta(hasta));

        return hastaMapper.toDTO(hastaRepository.save(hasta));
    }

    @Override
    @Transactional(readOnly = true)
    public HastaResponseDTO getById(Long hastaId) {
        return hastaMapper.toDTO(getHasta(hastaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HastaResponseDTO> getAll() {
        return hastaRepository.findAll().stream()
                .map(hastaMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ─── Güncelleme metodları ───────────────────────────────────────────────

    @Override
    @Transactional
    public void updateBoy(Long hastaId, Double boy) {
        Hasta hasta = getHasta(hastaId);
        hasta.setBoy(boy);
        hastaRepository.save(hasta);
    }

    @Override
    @Transactional
    public void updateKilo(Long hastaId, Double kilo) {
        Hasta hasta = getHasta(hastaId);
        hasta.setKilo(kilo);
        hastaRepository.save(hasta);
    }

    @Override
    @Transactional
    public void updateIletisim(Long hastaId, IletisimBilgisi iletisim) {
        Hasta hasta = getHasta(hastaId);

        IletisimBilgisi mevcut = hasta.getIletisimBilgisi();
        mevcut.setIsim(iletisim.getIsim());
        mevcut.setSoyisim(iletisim.getSoyisim());
        mevcut.setTelefon(iletisim.getTelefon());
        mevcut.setDoğumTarihi(iletisim.getDoğumTarihi());

        iletisimRepository.save(mevcut);
    }

    @Override
    @Transactional
    public void updatePassword(Long hastaId, String password) {
        Hasta hasta = getHasta(hastaId);
        hasta.setPassword(passwordEncoder.encode(password));
        hastaRepository.save(hasta);
    }

    @Override
    @Transactional
    public void updateEmail(Long hastaId, String email) {
        Hasta hasta = getHasta(hastaId);
        hasta.setEmail(email);
        hastaRepository.save(hasta);
    }

    @Override
    public Optional<Hasta> findById(Long hastaId) {
        return hastaRepository.findById(hastaId);
    }

    // ─── Hastalık CRUD metodları ────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<HastalikDTO> getHastaliklar(Long hastaId) {
        return getHasta(hastaId)
                .getHastaliklar()
                .stream()
                .map(hastalikMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createHastalik(Long hastaId, String hastalik) {
        Hasta hasta = getHasta(hastaId);

        Hastalik yeni = new Hastalik(hastalik);
        yeni.setHasta(hasta);
        hasta.getHastaliklar().add(yeni);

        hastalikRepository.save(yeni);
    }

    @Override
    @Transactional
    public void updateHastalik(Long hastaId, Long id, String hastalik) {
        Hastalik mevcut = hastalikRepository.findById(id)
                .filter(h -> h.getHasta().getId().equals(hastaId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Hastalık bulunamadı: " + id));

        mevcut.setDetay(hastalik);
        hastalikRepository.save(mevcut);
    }

    @Override
    @Transactional
    public void deleteHastalik(Long hastaId, Long id) {
        Hastalik silinecek = hastalikRepository.findById(id)
                .filter(h -> h.getHasta().getId().equals(hastaId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Hastalık bulunamadı: " + id));

        Hasta hasta = getHasta(hastaId);
        hasta.getHastaliklar().remove(silinecek);

        hastalikRepository.delete(silinecek);
    }
}