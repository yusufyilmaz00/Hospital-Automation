package com.seproje.hospital.randevu;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private List<Reçete> reçeteler = new ArrayList<>();

    protected Tedavi() {}

    public Tedavi(TedaviTipi tedaviTipi, String açıklama, Randevu randevu) {
        this.tedaviTipi = tedaviTipi;
        this.açıklama = açıklama;
        this.randevu = randevu;
    }

    public Long getId() { return id; }

    public TedaviTipi getTedaviTipi() { return tedaviTipi; }

    public void setTedaviTipi(TedaviTipi tedaviTipi) { this.tedaviTipi = tedaviTipi; }

    public String getAçıklama() { return açıklama; }

    public void setAçıklama(String açıklama) { this.açıklama = açıklama; }

    public Randevu getRandevu() { return randevu; }

    public List<Reçete> getReçeteler() { return reçeteler; }
}
