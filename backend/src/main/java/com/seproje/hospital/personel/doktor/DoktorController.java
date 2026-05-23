package com.seproje.hospital.personel.doktor;

import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorCreateDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO;
import com.seproje.hospital.security.SecurityContextUtil;
import jakarta.validation.Valid;
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
}
