package com.seproje.hospital.personel.doktor;

import com.seproje.hospital.hasta.dto.HastalikRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastaTedaviDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorCreateDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO;
import com.seproje.hospital.randevu.dto.RaporRequestDTO;
import com.seproje.hospital.randevu.dto.RaporResponseDTO;
import com.seproje.hospital.randevu.dto.ReceteDTO;
import com.seproje.hospital.randevu.dto.ReceteRequestDTO;
import com.seproje.hospital.randevu.dto.RandevuSureGuncelleRequestDTO;
import com.seproje.hospital.randevu.dto.TedaviRequestDTO;
import com.seproje.hospital.security.SecurityContextUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doktor")
@RequiredArgsConstructor
public class DoktorController {

    private final DoctorService doctorService;
    private final DoktorMapper doktorMapper;

    // Admin rolü eklendiğinde korunacak
    @PostMapping("/register")
    public ResponseEntity<DoktorResponseDTO> doktorKaydet(@Valid @RequestBody DoktorCreateDTO dto) {
        Doktor doktor = doctorService.createDoktor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doktorMapper.toDTO(doktor));
    }

    @GetMapping
    public ResponseEntity<List<DoktorResponseDTO>> doktorlariListele() {
        List<DoktorResponseDTO> list = doctorService.getAllDoctors().stream()
                .map(doktorMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/randevular")
    public ResponseEntity<List<DoktorRandevuDTO>> randevulariListele() {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.ok(doctorService.getMyRandevular(doktorId)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/randevu/{randevuId}/hasta")
    public ResponseEntity<HastaResponseDTO> hastayiGor(@PathVariable Long randevuId) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.ok(
                        doctorService.getHastaByRandevuId(doktorId, randevuId)
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/randevu/{randevuId}/sure")
    public ResponseEntity<DoktorRandevuDTO> randevuSuresiGuncelle(
            @PathVariable Long randevuId,
            @Valid @RequestBody RandevuSureGuncelleRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.ok(
                        doctorService.updateRandevuSuresi(doktorId, randevuId, dto.getSureDakika())
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/randevu/{randevuId}/tedavi")
    public ResponseEntity<HastaTedaviDTO> tedaviEkle(
            @PathVariable Long randevuId,
            @Valid @RequestBody TedaviRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(doctorService.addTedavi(doktorId, randevuId, dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/randevu/{randevuId}/tedavi/{tedaviId}")
    public ResponseEntity<Void> tedaviSil(
            @PathVariable Long randevuId,
            @PathVariable Long tedaviId) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.removeTedavi(doktorId, randevuId, tedaviId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/randevu/{randevuId}/tedavi/{tedaviId}/recete")
    public ResponseEntity<ReceteDTO> receteEkle(
            @PathVariable Long randevuId,
            @PathVariable Long tedaviId,
            @Valid @RequestBody ReceteRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(doctorService.addRecete(doktorId, randevuId, tedaviId, dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/randevu/{randevuId}/tedavi/{tedaviId}/recete/{receteId}")
    public ResponseEntity<Void> receteSil(
            @PathVariable Long randevuId,
            @PathVariable Long tedaviId,
            @PathVariable Long receteId) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.removeRecete(doktorId, randevuId, tedaviId, receteId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/randevu/{randevuId}/rapor")
    public ResponseEntity<RaporResponseDTO> raporEkle(
            @PathVariable Long randevuId,
            @Valid @RequestBody RaporRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(doctorService.addRapor(doktorId, randevuId, dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/randevu/{randevuId}/rapor/{raporId}")
    public ResponseEntity<Void> raporSil(
            @PathVariable Long randevuId,
            @PathVariable Long raporId) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.removeRapor(doktorId, randevuId, raporId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // ─── Hasta Bilgilerinin Yönetimi ─────────────────────────────────────────

    @PutMapping("/hasta/{hastaId}/boy")
    public ResponseEntity<Void> hastaninBoyunuGuncelle(
            @PathVariable Long hastaId,
            @RequestParam @Positive Double boy) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.updateHastaBoy(doktorId, hastaId, boy);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/hasta/{hastaId}/kilo")
    public ResponseEntity<Void> hastaninKilosunuGuncelle(
            @PathVariable Long hastaId,
            @RequestParam @Positive Double kilo) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.updateHastaKilo(doktorId, hastaId, kilo);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/hasta/{hastaId}/hastalik")
    public ResponseEntity<Void> hastalikEkle(
            @PathVariable Long hastaId,
            @Valid @RequestBody HastalikRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.addHastalik(doktorId, hastaId, dto.getDetay());
                    return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/hasta/{hastaId}/hastalik/{hastalikId}")
    public ResponseEntity<Void> hastalikGuncelle(
            @PathVariable Long hastaId,
            @PathVariable Long hastalikId,
            @Valid @RequestBody HastalikRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.updateHastalik(doktorId, hastaId, hastalikId, dto.getDetay());
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/hasta/{hastaId}/hastalik/{hastalikId}")
    public ResponseEntity<Void> hastalikSil(
            @PathVariable Long hastaId,
            @PathVariable Long hastalikId) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> {
                    doctorService.deleteHastalik(doktorId, hastaId, hastalikId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
