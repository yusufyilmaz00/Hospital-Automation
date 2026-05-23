package com.seproje.hospital.randevu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RaporRequestDTO {

    @NotBlank(message = "Rapor içeriği boş olamaz")
    private String icerik;
}
