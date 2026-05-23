package com.seproje.hospital.personel.randevu;

import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.personel.doktor.DoktorMapper;
import com.seproje.hospital.personel.randevu.dto.AlternatifTarihDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AlternatifTarihMapper {

    private final DoktorMapper doktorMapper;

    public AlternatifTarihDTO toDTO(Doktor doktor, List<LocalDateTime> tarihler) {
        return AlternatifTarihDTO.builder()
                .doktor(doktorMapper.toDTO(doktor))
                .tarihler(tarihler)
                .build();
    }
}
