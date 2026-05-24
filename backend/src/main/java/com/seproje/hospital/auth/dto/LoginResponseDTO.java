package com.seproje.hospital.auth.dto;

import com.seproje.hospital.auth.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    UserType type;
}
