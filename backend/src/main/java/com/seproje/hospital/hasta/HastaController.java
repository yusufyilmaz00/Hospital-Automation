package com.seproje.hospital.hasta;

import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.mapper.HastaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hasta")
@RequiredArgsConstructor
public class HastaController {

    private final HastaRepository hastaRepository;
    private final HastaMapper hastaMapper;

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
    public HastaResponseDTO create(@RequestBody HastaRequestDTO dto) {

        Hasta hasta = hastaMapper.toEntity(dto);

        return hastaMapper.toDTO(
                hastaRepository.save(hasta)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public HastaResponseDTO update(@PathVariable Long id,
                                   @RequestBody HastaRequestDTO dto) {

        Hasta existing = hastaRepository.findById(id)
                .orElseThrow();

        Hasta updated = hastaMapper.toEntity(dto);
        updated.setId(existing.getId());

        return hastaMapper.toDTO(
                hastaRepository.save(updated)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        hastaRepository.deleteById(id);
    }
}