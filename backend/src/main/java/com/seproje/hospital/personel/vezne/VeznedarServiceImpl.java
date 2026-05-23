package com.seproje.hospital.personel.vezne;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.personel.vezne.dto.VeznedarCreateDTO;
import com.seproje.hospital.personel.vezne.dto.VeznedarResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VeznedarServiceImpl implements VeznedarService {

    private final VeznedarRepository veznedarRepository;
    private final IletisimBilgisiMapper iletisimBilgisiMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public VeznedarResponseDTO create(VeznedarCreateDTO dto) {
        Veznedar veznedar = new Veznedar();
        veznedar.setEmail(dto.getEmail());
        veznedar.setPassword(passwordEncoder.encode(dto.getPassword()));
        veznedar.setContactInformation(iletisimBilgisiMapper.toEntity(dto.getContactInformation()));

        Veznedar saved = veznedarRepository.save(veznedar);
        return VeznedarResponseDTO.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .iletisimBilgisi(iletisimBilgisiMapper.toDTO(saved.getContactInformation()))
                .build();
    }
}
