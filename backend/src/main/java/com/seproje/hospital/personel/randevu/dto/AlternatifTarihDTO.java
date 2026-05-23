package com.seproje.hospital.personel.randevu.dto;

import com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlternatifTarihDTO {
    private DoktorResponseDTO doktor;
    private List<LocalDateTime> tarihler;
}
