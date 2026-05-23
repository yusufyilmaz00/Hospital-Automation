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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Doktor", description = "Doktor kaydı, randevu görüntüleme, tedavi, reçete, rapor ve hasta klinik bilgileri")
public class DoktorController {

    private final DoctorService doctorService;
    private final DoktorMapper doktorMapper;

    // Admin rolü eklendiğinde korunacak
    @Operation(summary = "Doktor oluşturur", description = "Demo/test akışı için doktor oluşturur. Oluşturulan doktor /api/auth/login ile giriş yapabilir.")
    @PostMapping("/register")
    public ResponseEntity<DoktorResponseDTO> doktorKaydet(@Valid @RequestBody DoktorCreateDTO dto) {
        Doktor doktor = doctorService.createDoktor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doktorMapper.toDTO(doktor));
    }

    @Operation(summary = "Doktorları listeler", description = "Sistemdeki doktorları döner.")
    @GetMapping
    public ResponseEntity<List<DoktorResponseDTO>> doktorlariListele() {
        List<DoktorResponseDTO> list = doctorService.getAllDoctors().stream()
                .map(doktorMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Doktor randevularını listeler", description = "Giriş yapan doktorun kendi randevularını döner.")
    @GetMapping("/randevular")
    public ResponseEntity<List<DoktorRandevuDTO>> randevulariListele() {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.ok(doctorService.getMyRandevular(doktorId)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Randevudaki hastayı görüntüler", description = "Giriş yapan doktorun kendi randevusundaki hasta detaylarını döner.")
    @GetMapping("/randevu/{randevuId}/hasta")
    public ResponseEntity<HastaResponseDTO> hastayiGor(@PathVariable Long randevuId) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.ok(
                        doctorService.getHastaByRandevuId(doktorId, randevuId)
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Randevu süresini günceller", description = "Doktorun randevu sonrasında süreDakika bilgisini set etmesini sağlar. Ödemesi alınmış randevu güncellenemez.")
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

    @Operation(summary = "Randevuya tedavi ekler", description = "Tedavi tipi ve açıklama ile randevuya tedavi ekler. Tedaviler ödeme hesaplamasında kullanılır.")
    @PostMapping("/randevu/{randevuId}/tedavi")
    public ResponseEntity<HastaTedaviDTO> tedaviEkle(
            @PathVariable Long randevuId,
            @Valid @RequestBody TedaviRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(doctorService.addTedavi(doktorId, randevuId, dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Tedavi siler", description = "Doktorun kendi randevusundaki tedaviyi siler. Ödemesi alınmış randevu güncellenemez.")
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

    @Operation(summary = "Tedaviye reçete ekler", description = "Tedaviye barkodu sistem tarafından üretilen reçete ekler. ilaclar listesi boş olabilir.")
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

    @Operation(summary = "Reçete siler", description = "Doktorun kendi randevusundaki tedaviye ait reçeteyi siler.")
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

    @Operation(summary = "Randevuya rapor ekler", description = "Doktorun kendi randevusuna rapor ekler.")
    @PostMapping("/randevu/{randevuId}/rapor")
    public ResponseEntity<RaporResponseDTO> raporEkle(
            @PathVariable Long randevuId,
            @Valid @RequestBody RaporRequestDTO dto) {
        return SecurityContextUtil.currentUser(Doktor.class)
                .map(doktorId -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(doctorService.addRapor(doktorId, randevuId, dto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Rapor siler", description = "Doktorun kendi randevusundaki raporu siler.")
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

    @Operation(summary = "Hasta boyunu günceller", description = "Doktorun erişim yetkisi olan hastanın boy bilgisini günceller.")
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

    @Operation(summary = "Hasta kilosunu günceller", description = "Doktorun erişim yetkisi olan hastanın kilo bilgisini günceller.")
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

    @Operation(summary = "Hasta hastalığı ekler", description = "Doktorun erişim yetkisi olan hastaya hastalık bilgisi ekler.")
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

    @Operation(summary = "Hasta hastalığını günceller", description = "Doktorun erişim yetkisi olan hastadaki hastalık bilgisini günceller.")
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

    @Operation(summary = "Hasta hastalığını siler", description = "Doktorun erişim yetkisi olan hastadaki hastalık bilgisini siler.")
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
