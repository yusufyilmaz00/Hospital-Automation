package com.seproje.hospital.personel.personel;

import com.seproje.hospital.auth.AuthUser;
import lombok.Data;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Data
@MappedSuperclass
public abstract class Personel implements AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personel_seq")
    @SequenceGenerator(name = "personel_seq", sequenceName = "personel_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iletisim_bilgisi_id")
    private IletisimBilgisi contactInformation;
}
