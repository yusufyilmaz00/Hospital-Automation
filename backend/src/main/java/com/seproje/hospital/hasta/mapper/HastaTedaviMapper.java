package com.seproje.hospital.hasta.mapper;

import com.seproje.hospital.hasta.dto.HastaTedaviDTO;
import com.seproje.hospital.randevu.Tedavi;
import com.seproje.hospital.randevu.mapper.ReceteMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ReceteMapper.class}
)
public interface HastaTedaviMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "tedaviTipi", target = "tedaviTipi")
    @Mapping(source = "açıklama", target = "aciklama")
    @Mapping(source = "reçeteler", target = "receteler")
    HastaTedaviDTO toDTO(Tedavi tedavi);
}
