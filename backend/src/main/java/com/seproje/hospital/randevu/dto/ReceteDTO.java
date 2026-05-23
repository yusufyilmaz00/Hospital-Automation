package com.seproje.hospital.randevu.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceteDTO {
    private Long id;
    private String barkod;
    private List<String> ilaclar;
}
