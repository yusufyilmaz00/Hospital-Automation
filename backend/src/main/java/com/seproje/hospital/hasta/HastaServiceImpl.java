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

        // Hastalik -> Hasta ilişkisini kur
        hasta.getHastaliklar().forEach(h -> h.setHasta(hasta));

        return hastaMapper.toDTO(hastaRepository.save(hasta));
    }

    // ─── Güncelleme metodları ───────────────────────────────────────────────

    @Override
    @Transactional
    public void updateBoy(Hasta hasta, Double boy) {
        hasta.setBoy(boy);
        hastaRepository.save(hasta);
    }

    @Override
    @Transactional
    public void updateKilo(Hasta hasta, Double kilo) {
        hasta.setKilo(kilo);
        hastaRepository.save(hasta);
    }

    @Override
    @Transactional
    public void updateIletisim(Hasta hasta, IletisimBilgisi iletisim) {
        IletisimBilgisi mevcut = hasta.getIletisimBilgisi();
        mevcut.setIsim(iletisim.getIsim());
        mevcut.setSoyisim(iletisim.getSoyisim());
        mevcut.setTelefon(iletisim.getTelefon());
        mevcut.setDoğumTarihi(iletisim.getDoğumTarihi());
        // IletisimBilgisi alanlarını ihtiyaca göre genişlet
        iletisimRepository.save(mevcut);
    }

    @Override
    @Transactional
    public void updatePassword(Hasta hasta, String password) {
        hasta.setPassword(passwordEncoder.encode(password));
        hastaRepository.save(hasta);
    }

    @Override
    @Transactional
    public void updateEmail(Hasta hasta, String email) {
        hasta.setEmail(email);
        hastaRepository.save(hasta);
    }

    // ─── Hastalık CRUD metodları ────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<HastalikDTO> getHastaliklar(Hasta hasta) {
        return hasta.getHastaliklar().stream().map(hastalikMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createHastalik(Hasta hasta, String hastalik) {
        Hastalik yeni = new Hastalik(hastalik);
        yeni.setHasta(hasta);
        hasta.getHastaliklar().add(yeni);
        hastalikRepository.save(yeni);
    }

    @Override
    @Transactional
    public void updateHastalik(Hasta hasta, Long id, String hastalik) {
        Hastalik mevcut = hastalikRepository.findById(id)
                .filter(h -> h.getHasta().getId().equals(hasta.getId()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Hastalık bulunamadı: " + id));

        mevcut.setDetay(hastalik);
        hastalikRepository.save(mevcut);
    }

    @Override
    @Transactional
    public void deleteHastalik(Hasta hasta, Long id) {
        Hastalik silinecek = hastalikRepository.findById(id)
                .filter(h -> h.getHasta().getId().equals(hasta.getId()))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Hastalık bulunamadı: " + id));

        hasta.getHastaliklar().remove(silinecek);
        hastalikRepository.delete(silinecek);
    }
}