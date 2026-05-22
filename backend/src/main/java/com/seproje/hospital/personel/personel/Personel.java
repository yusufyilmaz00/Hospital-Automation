package com.seproje.hospital.personel.personel;

import com.seproje.hospital.auth.AuthUser;
import lombok.Data;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Data
@MappedSuperclass
public abstract class Personel implements AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iletisim_bilgisi_id")
    private IletisimBilgisi contactInformation;
}
