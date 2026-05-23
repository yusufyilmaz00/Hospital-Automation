package com.seproje.hospital.personel.vezne;

import com.seproje.hospital.personel.vezne.dto.VeznedarCreateDTO;
import com.seproje.hospital.personel.vezne.dto.VeznedarResponseDTO;
import com.seproje.hospital.randevu.RandevuService;
import com.seproje.hospital.randevu.dto.RandevuOdemeDTO;
import com.seproje.hospital.security.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/veznedar")
@RequiredArgsConstructor
@Tag(name = "Veznedar", description = "Vezne personeli kaydı ve randevu ödeme işlemleri")
public class VeznedarController {

    private final RandevuService randevuService;
    private final VeznedarService veznedarService;

    @Operation(
            summary = "Veznedar oluşturur",
            description = "Demo ve test akışlarında vezne personeli oluşturmak için kullanılır. Oluşturulan kullanıcı /api/auth/login ile giriş yapabilir."
    )
    @PostMapping("/register")
    public ResponseEntity<VeznedarResponseDTO> veznedarOlustur(@Valid @RequestBody VeznedarCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veznedarService.create(dto));
    }

    @Operation(
            summary = "Randevu ödemesini alır",
            description = "Randevudaki tedaviler, doktor unvanı, süre ve sigorta indirimi üzerinden ücreti hesaplar; randevuyu ödenmiş olarak işaretler."
    )
    @PostMapping("/randevu/{randevuId}/odeme")
    public ResponseEntity<RandevuOdemeDTO> randevuOdemesiAl(@PathVariable Long randevuId) {
        return SecurityContextUtil.currentUser(Veznedar.class)
                .map(id -> ResponseEntity.ok(randevuService.ucretHesaplaVeOdemeAl(randevuId)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
