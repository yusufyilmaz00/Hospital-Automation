package com.seproje.hospital.hasta;

import com.seproje.hospital.auth.AuthUser;
import com.seproje.hospital.auth.UserType;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.randevu.Randevu;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hasta")
public class Hasta implements AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iletisim_bilgisi_id", nullable = false)
    private IletisimBilgisi iletisimBilgisi;

    @Builder.Default
    @OneToMany(mappedBy = "hasta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Randevu> randevular = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "hasta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hastalik> hastaliklar = new ArrayList<>();

    @Column(nullable = false)
    private Double boy;

    @Column(nullable = false)
    private Double kilo;

    @Override
    public UserType getUserType() {
        return UserType.HASTA;
    }
}
