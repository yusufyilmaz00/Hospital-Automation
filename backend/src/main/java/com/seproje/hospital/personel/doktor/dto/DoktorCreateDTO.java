package com.seproje.hospital.personel.doktor.dto;

import com.seproje.hospital.Hastane;
import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import com.seproje.hospital.personel.doktor.Doktor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoktorCreateDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @Valid
    private IletisimBilgisiDTO iletisimBilgisi;

    @NotNull
    private Hastane.Bolum bolum;

    @NotNull
    private Doktor.Unvan unvan;
}
