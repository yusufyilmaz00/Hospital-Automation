package com.seproje.hospital.hasta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HastalikRequestDTO {

    @NotBlank(message = "Hastalık detayı boş olamaz")
    private String detay;
}
