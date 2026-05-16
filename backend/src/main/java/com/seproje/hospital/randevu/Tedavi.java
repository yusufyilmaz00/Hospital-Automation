package com.seproje.hospital.randevu;

import java.util.ArrayList;

public class Tedavi{
    private TedaviTipi tedaviTipi;
    private String açıklama;
    private ArrayList<Reçete> reçeteler;

    public Tedavi(TedaviTipi tedaviTipi, String açıklama) {
        this.tedaviTipi = tedaviTipi;
        this.açıklama = açıklama;
        this.reçeteler = new ArrayList<>();
    }

    public TedaviTipi getTedaviTipi() {
        return tedaviTipi;
    }

    public void setTedaviTipi(TedaviTipi tedaviTipi) {
        this.tedaviTipi = tedaviTipi;
    }

    public String getAçıklama() {
        return açıklama;
    }

    public void setAçıklama(String açıklama) {
        this.açıklama = açıklama;
    }

    public ArrayList<Reçete> getReçeteler() {
        return reçeteler;
    }

    public void reçeteEkle() {
        reçeteler.add(new Reçete());
    }

    public void ilaçEkle(String barkod, String ilaç) {
        for (Reçete reçete : reçeteler) {
            if (reçete.getBarkod().equals(barkod)) {
                reçete.getIlaçlar().add(ilaç);
                return;
            }
        }
        throw new IllegalArgumentException("Barkoda ait reçete bulunamadı: " + barkod);
    }

    public void reçeteSil(String barkod) {
        for (Reçete reçete : reçeteler) {
            if (reçete.getBarkod().equals(barkod)) {
                reçeteler.remove(reçete);
                return;
            }
        }
        throw new IllegalArgumentException("Barkoda ait reçete bulunamadı: " + barkod);
    }
}
