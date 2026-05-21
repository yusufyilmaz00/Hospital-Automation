package com.seproje.hospital.hasta.mapper;

import com.seproje.hospital.hasta.dto.HastaDoctorDTO;
import com.seproje.hospital.personel.Doktor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HastaDoctorMapper {
    @Mapping(source = "department", target = "bolum")
    @Mapping(source = "unvan", target = "unvan")
    HastaDoctorDTO toDTO(Doktor doctor);

    default String map(Doktor.Unvan unvan) {
        return unvan == null ? null : unvan.getDisplayName();
    }
}
