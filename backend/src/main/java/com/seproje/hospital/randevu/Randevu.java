package com.seproje.hospital.randevu;

import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.personel.Doktor;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Randevu{

    private ArrayList<Tedavi> tedaviler;
    private LocalDateTime randevuZamani;
    private Hasta hasta;
    private Doktor doktor;
    private Double ücret;

    public Randevu(LocalDateTime randevuZamani, Hasta hasta, Doktor doktor, Double ücret) {
        this.randevuZamani = randevuZamani;
        this.hasta = hasta;
        this.doktor = doktor;
        this.ücret = ücret;
        this.tedaviler = new ArrayList<>();
    }

    public ArrayList<Tedavi> getTedaviler() {
        return tedaviler;
    }

    public LocalDateTime getRandevuZamani() {
        return randevuZamani;
    }

    public void setRandevuZamani(LocalDateTime randevuZamani) {
        this.randevuZamani = randevuZamani;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public Doctor getDoktor() {
        return doktor;
    }

    public void setDoktor(Doctor doktor) {
        this.doktor = doktor;
    }

    public Double getÜcret() {
        return ücret;
    }

    public void setÜcret(Double ücret) {
        this.ücret = ücret;
    }
}