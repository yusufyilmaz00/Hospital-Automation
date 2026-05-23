package com.seproje.hospital.personel.doktor;

import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.randevu.Randevu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoktorRandevuMapper {

    @Mapping(source = "hasta.id", target = "hastaId")
    @Mapping(source = "hasta.iletisimBilgisi.isim", target = "hastaIsim")
    @Mapping(source = "hasta.iletisimBilgisi.soyisim", target = "hastaSoyisim")
    @Mapping(target = "ucret", expression = "java(randevu.getÜcret())")
    DoktorRandevuDTO toDTO(Randevu randevu);
}
