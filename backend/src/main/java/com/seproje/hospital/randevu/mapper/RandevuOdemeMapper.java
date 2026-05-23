package com.seproje.hospital.randevu.mapper;

import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.randevu.dto.RandevuOdemeDTO;
import org.springframework.stereotype.Component;

@Component
public class RandevuOdemeMapper {

    public RandevuOdemeDTO toDTO(Randevu randevu, Double brutUcret) {
        return RandevuOdemeDTO.builder()
                .randevuId(randevu.getId())
                .brutUcret(brutUcret)
                .sigortaIndirimOrani(randevu.getSigortaIndirimOrani())
                .odenecekUcret(randevu.getÜcret())
                .odendi(randevu.getOdendi())
                .build();
    }
}
