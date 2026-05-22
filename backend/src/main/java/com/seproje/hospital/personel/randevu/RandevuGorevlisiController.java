package com.seproje.hospital.personel.randevu;

import com.seproje.hospital.personel.doktor.DoctorService;
import com.seproje.hospital.personel.doktor.DoktorMapper;
import com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO;
import com.seproje.hospital.personel.randevu.dto.RandevuGorevlisiCreateDTO;
import com.seproje.hospital.randevu.RandevuRepository;
import com.seproje.hospital.randevu.dto.RandevuCreateRequestDTO;
import com.seproje.hospital.security.SecurityContextUtil;
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
public class RandevuGorevlisiController {

    private final RandevuGorevlisiService randevuGorevlisiService;
    private final DoctorService doctorService;
    private final DoktorMapper doktorMapper;
    private final RandevuRepository randevuRepository;

    // Admin rolü eklendiğinde korunacak
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RandevuGorevlisiCreateDTO dto) {
        randevuGorevlisiService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

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

    @PostMapping("/randevu")
    public ResponseEntity<Void> randevuOlustur(@Valid @RequestBody RandevuCreateRequestDTO dto) {
        return SecurityContextUtil.currentUser(RandevuGorevlisi.class)
                .map(id -> {
                    randevuGorevlisiService.randevuIslemiYap(
                            dto.getHastaId(), dto.getDoktorId(), dto.getRandevuZamani());
                    return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

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
}
