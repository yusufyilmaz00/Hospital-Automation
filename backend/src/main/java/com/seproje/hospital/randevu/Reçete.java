package com.seproje.hospital.randevu;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recete")
public class Reçete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String barkod;

    @ElementCollection
    @CollectionTable(name = "recete_ilac", joinColumns = @JoinColumn(name = "recete_id"))
    @Column(name = "ilac")
    private List<String> ilaçlar = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tedavi_id", nullable = false)
    private Tedavi tedavi;

    protected Reçete() {}

    public Reçete(String barkod, Tedavi tedavi) {
        this.barkod = barkod;
        this.tedavi = tedavi;
    }

    public Long getId() { return id; }

    public String getBarkod() { return barkod; }

    public List<String> getIlaçlar() { return ilaçlar; }

    public Tedavi getTedavi() { return tedavi; }

    public void ilaçEkle(String ilaç) {
        if (ilaçlar.contains(ilaç)) {
            throw new IllegalArgumentException("İlaç zaten reçetede mevcut: " + ilaç);
        }
        ilaçlar.add(ilaç);
    }

    public void ilaçSil(String ilaç) {
        if (!ilaçlar.contains(ilaç)) {
            throw new IllegalArgumentException("İlaç reçetede bulunamadı: " + ilaç);
        }
        ilaçlar.remove(ilaç);
    }
}
