package com.seproje.hospital.personel;

import jakarta.persistence.*;

@Entity
@Table(name = "personel")
public class Doktor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected Doktor() {}

    public Doktor(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
