package com.seproje.hospital;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.klinik.Klinik;
import com.seproje.hospital.personel.Personel;

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
@Table(name = "hastane")
public class Hastane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ad;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hastane_id")
    private List<Klinik> klinikler = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hastane_id")
    private List<Hasta> hastalar = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hastane_id")
    private List<Personel> personeller = new ArrayList<>();
}

