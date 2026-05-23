package com.seproje.hospital.personel.doktor.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoktorRandevuDTO {
    private Long id;
    private LocalDateTime randevuZamani;
    private Long hastaId;
    private String hastaIsim;
    private String hastaSoyisim;
    private Integer sureDakika;
    private Double ucret;
}
