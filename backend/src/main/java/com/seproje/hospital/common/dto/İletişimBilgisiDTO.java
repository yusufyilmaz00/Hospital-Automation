package com.seproje.hospital.common.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class İletişimBilgisiDTO {

    private String isim;
    private String soyisim;
    private String tckno;
    private String telefon;
    private String adres;
    private LocalDate doğumTarihi;
}