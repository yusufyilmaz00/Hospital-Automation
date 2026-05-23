package com.seproje.hospital.randevu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RandevuOdemeDTO {
    private Long randevuId;
    private Double brutUcret;
    private Double sigortaIndirimOrani;
    private Double odenecekUcret;
    private Boolean odendi;
}
