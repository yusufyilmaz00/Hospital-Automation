package com.seproje.hospital.personel;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.Hastane.Bolum;
import com.seproje.hospital.hasta.Hasta;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("DOKTOR")
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

        public String getDisplayName() {
            return displayName;
        }

        public double getHourlyRate() {
            return hourlyRate;
        }
    }

    @OneToMany(mappedBy = "doktor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Randevu> activeReservations = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "bolum")
    private Bolum department;

    @Enumerated(EnumType.STRING)
    @Column(name = "unvan")
    private Unvan unvan;

    protected Doktor() {
        super();
    }

    public Doktor(IletisimBilgisi contactInformation, String personelID, String username, String password,
            List<Randevu> activeReservations, Bolum department, Unvan unvan) {

        super(contactInformation, personelID, username, password);
        this.activeReservations = activeReservations != null ? activeReservations : new ArrayList<>();
        this.department = department;
        this.unvan = unvan;
    }

    public List<Randevu> getActiveReservations() {
        return activeReservations;
    }

    public Bolum getDepartment() {
        return department;
    }

    public Unvan getUnvan() {
        return unvan;
    }

    public void setActiveReservations(List<Randevu> activeReservations) {
        this.activeReservations = activeReservations;
    }

    public void setDepartment(Bolum department) {
        this.department = department;
    }

    public void setUnvan(Unvan unvan) {
        this.unvan = unvan;
    }

    public double calculateSalary() {
        double baseSalary = unvan.getHourlyRate() * 160; // Assuming 160 working hours per month
        double bonus = activeReservations.size() * 20; // Bonus for each active reservation
        return baseSalary + bonus;
    }

    public void addReservation(LocalDateTime randevuZamani, Hasta hasta) {
        if(checkAvailability(randevuZamani)) {
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
        if (activeReservations == null) {
            return true;
        }
        for (Randevu randevu : activeReservations) {
            if (randevu.getRandevuZamani().equals(desiredTime)) {
                return false;
            }
        }
        return true;
    }
}
