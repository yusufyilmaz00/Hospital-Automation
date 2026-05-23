package com.seproje.hospital.randevu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceteRepository extends JpaRepository<Reçete, Long> {

    boolean existsByBarkod(String barkod);

    Optional<Reçete> findByIdAndTedaviIdAndTedaviRandevuIdAndTedaviRandevuDoktorId(
            Long id,
            Long tedaviId,
            Long randevuId,
            Long doktorId);
}
