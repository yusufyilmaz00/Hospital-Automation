package com.seproje.hospital.randevu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RandevuCreateResponseDTO {
    private Long id;
    private Long hastaId;
    private Long doktorId;
    private LocalDateTime randevuZamani;
    private Integer sureDakika;
}
