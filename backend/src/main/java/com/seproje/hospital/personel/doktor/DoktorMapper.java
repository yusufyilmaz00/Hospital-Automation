package com.seproje.hospital.personel.doktor;

import com.seproje.hospital.Hastane;
import com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoktorMapper {

    @Mapping(source = "contactInformation.isim", target = "isim")
    @Mapping(source = "contactInformation.soyisim", target = "soyisim")
    @Mapping(source = "department", target = "bolum")
    @Mapping(source = "unvan", target = "unvan")
    DoktorResponseDTO toDTO(Doktor doktor);

    default String mapBolum(Hastane.Bolum bolum) {
        return bolum == null ? null : bolum.name();
    }

    default String mapUnvan(Doktor.Unvan unvan) {
        return unvan == null ? null : unvan.getDisplayName();
    }
}
