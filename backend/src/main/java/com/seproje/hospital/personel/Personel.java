package com.seproje.hospital.personel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.seproje.hospital.common.IletisimBilgisi;

import jakarta.persistence.*;

@Entity
@Table(name = "personel")
@Inheritance(strategy = InheritanceType.JOINED)
public class Personel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personel_id", unique = true)
    private String personelID;

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

    public Long getId() { return id; }

    public String getPersonelID() { return personelID; }

    public void setPersonelID(String personelID) { this.personelID = personelID; }

    public IletisimBilgisi getContactInformation() { return contactInformation; }

    public void setContactInformation(IletisimBilgisi contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) {
        this.password = passwordEncoder.encode(password);
    }

    public boolean login(String username, String password) {
        return username.equals(this.username) && passwordEncoder.matches(password, this.password);
    }
}
