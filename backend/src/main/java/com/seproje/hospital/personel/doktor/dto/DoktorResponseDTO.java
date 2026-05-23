package com.seproje.hospital.personel.doktor.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoktorResponseDTO {
    private Long id;
    private String isim;
    private String soyisim;
    private String bolum;
    private String unvan;
}
