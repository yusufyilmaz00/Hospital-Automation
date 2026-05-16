package com.seproje.hospital.randevu;

import java.util.ArrayList;
import java.security.SecureRandom;

public class Reçete {
    private static final String KARAKTERLER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RNG = new SecureRandom();

    private ArrayList<String> ilaçlar;
    private String barkod;

    public Reçete() {
        this.ilaçlar = new ArrayList<>();
        this.barkod = barkodUret(10);
    }

    private static String barkodUret(int uzunluk) {
        StringBuilder sb = new StringBuilder(uzunluk);
        for (int i = 0; i < uzunluk; i++) {
            sb.append(KARAKTERLER.charAt(RNG.nextInt(KARAKTERLER.length())));
        }
        return sb.toString();
    }

    public ArrayList<String> getIlaçlar() {
        return ilaçlar;
    }

    public String getBarkod() {
        return barkod;
    }

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
