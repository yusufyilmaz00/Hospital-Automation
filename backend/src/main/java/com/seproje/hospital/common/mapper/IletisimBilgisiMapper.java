package com.seproje.hospital.common.mapper;

import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import com.seproje.hospital.common.IletisimBilgisi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IletisimBilgisiMapper {

    @Mapping(target = "id", ignore = true)
    IletisimBilgisi toEntity(IletisimBilgisiDTO dto);

    IletisimBilgisiDTO toDTO(IletisimBilgisi entity);
}