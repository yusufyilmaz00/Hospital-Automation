package com.seproje.hospital.hasta;

import com.seproje.hospital.common.İletişimBilgisi;
import com.seproje.hospital.randevu.Randevu;
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
@Table(name = "hasta")
public class Hasta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iletisim_bilgisi_id", nullable = false)
    private İletişimBilgisi i̇letişimBilgisi;

    @OneToMany(mappedBy = "hasta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Randevu> randevular = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "hasta_hastaliklar")
    @Column(name = "hastalik")
    private List<String> hastalıklar;

    @Column(nullable = false)
    private Double boy;

    @Column(nullable = false)
    private Double kilo;
}
