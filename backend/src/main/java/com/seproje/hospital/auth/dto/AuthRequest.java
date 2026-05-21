package com.seproje.hospital.auth.dto;

import com.seproje.hospital.auth.dto.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank(message = "Şifre boş olamaz")
    @ValidPassword
    private String password;

    @NotBlank(message = "e-Mail boş olamaz")
    @Email
    private String email;

}
