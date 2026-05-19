package com.seproje.hospital.personel;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Entity
@Table(name = "personel")
@RequiredArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Personel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String personelID;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iletisim_bilgisi_id", nullable = false)
    private IletisimBilgisi contactInformation;
    private String username;
    private String password;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Personel(IletisimBilgisi contactInformation, String personelID, String username, String password) {
        this.contactInformation = contactInformation;
        this.personelID = personelID;
        this.username = username;
        this.password = passwordEncoder.encode(password);
    }

    public void setContactInformation(IletisimBilgisi contactInformation) {
        this.contactInformation = contactInformation;
    }

    public void setPersonelID(String personelID) {
        this.personelID = personelID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password);
    }

    public IletisimBilgisi getContactInformation() {
        return contactInformation;
    }

    public String getPersonelID() {
        return personelID;
    }

    public boolean login(String username, String password) {
        return username.equals(this.username) && passwordEncoder.matches(password, this.password);
    }
}
