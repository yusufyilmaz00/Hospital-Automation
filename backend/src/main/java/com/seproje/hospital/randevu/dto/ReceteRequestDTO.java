package com.seproje.hospital.randevu.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceteRequestDTO {

    @NotEmpty(message = "Reçetede en az bir ilaç bulunmalıdır")
    private List<String> ilaclar;
}
