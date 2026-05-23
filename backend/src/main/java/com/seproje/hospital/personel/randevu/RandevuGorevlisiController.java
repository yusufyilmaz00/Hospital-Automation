package com.seproje.hospital.personel.randevu;

import com.seproje.hospital.personel.doktor.DoctorService;
import com.seproje.hospital.personel.doktor.DoktorMapper;
import com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO;
import com.seproje.hospital.personel.randevu.dto.AlternatifTarihDTO;
import com.seproje.hospital.personel.randevu.dto.AlternatifTarihRequestDTO;
import com.seproje.hospital.personel.randevu.dto.RandevuGorevlisiCreateDTO;
import com.seproje.hospital.randevu.RandevuRepository;
import com.seproje.hospital.randevu.dto.RandevuCreateRequestDTO;
import com.seproje.hospital.randevu.dto.RandevuCreateResponseDTO;
import com.seproje.hospital.randevu.mapper.RandevuCreateResponseMapper;
import com.seproje.hospital.security.SecurityContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/randevu-gorevlisi")
@RequiredArgsConstructor
@Tag(name = "Randevu Görevlisi", description = "Doktor listeleme, randevu oluşturma, iptal ve alternatif tarih önerileri")
public class RandevuGorevlisiController {

    private final RandevuGorevlisiService randevuGorevlisiService;
    private final DoctorService doctorService;
    private final DoktorMapper doktorMapper;
    private final RandevuRepository randevuRepository;
    private final RandevuCreateResponseMapper randevuCreateResponseMapper;

    // Admin rolü eklendiğinde korunacak
    @Operation(
            summary = "Randevu görevlisi oluşturur",
            description = "Demo/test akışı için randevu görevlisi oluşturur. Oluşturulan kullanıcı /api/auth/login ile giriş yapabilir."
    )
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RandevuGorevlisiCreateDTO dto) {
        randevuGorevlisiService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Doktorları listeler",
            description = "Randevu oluştururken seçilebilecek doktorları döner."
    )
    @GetMapping("/doktorlar")
    public ResponseEntity<List<DoktorResponseDTO>> doktorlariListele() {
        return SecurityContextUtil.currentUser(RandevuGorevlisi.class)
                .map(id -> {
                    List<DoktorResponseDTO> list = doctorService.getAllDoctors().stream()
                            .map(doktorMapper::toDTO)
                            .toList();
                    return ResponseEntity.ok(list);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @Operation(
            summary = "Randevu oluşturur",
            description = "hastaId, doktorId ve randevuZamani ile randevu oluşturur. Doktor o saatte müsait değilse 400 döner; frontend aynı haftadaki öneriler için alternatif-tarihler endpointini çağırmalıdır."
    )
    @PostMapping("/randevu")
    public ResponseEntity<RandevuCreateResponseDTO> randevuOlustur(@Valid @RequestBody RandevuCreateRequestDTO dto) {
        return SecurityContextUtil.currentUser(RandevuGorevlisi.class)
                .map(id -> {
                    var randevu = randevuGorevlisiService.randevuIslemiYap(
                            dto.getHastaId(), dto.getDoktorId(), dto.getRandevuZamani());
                    return ResponseEntity.status(HttpStatus.CREATED).body(randevuCreateResponseMapper.toDTO(randevu));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @Operation(
            summary = "Randevuyu iptal eder",
            description = "Randevu görevlisi session cookie'si ile var olan randevuyu siler."
    )
    @DeleteMapping("/randevu/{randevuId}")
    public ResponseEntity<Void> randevuIptalEt(@PathVariable Long randevuId) {
        return SecurityContextUtil.currentUser(RandevuGorevlisi.class)
                .map(id -> {
                    Long doktorId = randevuRepository.findById(randevuId)
                            .orElseThrow(() -> new EntityNotFoundException("Randevu bulunamadı: " + randevuId))
                            .getDoktor().getId();
                    randevuGorevlisiService.randevuIptalEt(doktorId, randevuId);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @Operation(
            summary = "Bir haftalık alternatif tarihleri listeler",
            description = "haftaBaslangic=YYYY-MM-DD parametresi alır; o hafta içinde müsait doktorları ve 30 dakikalık müsait slotlarını döner."
    )
    @GetMapping("/alternatif-tarihler")
    public ResponseEntity<List<AlternatifTarihDTO>> alternatifTarihler(@Valid @ModelAttribute AlternatifTarihRequestDTO dto) {
        return SecurityContextUtil.currentUser(RandevuGorevlisi.class)
                .map(id -> ResponseEntity.ok(randevuGorevlisiService.alternatifTarihleriListele(dto.getHaftaBaslangic())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
