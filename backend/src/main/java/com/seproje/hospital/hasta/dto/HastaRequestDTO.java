package com.seproje.hospital.hasta.dto;

import com.seproje.hospital.auth.dto.validator.ValidPassword;
import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HastaRequestDTO {

    @Valid
    @NotNull(message = "İletişim bilgisi boş olamaz")
    private IletisimBilgisiDTO iletisimBilgisi;

    @NotNull(message = "Boy boş olamaz")
    @Positive(message = "Boy pozitif bir değer olmalıdır")
    private Double boy;

    @NotNull(message = "Kilo boş olamaz")
    @Positive(message = "Kilo pozitif bir değer olmalıdır")
    private Double kilo;

    @NotNull(message = "eMail boş olamaz")
    @Email
    private String email;

    @NotNull(message = "Şifre boş olamaz")
    @ValidPassword
    private String password;

    private List<String> hastaliklar;
}