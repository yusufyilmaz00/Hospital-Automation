package com.seproje.hospital.common;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "iletisim_bilgisi")
public class IletisimBilgisi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String isim;

    @Column(nullable = false, length = 255)
    private String soyisim;

    @Column(nullable = false, length = 11)
    private String tckno;

    @Column(nullable = false, length = 20)
    private String telefon;

    @Lob
    @Column(nullable = false)
    private String adres;

    @Column(nullable = false)
    private LocalDate doğumTarihi;
}
