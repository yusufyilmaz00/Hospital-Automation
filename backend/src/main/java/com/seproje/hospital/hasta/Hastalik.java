package com.seproje.hospital.hasta;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@Table(name = "hastalik")
@RequiredArgsConstructor
public class Hastalik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String detay;

    @ManyToOne
    @JoinColumn(name = "hasta_id")
    private Hasta hasta;

    public Hastalik(String detay) {
        this.detay = detay;
    }
}