package com.seproje.hospital.randevu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TedaviRepository extends JpaRepository<Tedavi, Long> {

    Optional<Tedavi> findByIdAndRandevuIdAndRandevuDoktorId(Long id, Long randevuId, Long doktorId);
}
