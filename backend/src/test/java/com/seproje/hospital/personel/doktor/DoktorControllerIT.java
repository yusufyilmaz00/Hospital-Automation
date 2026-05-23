package com.seproje.hospital.personel.doktor;

import com.seproje.hospital.Hastane;
import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.HastaRepository;
import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.randevu.RandevuRepository;
import com.seproje.hospital.randevu.dto.RandevuSureGuncelleRequestDTO;
import com.seproje.hospital.session.SessionConstants;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DoktorControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doktorRepository;

    @Autowired
    private HastaRepository hastaRepository;

    @Autowired
    private RandevuRepository randevuRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Doktor doktor;
    private Doktor baskaDoktor;
    private Randevu randevu;

    private static final String DOKTOR_EMAIL = "doktor-controller@test.com";
    private static final String DOKTOR_SIFRE = "Doktor1234!";
    private static final String BASKA_DOKTOR_EMAIL = "baska-doktor@test.com";
    private static final String BASKA_DOKTOR_SIFRE = "Baska1234!";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        doktor = doktorRepository.save(doktorOlustur(DOKTOR_EMAIL, DOKTOR_SIFRE, "10000000146"));
        baskaDoktor = doktorRepository.save(doktorOlustur(BASKA_DOKTOR_EMAIL, BASKA_DOKTOR_SIFRE, "10000000154"));

        Hasta hasta = new Hasta();
        hasta.setEmail("hasta-doktor-controller@test.com");
        hasta.setPassword(passwordEncoder.encode("Hasta1234!"));
        hasta.setBoy(170.0);
        hasta.setKilo(65.0);
        hasta.setIletisimBilgisi(IletisimBilgisi.builder()
                .isim("Ayşe").soyisim("Kaya").tckno("20000000144")
                .telefon("05559998877").adres("Hasta Mah. No:2")
                .doğumTarihi(LocalDate.of(1990, 6, 20))
                .build());
        hasta = hastaRepository.save(hasta);

        randevu = randevuRepository.save(Randevu.builder()
                .randevuZamani(LocalDateTime.now().plusDays(1).withNano(0))
                .hasta(hasta)
                .doktor(doktor)
                .build());
    }

    private Doktor doktorOlustur(String email, String sifre, String tckno) {
        Doktor yeniDoktor = new Doktor();
        yeniDoktor.setEmail(email);
        yeniDoktor.setPassword(passwordEncoder.encode(sifre));
        yeniDoktor.setDepartment(Hastane.Bolum.CARDIOLOGY);
        yeniDoktor.setUnvan(Doktor.Unvan.SPECIALIST);
        yeniDoktor.setContactInformation(IletisimBilgisi.builder()
                .isim("Doktor").soyisim("Test").tckno(tckno)
                .telefon("05551112233").adres("Doktor Cad. No:1")
                .doğumTarihi(LocalDate.of(1975, 3, 15))
                .build());
        return yeniDoktor;
    }

    private Cookie loginYap(String email, String sifre) throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(email);
        authRequest.setPassword(sifre);

        var result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie(SessionConstants.COOKIE_NAME);
        assertThat(cookie).isNotNull();
        return cookie;
    }

    @Test
    void doktor_kendiRandevusununSuresiniGunceller() throws Exception {
        Cookie doktorCookie = loginYap(DOKTOR_EMAIL, DOKTOR_SIFRE);
        RandevuSureGuncelleRequestDTO request = new RandevuSureGuncelleRequestDTO(75);

        var result = mockMvc.perform(put("/api/doktor/randevu/" + randevu.getId() + "/sure")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        DoktorRandevuDTO dto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                DoktorRandevuDTO.class
        );

        assertThat(dto.getSureDakika()).isEqualTo(75);
        assertThat(randevuRepository.findById(randevu.getId()).orElseThrow().getSureDakika()).isEqualTo(75);
    }

    @Test
    void doktor_baskaDoktorunRandevusununSuresiniGuncelleyemez() throws Exception {
        Cookie baskaDoktorCookie = loginYap(BASKA_DOKTOR_EMAIL, BASKA_DOKTOR_SIFRE);
        RandevuSureGuncelleRequestDTO request = new RandevuSureGuncelleRequestDTO(90);

        mockMvc.perform(put("/api/doktor/randevu/" + randevu.getId() + "/sure")
                        .cookie(baskaDoktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        assertThat(randevuRepository.findById(randevu.getId()).orElseThrow().getSureDakika()).isEqualTo(30);
    }

    @Test
    void gecersizSure_400Doner() throws Exception {
        Cookie doktorCookie = loginYap(DOKTOR_EMAIL, DOKTOR_SIFRE);
        RandevuSureGuncelleRequestDTO request = new RandevuSureGuncelleRequestDTO(0);

        mockMvc.perform(put("/api/doktor/randevu/" + randevu.getId() + "/sure")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void odemesiAlinmisRandevununSuresiGuncellenemez() throws Exception {
        randevu.setOdendi(true);
        randevuRepository.save(randevu);

        Cookie doktorCookie = loginYap(DOKTOR_EMAIL, DOKTOR_SIFRE);
        RandevuSureGuncelleRequestDTO request = new RandevuSureGuncelleRequestDTO(90);

        mockMvc.perform(put("/api/doktor/randevu/" + randevu.getId() + "/sure")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(randevuRepository.findById(randevu.getId()).orElseThrow().getSureDakika()).isEqualTo(30);
    }
}
