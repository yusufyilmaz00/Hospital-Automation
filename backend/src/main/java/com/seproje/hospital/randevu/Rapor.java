package com.seproje.hospital.randevu;

import jakarta.persistence.*;

@Entity
@Table(name = "rapor")
public class Rapor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String icerik;

    @ManyToOne
    @JoinColumn(name = "randevu_id", nullable = false)
    private Randevu randevu;

    protected Rapor() {}

    public Rapor(String icerik, Randevu randevu) {
        this.icerik = icerik;
        this.randevu = randevu;
    }

    public Long getId() { return id; }

    public String getIcerik() { return icerik; }

    public Randevu getRandevu() { return randevu; }
}
