package com.seproje.hospital.hasta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HastaRandevuDTO {
    private Long id;
    private LocalDateTime tarih;
    private HastaDoctorDTO doktor;
    private List<HastaTedaviDTO> tedaviler;
}
