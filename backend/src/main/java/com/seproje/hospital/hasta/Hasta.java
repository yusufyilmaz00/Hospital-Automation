package com.seproje.hospital.hasta;

import jakarta.persistence.*;

@Entity
@Table(name = "hasta")
public class Hasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double boy;

    protected Hasta() {}

    public Hasta(Double boy) {
        this.boy = boy;
    }

    public Long getId() { return id; }

    public Double getBoy() { return boy; }

    public void setBoy(Double boy) { this.boy = boy; }
}
