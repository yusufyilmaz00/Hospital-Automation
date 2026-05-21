package com.seproje.hospital.hasta;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
public class Hastalik {
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String detay;

    @ManyToOne
    @JoinColumn(name = "hasta_id")
    private Hasta hasta;

    public Hastalik(String detay) {
        this.detay = detay;
    }
}