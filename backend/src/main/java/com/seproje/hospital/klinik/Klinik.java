package com.seproje.hospital.klinik;

import com.seproje.hospital.personel.Doktor;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "klinik")
public class Klinik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "bolum", nullable = false, unique = true)
    private BolumTipi bolum;

    @OneToMany(mappedBy = "klinik", cascade = CascadeType.ALL)
    private List<Doktor> doktorlar = new ArrayList<>();
}
