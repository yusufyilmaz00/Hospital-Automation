package com.seproje.hospital.randevu;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.personel.Doktor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "randevu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tedavi> tedaviler = new ArrayList<>();

    protected Randevu() {}

    public Randevu(LocalDateTime randevuZamani, Hasta hasta, Doktor doktor, Double ücret) {
        this.randevuZamani = randevuZamani;
        this.hasta = hasta;
        this.doktor = doktor;
        this.ücret = ücret;
    }

    public Long getId() { return id; }

    public LocalDateTime getRandevuZamani() { return randevuZamani; }

    public void setRandevuZamani(LocalDateTime randevuZamani) { this.randevuZamani = randevuZamani; }

    public Hasta getHasta() { return hasta; }

    public void setHasta(Hasta hasta) { this.hasta = hasta; }

    public Doktor getDoktor() { return doktor; }

    public void setDoktor(Doktor doktor) { this.doktor = doktor; }

    public Double getÜcret() { return ücret; }

    public void setÜcret(Double ücret) { this.ücret = ücret; }

    public List<Tedavi> getTedaviler() { return tedaviler; }
}
