package com.seproje.hospital.personel.vezne.dto;

import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeznedarResponseDTO {

    private Long id;
    private String email;
    private IletisimBilgisiDTO iletisimBilgisi;
}
