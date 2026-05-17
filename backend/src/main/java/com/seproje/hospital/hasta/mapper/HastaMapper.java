package com.seproje.hospital.hasta.mapper;

import com.seproje.hospital.common.mapper.İletişimBilgisiMapper;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {İletişimBilgisiMapper.class}
)
public interface HastaMapper {

    Hasta toEntity(HastaRequestDTO dto);

    HastaResponseDTO toDTO(Hasta entity);
}