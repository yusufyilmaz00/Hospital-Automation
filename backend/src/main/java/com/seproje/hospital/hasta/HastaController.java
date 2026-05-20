package com.seproje.hospital.hasta;

import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.mapper.HastaMapper;
import com.seproje.hospital.security.SecurityContextUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hasta")
@RequiredArgsConstructor
public class HastaController {

    private final HastaRepository hastaRepository;
    private final HastaMapper hastaMapper;
    private final HastaService hastaService;

    // GET ALL
    @GetMapping
    public List<HastaResponseDTO> getAll() {
        return hastaRepository.findAll()
                .stream()
                .map(hastaMapper::toDTO)
                .toList();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public HastaResponseDTO getById(@PathVariable Long id) {
        return hastaRepository.findById(id)
                .map(hastaMapper::toDTO)
                .orElseThrow();
    }

    // CREATE
    @PostMapping
    public HastaResponseDTO create(@Valid @RequestBody HastaRequestDTO dto) {
        return hastaService.create(dto);
    }

    // UPDATE
    @PutMapping("/{id}")
    public HastaResponseDTO update(@PathVariable Long id,
                                   @Valid @RequestBody HastaRequestDTO dto) {

        Hasta existing = hastaRepository.findById(id)
                .orElseThrow();

        Hasta updated = hastaMapper.toEntity(dto);
        updated.setId(existing.getId());

        return hastaMapper.toDTO(
                hastaRepository.save(updated)
        );
    }

    @GetMapping("/self")
    public ResponseEntity<HastaResponseDTO> getSelf(HttpServletResponse response) {
        Optional<Hasta> hastaOptional = SecurityContextUtil.currentUser(Hasta.class);
        return hastaOptional.map(hasta -> ResponseEntity.ok(hastaMapper.toDTO(hasta)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        hastaRepository.deleteById(id);
    }
}