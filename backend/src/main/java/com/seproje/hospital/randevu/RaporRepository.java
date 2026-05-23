package com.seproje.hospital.randevu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RaporRepository extends JpaRepository<Rapor, Long> {

    Optional<Rapor> findByIdAndRandevuDoktorId(Long id, Long doktorId);
}
