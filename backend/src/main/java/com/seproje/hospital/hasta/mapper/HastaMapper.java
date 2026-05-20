package com.seproje.hospital.hasta.mapper;

import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {IletisimBilgisiMapper.class, HastalikMapper.class}
)
public interface HastaMapper {

    Hasta toEntity(HastaRequestDTO dto);

    @Mapping(source = "hastaliklar", target = "hastaliklar")
    HastaResponseDTO toDTO(Hasta entity);

    default List<String> map(List<String> value) {
        return value == null ? new ArrayList<>() : value;
    }
}