package com.seproje.hospital.personel.vezne;

import com.seproje.hospital.personel.vezne.dto.VeznedarCreateDTO;
import com.seproje.hospital.personel.vezne.dto.VeznedarResponseDTO;

public interface VeznedarService {

    VeznedarResponseDTO create(VeznedarCreateDTO dto);
}
