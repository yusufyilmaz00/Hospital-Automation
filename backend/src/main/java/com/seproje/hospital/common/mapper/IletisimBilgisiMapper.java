package com.seproje.hospital.common.mapper;

import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import com.seproje.hospital.common.IletisimBilgisi;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IletisimBilgisiMapper {

    IletisimBilgisi toEntity(IletisimBilgisiDTO dto);

    IletisimBilgisiDTO toDTO(IletisimBilgisi entity);
}