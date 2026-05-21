package com.seproje.hospital.hasta;

import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import com.seproje.hospital.common.mapper.IletisimBilgisiMapper;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastalikDTO;
import com.seproje.hospital.hasta.mapper.HastaMapper;
import com.seproje.hospital.security.SecurityContextUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hasta")
@RequiredArgsConstructor
public class HastaController {

    private final HastaService hastaService;
    private final IletisimBilgisiMapper iletisimMapper;

    @GetMapping("/self")
    public ResponseEntity<HastaResponseDTO> getSelf() {
        return SecurityContextUtil.currentUser(Hasta.class)
                .map(hastaId -> ResponseEntity.ok(
                        hastaService.getById(hastaId)
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ─── Self Güncelleme ────────────────────────────────────────────────────

    @PutMapping("/self/boy")
    public ResponseEntity<Void> updateBoy(@RequestParam @Positive Double boy) {
        return SecurityContextUtil.currentUser(Hasta.class)
                .map(hastaId -> {
                    hastaService.updateBoy(hastaId, boy);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/self/kilo")
    public ResponseEntity<Void> updateKilo(@RequestParam @Positive Double kilo) {
        return SecurityContextUtil.currentUser(Hasta.class)
                .map(hastaId -> {
                    hastaService.updateKilo(hastaId, kilo);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/self/iletisim")
    public ResponseEntity<Void> updateIletisim(@Valid @RequestBody IletisimBilgisiDTO dto) {
        IletisimBilgisi iletisim = iletisimMapper.toEntity(dto);

        return SecurityContextUtil.currentUser(Hasta.class)
                .map(hastaId -> {
                    hastaService.updateIletisim(hastaId, iletisim);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}