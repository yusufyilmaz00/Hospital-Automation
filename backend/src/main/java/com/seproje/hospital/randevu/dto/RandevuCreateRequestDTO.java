package com.seproje.hospital.randevu.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RandevuCreateRequestDTO {

    @NotNull(message = "Hasta ID boş olamaz")
    private Long hastaId;

    @NotNull(message = "Doktor ID boş olamaz")
    private Long doktorId;

    @NotNull(message = "Randevu zamanı boş olamaz")
    @Future(message = "Randevu zamanı gelecekte olmalıdır")
    private LocalDateTime randevuZamani;
}
