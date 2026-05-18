package com.seproje.hospital.common.dto;

import com.seproje.hospital.hasta.dto.validator.ValidTckn;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IletisimBilgisiDTO {

    @NotBlank(message = "İsim boş olamaz")
    private String isim;

    @NotBlank(message = "Soyisim boş olamaz")
    private String soyisim;

    @NotBlank(message = "TCKN boş olamaz")
    @ValidTckn
    private String tckno;

    @NotBlank(message = "Telefon boş olamaz")
    private String telefon;

    @NotBlank(message = "Adres boş olamaz")
    private String adres;

    @NotNull(message = "Doğum tarihi boş olamaz")
    private LocalDate doğumTarihi;
}