package com.seproje.hospital.randevu.dto;

import com.seproje.hospital.randevu.TedaviTipi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TedaviRequestDTO {

    @NotNull(message = "Tedavi tipi boş olamaz")
    private TedaviTipi tedaviTipi;

    @NotBlank(message = "Açıklama boş olamaz")
    private String aciklama;
}
