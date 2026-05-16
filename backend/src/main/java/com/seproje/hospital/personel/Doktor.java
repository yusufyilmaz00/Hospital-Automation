package com.seproje.hospital.personel;

public class Doktor {
    private Long id;
    private String name;

    public Doktor(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}