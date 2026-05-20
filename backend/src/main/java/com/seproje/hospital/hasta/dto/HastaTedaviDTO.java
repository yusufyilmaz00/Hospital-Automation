package com.seproje.hospital.hasta.dto;

import com.seproje.hospital.randevu.TedaviTipi;
import com.seproje.hospital.randevu.dto.ReceteDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HastaTedaviDTO {
    private Long id;
    private TedaviTipi tedaviTipi;
    private String aciklama;
    private List<ReceteDTO> receteler;
}
