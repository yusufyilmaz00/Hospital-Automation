package com.seproje.hospital.common.mapper;

import com.seproje.hospital.common.dto.İletişimBilgisiDTO;
import com.seproje.hospital.common.İletişimBilgisi;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface İletişimBilgisiMapper {

    İletişimBilgisi toEntity(İletişimBilgisiDTO dto);

    İletişimBilgisiDTO toDTO(İletişimBilgisi entity);
}