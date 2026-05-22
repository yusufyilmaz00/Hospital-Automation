package com.seproje.hospital.personel.kayit.dto;

import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KayitGorevlisiResponseDTO {

    private Long id;
    private String email;
    private IletisimBilgisiDTO iletisimBilgisi;
}
