package com.seproje.hospital.hasta.dto;

import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HastaResponseDTO {

    private Long id;
    private IletisimBilgisiDTO iletisimBilgisi;
    private List<HastalikDTO> hastaliklar;

    private Double boy;
    private Double kilo;
}