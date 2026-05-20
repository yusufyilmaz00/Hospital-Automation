package com.seproje.hospital.hasta;

import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastalikDTO;

import java.util.List;

public interface HastaService {
    HastaResponseDTO create(HastaRequestDTO dto);

    void updateBoy(Hasta hasta, Double boy);
    void updateKilo(Hasta hasta, Double kilo);
    void updateIletisim(Hasta hasta, IletisimBilgisi iletisim);
    void updatePassword(Hasta hasta, String password);
    void updateEmail(Hasta hasta, String email);

    List<HastalikDTO> getHastaliklar(Hasta hasta);
    void createHastalik(Hasta hasta, String hastalik);
    void updateHastalik(Hasta hasta, Long id, String hastalik);
    void deleteHastalik(Hasta hasta, Long id);
}