package com.seproje.hospital.randevu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceteRepository extends JpaRepository<Reçete, Long> {

    boolean existsByBarkod(String barkod);
}
