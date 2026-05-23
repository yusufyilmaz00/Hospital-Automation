package com.seproje.hospital.randevu.mapper;

import com.seproje.hospital.randevu.Rapor;
import com.seproje.hospital.randevu.dto.RaporResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RaporMapper {

    RaporResponseDTO toDTO(Rapor rapor);
}
