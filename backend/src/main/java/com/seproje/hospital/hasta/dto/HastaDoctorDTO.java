package com.seproje.hospital.hasta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HastaDoctorDTO {
    private String unvan;
    private String bolum;
}
