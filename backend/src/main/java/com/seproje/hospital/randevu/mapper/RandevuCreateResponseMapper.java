package com.seproje.hospital.randevu.mapper;

import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.randevu.dto.RandevuCreateResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RandevuCreateResponseMapper {

    public RandevuCreateResponseDTO toDTO(Randevu randevu) {
        return RandevuCreateResponseDTO.builder()
                .id(randevu.getId())
                .hastaId(randevu.getHasta().getId())
                .doktorId(randevu.getDoktor().getId())
                .randevuZamani(randevu.getRandevuZamani())
                .sureDakika(randevu.getSureDakika())
                .build();
    }
}
