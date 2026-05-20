package com.seproje.hospital.hasta.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HastaDoctorDTO {
    private String unvan;
    private String bolum;
}
