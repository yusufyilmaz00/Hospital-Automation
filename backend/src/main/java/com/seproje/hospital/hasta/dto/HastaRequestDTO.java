package com.seproje.hospital.hasta.dto;

import com.seproje.hospital.common.dto.İletişimBilgisiDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HastaRequestDTO {

    private İletişimBilgisiDTO iletisimBilgisi;

    private List<String> hastaliklar;

    private Double boy;
    private Double kilo;
}