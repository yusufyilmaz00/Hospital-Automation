package com.seproje.hospital.personel.kayit.dto;

import com.seproje.hospital.auth.dto.validator.ValidPassword;
import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KayitGorevlisiRequestDTO {

    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Şifre boş olamaz")
    @ValidPassword
    private String password;

    @Valid
    private IletisimBilgisiDTO iletisimBilgisi;
}
