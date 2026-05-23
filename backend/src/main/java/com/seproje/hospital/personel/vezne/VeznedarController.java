package com.seproje.hospital.personel.vezne;

import com.seproje.hospital.randevu.RandevuService;
import com.seproje.hospital.randevu.dto.RandevuOdemeDTO;
import com.seproje.hospital.security.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/veznedar")
@RequiredArgsConstructor
public class VeznedarController {

    private final RandevuService randevuService;

    @PostMapping("/randevu/{randevuId}/odeme")
    public ResponseEntity<RandevuOdemeDTO> randevuOdemesiAl(@PathVariable Long randevuId) {
        return SecurityContextUtil.currentUser(Veznedar.class)
                .map(id -> ResponseEntity.ok(randevuService.ucretHesaplaVeOdemeAl(randevuId)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
