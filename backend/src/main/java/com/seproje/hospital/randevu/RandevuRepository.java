package com.seproje.hospital.randevu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RandevuRepository extends JpaRepository<Randevu, Long> {

    Optional<Randevu> findByIdAndDoktorId(Long id, Long doktorId);

    boolean existsByHastaIdAndDoktorId(Long hastaId, Long doktorId);
}
