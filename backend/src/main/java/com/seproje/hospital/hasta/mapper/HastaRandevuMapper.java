package com.seproje.hospital.hasta.mapper;

import com.seproje.hospital.hasta.dto.HastaRandevuDTO;
import com.seproje.hospital.randevu.Randevu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {HastaDoctorMapper.class, HastaTedaviMapper.class}
)
public interface HastaRandevuMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "randevuZamani", target = "tarih")
    @Mapping(source = "doktor", target = "doktor")
    @Mapping(source = "tedaviler", target = "tedaviler")
    HastaRandevuDTO toDTO(Randevu randevu);
}
