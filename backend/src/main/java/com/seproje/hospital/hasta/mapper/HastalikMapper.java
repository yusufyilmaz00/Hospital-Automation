package com.seproje.hospital.hasta.mapper;

import com.seproje.hospital.hasta.Hastalik;
import com.seproje.hospital.hasta.dto.HastalikDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HastalikMapper {
    @Mapping(target = "hasta", ignore = true)
    Hastalik toEntity(HastalikDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "detay", target = "detay")
    HastalikDTO toDTO(Hastalik entity);

    default Hastalik map(String value) {
        if (value == null) return null;

        Hastalik h = new Hastalik();
        h.setDetay(value);
        return h;
    }

    default String map(Hastalik value) {
        return value == null ? null : value.getDetay();
    }
}