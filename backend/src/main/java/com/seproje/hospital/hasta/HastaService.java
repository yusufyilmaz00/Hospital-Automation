package com.seproje.hospital.hasta;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.common.IletisimBilgisiRepository;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.mapper.HastaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HastaService {

    private final HastaRepository hastaRepository;
    private final IletisimBilgisiRepository iletisimRepository;
    private final IletisimBilgisiMapper iletisimMapper;
    private final HastaMapper hastaMapper;

    public HastaResponseDTO create(HastaRequestDTO dto) {

        IletisimBilgisi iletisim = iletisimMapper.toEntity(dto.getIletisimBilgisi());
        iletisim = iletisimRepository.save(iletisim);

        Hasta hasta = Hasta.builder()
                .iletisimBilgisi(iletisim)
                .boy(dto.getBoy())
                .kilo(dto.getKilo())
                .hastaliklar(dto.getHastaliklar())
                .build();

        return hastaMapper.toDTO(hastaRepository.save(hasta));
    }
}