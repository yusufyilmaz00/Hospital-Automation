package com.seproje.hospital.randevu.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceteRequestDTO {

    private List<String> ilaclar;
}
