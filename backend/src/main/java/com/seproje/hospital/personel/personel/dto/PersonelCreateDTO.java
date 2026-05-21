package com.seproje.hospital.personel.personel.dto;

import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class PersonelCreateDTO {

    @NotBlank(message = "Email boş olamaz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    private String password;

    @NotNull(message = "İletişim bilgisi boş olamaz")
    private IletisimBilgisiDTO contactInformation;
}