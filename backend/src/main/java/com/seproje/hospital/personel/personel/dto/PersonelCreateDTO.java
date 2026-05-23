package com.seproje.hospital.personel.personel.dto;

import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class PersonelCreateDTO {

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    private String password;

    @NotNull(message = "İletişim bilgisi boş olamaz")
    @Valid
    private IletisimBilgisiDTO contactInformation;
}
