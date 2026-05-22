package com.seproje.hospital.hasta;

import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastalikDTO;

import java.util.List;
import java.util.Optional;

public interface HastaService {
    HastaResponseDTO create(HastaRequestDTO dto);
    HastaResponseDTO getById(Long hastaId);
    void updateBoy(Long hastaId, Double boy);
    void updateKilo(Long hastaId, Double kilo);
    void updateIletisim(Long hastaId, IletisimBilgisi iletisim);
    void updatePassword(Long hastaId, String password);
    void updateEmail(Long hastaId, String email);

    Optional<Hasta> findById(Long hastaId);

    List<HastalikDTO> getHastaliklar(Long hastaId);
    void createHastalik(Long hastaId, String hastalik);
    void updateHastalik(Long hastaId, Long hastalikId, String hastalik);
    void deleteHastalik(Long hastaId, Long hastalikId);
}