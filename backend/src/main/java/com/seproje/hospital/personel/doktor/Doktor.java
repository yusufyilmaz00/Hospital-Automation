package com.seproje.hospital.personel.doktor;

import java.util.List;
import java.util.ArrayList;

import com.seproje.hospital.auth.UserType;
import com.seproje.hospital.personel.personel.Personel;
import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.Hastane.Bolum;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Override
    public UserType getUserType() {
        return UserType.DOKTOR;
    }
}
