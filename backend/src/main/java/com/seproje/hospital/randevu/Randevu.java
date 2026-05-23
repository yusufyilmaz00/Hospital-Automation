package com.seproje.hospital.randevu;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.personel.doktor.Doktor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "randevu")
public class Randevu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "randevu_zamani", nullable = false)
    private LocalDateTime randevuZamani;

    @ManyToOne
    @JoinColumn(name = "hasta_id", nullable = false)
    private Hasta hasta;

    @ManyToOne
    @JoinColumn(name = "doktor_id", nullable = false)
    private Doktor doktor;

    @Column(name = "ucret")
    private Double ücret;

    @Column(name = "sure_dakika")
    @Builder.Default
    private Integer sureDakika = 30;

    @Column(name = "sigorta_indirim_orani")
    @Builder.Default
    private Double sigortaIndirimOrani = 0.0;

    @Column(name = "odendi", nullable = false)
    @Builder.Default
    private Boolean odendi = false;

    @OneToMany(mappedBy = "randevu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Tedavi> tedaviler = new ArrayList<>();
}
