package com.seproje.hospital.randevu.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReceteDTO {
    private Long id;
    private String barkod;
    private List<String> ilaclar;
}
