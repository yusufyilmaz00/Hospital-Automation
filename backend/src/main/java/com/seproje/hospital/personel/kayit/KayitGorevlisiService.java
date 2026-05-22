package com.seproje.hospital.personel.kayit;

import com.seproje.hospital.personel.kayit.dto.KayitGorevlisiRequestDTO;
import com.seproje.hospital.personel.kayit.dto.KayitGorevlisiResponseDTO;

import java.util.List;

public interface KayitGorevlisiService {

    KayitGorevlisiResponseDTO create(KayitGorevlisiRequestDTO dto);

    List<KayitGorevlisiResponseDTO> getAll();
}
