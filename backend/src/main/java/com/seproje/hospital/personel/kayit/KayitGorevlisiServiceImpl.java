package com.seproje.hospital.personel.kayit;

import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.common.IletisimBilgisiRepository;
import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.personel.kayit.dto.KayitGorevlisiRequestDTO;
import com.seproje.hospital.personel.kayit.dto.KayitGorevlisiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KayitGorevlisiServiceImpl implements KayitGorevlisiService {

    private final KayitGorevlisiRepository repository;
    private final IletisimBilgisiRepository iletisimRepository;
    private final IletisimBilgisiMapper iletisimMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public KayitGorevlisiResponseDTO create(KayitGorevlisiRequestDTO dto) {
        KayitGorevlisi kg = new KayitGorevlisi();
        kg.setEmail(dto.getEmail());
        kg.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (dto.getIletisimBilgisi() != null) {
            IletisimBilgisi iletisim = iletisimRepository.save(
                    iletisimMapper.toEntity(dto.getIletisimBilgisi())
            );
            kg.setContactInformation(iletisim);
        }

        return toDTO(repository.save(kg));
    }

    @Override
    @Transactional(readOnly = true)
    public List<KayitGorevlisiResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    private KayitGorevlisiResponseDTO toDTO(KayitGorevlisi kg) {
        return KayitGorevlisiResponseDTO.builder()
                .id(kg.getId())
                .email(kg.getEmail())
                .iletisimBilgisi(
                        kg.getContactInformation() != null
                                ? iletisimMapper.toDTO(kg.getContactInformation())
                                : null
                )
                .build();
    }
}
