package com.seproje.hospital.personel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Entity
@Table(name = "personel")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "personel_tipi", discriminatorType = DiscriminatorType.STRING)
public class Personel {
    @Id
    @Column(name = "personel_id")
    private String personelID;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iletisim_bilgisi_id")
    private IletisimBilgisi contactInformation;

    private String username;
    private String password;
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    protected Personel() {}

    public Personel(IletisimBilgisi contactInformation, String personelID, String username, String password) {
        this.contactInformation = contactInformation;
        this.personelID = personelID;
        this.username = username;
        this.password = passwordEncoder.encode(password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
