package com.seproje.hospital.personel.kayit;

import com.seproje.hospital.hasta.HastaService;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.personel.kayit.dto.KayitGorevlisiRequestDTO;
import com.seproje.hospital.personel.kayit.dto.KayitGorevlisiResponseDTO;
import com.seproje.hospital.security.SecurityContextUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kayit")
@RequiredArgsConstructor
public class KayitGorevlisiController {

    private final HastaService hastaService;
    private final KayitGorevlisiService kayitGorevlisiService;

    // Admin rolü eklendiğinde bu endpoint korunacak
    @PostMapping("/register")
    public ResponseEntity<KayitGorevlisiResponseDTO> kayitGorevlisiOlustur(
            @Valid @RequestBody KayitGorevlisiRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(kayitGorevlisiService.create(dto));
    }

    @PostMapping("/hasta")
    public ResponseEntity<HastaResponseDTO> hastaKaydet(@Valid @RequestBody HastaRequestDTO dto) {
        return SecurityContextUtil.currentUser(KayitGorevlisi.class)
                .map(id -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(hastaService.create(dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/hastalar")
    public ResponseEntity<List<HastaResponseDTO>> hastalariListele() {
        return SecurityContextUtil.currentUser(KayitGorevlisi.class)
                .map(id -> ResponseEntity.ok(hastaService.getAll()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
