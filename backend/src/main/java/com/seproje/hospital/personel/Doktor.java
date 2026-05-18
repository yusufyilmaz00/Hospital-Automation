package com.seproje.hospital.personel;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.klinik.BolumTipi;
import com.seproje.hospital.klinik.Klinik;
import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.hasta.Hasta;

import jakarta.persistence.*;

@Entity
@Table(name = "doktor")
public class Doktor extends Personel {

    public enum Unvan {
        INTERN("Stajyer", 50.0),
        RESIDENT("Pratisyen", 75.0),
        SPECIALIST("Uzman", 100.0),
        ASSOCIATE_PROFESSOR("Doçent", 150.0),
        PROFESSOR("Profesör", 200.0);

        private final String displayName;
        private final double hourlyRate;

        Unvan(String displayName, double hourlyRate) {
            this.displayName = displayName;
            this.hourlyRate = hourlyRate;
        }

        public String getDisplayName() { return displayName; }

        public double getHourlyRate() { return hourlyRate; }
    }

    @OneToMany(mappedBy = "doktor", cascade = CascadeType.ALL)
    private List<Randevu> activeReservations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "bolum")
    private BolumTipi department;

    @Enumerated(EnumType.STRING)
    @Column(name = "unvan")
    private Unvan unvan;

    @ManyToOne
    @JoinColumn(name = "klinik_id")
    private Klinik klinik;

    protected Doktor() {}

    public Doktor(IletisimBilgisi contactInformation, String personelID, String username, String password,
            BolumTipi department, Unvan unvan) {
        super(contactInformation, personelID, username, password);
        this.department = department;
        this.unvan = unvan;
    }

    public List<Randevu> getActiveReservations() { return activeReservations; }

    public BolumTipi getDepartment() { return department; }

    public Unvan getUnvan() { return unvan; }

    public Klinik getKlinik() { return klinik; }

    public void setDepartment(BolumTipi department) { this.department = department; }

    public void setUnvan(Unvan unvan) { this.unvan = unvan; }

    public void setKlinik(Klinik klinik) { this.klinik = klinik; }

    public double calculateSalary() {
        double baseSalary = unvan.getHourlyRate() * 160;
        double bonus = activeReservations.size() * 20;
        return baseSalary + bonus;
    }

    public void addReservation(LocalDateTime randevuZamani, Hasta hasta) {
        if (checkAvailability(randevuZamani)) {
            Randevu newRandevu = new Randevu(randevuZamani, hasta, this, calculateSalary());
            activeReservations.add(newRandevu);
        } else {
            throw new IllegalArgumentException("The doctor is not available at the desired time.");
        }
    }

    public void removeReservation(Randevu randevu) {
        activeReservations.remove(randevu);
    }

    public boolean checkAvailability(LocalDateTime desiredTime) {
        for (Randevu randevu : activeReservations) {
            if (randevu.getRandevuZamani().equals(desiredTime)) {
                return false;
            }
        }
        return true;
    }
}
