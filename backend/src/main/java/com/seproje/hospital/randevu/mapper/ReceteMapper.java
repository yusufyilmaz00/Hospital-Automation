package com.seproje.hospital.randevu.mapper;

import com.seproje.hospital.randevu.Reçete;
import com.seproje.hospital.randevu.dto.ReceteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceteMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "barkod", target = "barkod")
    @Mapping(source = "ilaçlar", target = "ilaclar")
    ReceteDTO toDTO(Reçete receete);
}
