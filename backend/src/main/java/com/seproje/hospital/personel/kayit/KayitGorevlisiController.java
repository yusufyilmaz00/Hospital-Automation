package com.seproje.hospital.personel.kayit;

import com.seproje.hospital.hasta.HastaService;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.security.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kayit")
@RequiredArgsConstructor
public class KayitGorevlisiController {

    private final HastaService hastaService;

    @PostMapping("/hasta")
    public ResponseEntity<HastaResponseDTO> hastaKaydet(@Valid @RequestBody HastaRequestDTO dto) {
        return SecurityContextUtil.currentUser(KayitGorevlisi.class)
                .map(id -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(hastaService.create(dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
