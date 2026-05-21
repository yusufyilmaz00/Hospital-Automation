package com.seproje.hospital.personel.doktor;

import com.seproje.hospital.auth.AuthUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends AuthUserRepository<Doktor> {
}
