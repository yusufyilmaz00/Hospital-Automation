package com.seproje.hospital.randevu;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tedavi")
public class Tedavi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tedavi_tipi", nullable = false)
    private TedaviTipi tedaviTipi;

    @Column(name = "aciklama", columnDefinition = "TEXT")
    private String açıklama;

    @ManyToOne
    @JoinColumn(name = "randevu_id", nullable = false)
    private Randevu randevu;

    @OneToMany(mappedBy = "tedavi", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Reçete> reçeteler = new ArrayList<>();
}
