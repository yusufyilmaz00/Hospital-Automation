package com.seproje.hospital.personel.vezne;

import com.seproje.hospital.Hastane;
import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.HastaRepository;
import com.seproje.hospital.personel.doktor.DoctorRepository;
import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.randevu.RandevuRepository;
import com.seproje.hospital.randevu.Tedavi;
import com.seproje.hospital.randevu.TedaviTipi;
import com.seproje.hospital.randevu.dto.RandevuOdemeDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VeznedarControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VeznedarRepository veznedarRepository;

    @Autowired
    private DoctorRepository doktorRepository;

    @Autowired
    private HastaRepository hastaRepository;

    @Autowired
    private RandevuRepository randevuRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String VEZNEDAR_EMAIL = "veznedar@test.com";
    private static final String VEZNEDAR_SIFRE = "Vezne1234!";
    private static final String HASTA_EMAIL = "hasta-vezne@test.com";
    private static final String HASTA_SIFRE = "Hasta1234!";

    private Hasta hasta;
    private Doktor doktor;
    private Randevu randevu;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Veznedar veznedar = new Veznedar();
        veznedar.setEmail(VEZNEDAR_EMAIL);
        veznedar.setPassword(passwordEncoder.encode(VEZNEDAR_SIFRE));
        veznedarRepository.save(veznedar);

        doktor = new Doktor();
        doktor.setEmail("doktor-vezne@test.com");
        doktor.setPassword(passwordEncoder.encode("Doktor1234!"));
        doktor.setDepartment(Hastane.Bolum.CARDIOLOGY);
        doktor.setUnvan(Doktor.Unvan.SPECIALIST);
        doktor.setContactInformation(IletisimBilgisi.builder()
                .isim("Ahmet").soyisim("Yılmaz").tckno("10000000146")
                .telefon("05551112233").adres("Doktor Cad. No:1")
                .doğumTarihi(LocalDate.of(1975, 3, 15))
                .build());
        doktor = doktorRepository.save(doktor);

        hasta = new Hasta();
        hasta.setEmail(HASTA_EMAIL);
        hasta.setPassword(passwordEncoder.encode(HASTA_SIFRE));
        hasta.setBoy(170.0);
        hasta.setKilo(65.0);
        hasta.setIletisimBilgisi(IletisimBilgisi.builder()
                .isim("Ayşe").soyisim("Kaya").tckno("20000000144")
                .telefon("05559998877").adres("Hasta Mah. No:2")
                .doğumTarihi(LocalDate.of(1990, 6, 20))
                .build());
        hasta = hastaRepository.save(hasta);

        randevu = Randevu.builder()
                .randevuZamani(LocalDateTime.now().plusDays(1).withNano(0))
                .hasta(hasta)
                .doktor(doktor)
                .sureDakika(60)
                .build();
        randevu.getTedaviler().add(Tedavi.builder()
                .tedaviTipi(TedaviTipi.İLAÇ_TEDAVİSİ)
                .açıklama("İlaç tedavisi")
                .randevu(randevu)
                .build());
        randevu.getTedaviler().add(Tedavi.builder()
                .tedaviTipi(TedaviTipi.FİZİK_TEDAVİ)
                .açıklama("Fizik tedavi")
                .randevu(randevu)
                .build());
        randevu = randevuRepository.save(randevu);
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
    void veznedar_randevuUcretiniHesaplarVeOdemeAlir() throws Exception {
        Cookie veznedarCookie = loginYap(VEZNEDAR_EMAIL, VEZNEDAR_SIFRE);

        var result = mockMvc.perform(post("/api/veznedar/randevu/" + randevu.getId() + "/odeme")
                        .cookie(veznedarCookie))
                .andExpect(status().isOk())
                .andReturn();

        RandevuOdemeDTO dto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                RandevuOdemeDTO.class
        );

        assertThat(dto.getBrutUcret()).isEqualTo(2350.0);
        assertThat(dto.getSigortaIndirimOrani()).isEqualTo(0.20);
        assertThat(dto.getOdenecekUcret()).isEqualTo(1880.0);
        assertThat(dto.getOdendi()).isTrue();

        Randevu updated = randevuRepository.findById(randevu.getId()).orElseThrow();
        assertThat(updated.getÜcret()).isEqualTo(1880.0);
        assertThat(updated.getSigortaIndirimOrani()).isEqualTo(0.20);
        assertThat(updated.getOdendi()).isTrue();
    }

    @Test
    void hasta_veznedarOdemesiAlamaz_403Doner() throws Exception {
        Cookie hastaCookie = loginYap(HASTA_EMAIL, HASTA_SIFRE);

        mockMvc.perform(post("/api/veznedar/randevu/" + randevu.getId() + "/odeme")
                        .cookie(hastaCookie))
                .andExpect(status().isForbidden());
    }

    @Test
    void ayniRandevuIcinIkinciOdeme_400Doner() throws Exception {
        Cookie veznedarCookie = loginYap(VEZNEDAR_EMAIL, VEZNEDAR_SIFRE);

        mockMvc.perform(post("/api/veznedar/randevu/" + randevu.getId() + "/odeme")
                        .cookie(veznedarCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/veznedar/randevu/" + randevu.getId() + "/odeme")
                        .cookie(veznedarCookie))
                .andExpect(status().isBadRequest());
    }
}
