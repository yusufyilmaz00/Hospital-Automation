package com.seproje.hospital.randevu.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandevuSureGuncelleRequestDTO {

    @NotNull(message = "Randevu süresi boş olamaz")
    @Positive(message = "Randevu süresi pozitif olmalıdır")
    private Integer sureDakika;
}
