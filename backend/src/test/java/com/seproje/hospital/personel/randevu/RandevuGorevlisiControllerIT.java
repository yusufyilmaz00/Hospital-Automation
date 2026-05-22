package com.seproje.hospital.personel.randevu;

import tools.jackson.databind.ObjectMapper;
import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import com.seproje.hospital.hasta.Hasta;
import com.seproje.hospital.hasta.HastaRepository;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.personel.doktor.Doktor;
import com.seproje.hospital.personel.doktor.DoctorRepository;
import com.seproje.hospital.Hastane;
import com.seproje.hospital.randevu.dto.RandevuCreateRequestDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RandevuGorevlisiControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RandevuGorevlisiRepository randevuGorevlisiRepository;

    @Autowired
    private HastaRepository hastaRepository;

    @Autowired
    private DoctorRepository doktorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String RG_EMAIL = "rg@test.com";
    private static final String RG_SIFRE = "Test1234!";

    private static final String HASTA_EMAIL = "hasta@test.com";
    private static final String HASTA_SIFRE = "Hasta1234!";

    private Doktor doktor;
    private Hasta hasta;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Randevu görevlisi oluştur
        RandevuGorevlisi rg = new RandevuGorevlisi();
        rg.setEmail(RG_EMAIL);
        rg.setPassword(passwordEncoder.encode(RG_SIFRE));
        randevuGorevlisiRepository.save(rg);

        // Doktor oluştur (repository üzerinden, sequence ile)
        Doktor yeniDoktor = new Doktor();
        yeniDoktor.setEmail("doktor@test.com");
        yeniDoktor.setPassword(passwordEncoder.encode("Doktor1234!"));
        yeniDoktor.setDepartment(Hastane.Bolum.CARDIOLOGY);
        yeniDoktor.setUnvan(Doktor.Unvan.SPECIALIST);
        IletisimBilgisi doktorIletisim = IletisimBilgisi.builder()
                .isim("Ahmet").soyisim("Yılmaz").tckno("10000000146")
                .telefon("05551112233").adres("Doktor Cad. No:1")
                .doğumTarihi(LocalDate.of(1975, 3, 15))
                .build();
        yeniDoktor.setContactInformation(doktorIletisim);
        doktor = doktorRepository.save(yeniDoktor);

        // Hasta oluştur
        Hasta yeniHasta = new Hasta();
        yeniHasta.setEmail(HASTA_EMAIL);
        yeniHasta.setPassword(passwordEncoder.encode(HASTA_SIFRE));
        yeniHasta.setBoy(175.0);
        yeniHasta.setKilo(70.0);
        IletisimBilgisi hastaIletisim = IletisimBilgisi.builder()
                .isim("Fatma").soyisim("Kaya").tckno("20000000144")
                .telefon("05559998877").adres("Hasta Mah. No:2")
                .doğumTarihi(LocalDate.of(1990, 6, 20))
                .build();
        yeniHasta.setIletisimBilgisi(hastaIletisim);
        hasta = hastaRepository.save(yeniHasta);
    }

    // ─── Yardımcı metodlar ─────────────────────────────────────────────────

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

    private RandevuCreateRequestDTO gecerliRandevuDTO(LocalDateTime zaman) {
        return new RandevuCreateRequestDTO(hasta.getId(), doktor.getId(), zaman);
    }

    // ─── Testler ───────────────────────────────────────────────────────────

    @Test
    void rg_doktorListele_basarili() throws Exception {
        Cookie rgCookie = loginYap(RG_EMAIL, RG_SIFRE);

        var result = mockMvc.perform(get("/api/randevu-gorevlisi/doktorlar")
                        .cookie(rgCookie))
                .andExpect(status().isOk())
                .andReturn();

        com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO[] doktorlar =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO[].class
                );
        assertThat(doktorlar).hasSize(1);
    }

    @Test
    void rg_randevuOlustur_basarili() throws Exception {
        Cookie rgCookie = loginYap(RG_EMAIL, RG_SIFRE);
        LocalDateTime randevuZamani = LocalDateTime.now().plusDays(3).withNano(0);

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(rgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliRandevuDTO(randevuZamani))))
                .andExpect(status().isCreated());
    }

    @Test
    void rg_randevuOlusturdu_hasta_randevusunuGorebilir() throws Exception {
        // Randevu görevlisi randevu oluşturur
        Cookie rgCookie = loginYap(RG_EMAIL, RG_SIFRE);
        LocalDateTime randevuZamani = LocalDateTime.now().plusDays(5).withNano(0);

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(rgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliRandevuDTO(randevuZamani))))
                .andExpect(status().isCreated());

        // Hasta kendi randevusunu görür
        Cookie hastaCookie = loginYap(HASTA_EMAIL, HASTA_SIFRE);

        var selfResult = mockMvc.perform(get("/api/hasta/self")
                        .cookie(hastaCookie))
                .andExpect(status().isOk())
                .andReturn();

        HastaResponseDTO selfDTO = objectMapper.readValue(
                selfResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        assertThat(selfDTO.getRandevular()).hasSize(1);
        assertThat(selfDTO.getRandevular().get(0).getTarih()).isEqualTo(randevuZamani);
    }

    @Test
    void rg_randevuIptalEt_basarili() throws Exception {
        // Önce randevu oluştur
        Cookie rgCookie = loginYap(RG_EMAIL, RG_SIFRE);
        LocalDateTime randevuZamani = LocalDateTime.now().plusDays(7).withNano(0);

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(rgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliRandevuDTO(randevuZamani))))
                .andExpect(status().isCreated());

        // Hasta randevusunu görüp id'yi al
        Cookie hastaCookie = loginYap(HASTA_EMAIL, HASTA_SIFRE);

        var selfResult = mockMvc.perform(get("/api/hasta/self")
                        .cookie(hastaCookie))
                .andExpect(status().isOk())
                .andReturn();

        HastaResponseDTO selfDTO = objectMapper.readValue(
                selfResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        assertThat(selfDTO.getRandevular()).hasSize(1);
        Long randevuId = selfDTO.getRandevular().get(0).getId();

        // Randevu iptal et
        mockMvc.perform(delete("/api/randevu-gorevlisi/randevu/" + randevuId)
                        .cookie(rgCookie))
                .andExpect(status().isOk());

        // Hasta artık randevu görmemeli
        var afterResult = mockMvc.perform(get("/api/hasta/self")
                        .cookie(hastaCookie))
                .andExpect(status().isOk())
                .andReturn();

        HastaResponseDTO afterDTO = objectMapper.readValue(
                afterResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        assertThat(afterDTO.getRandevular()).isEmpty();
    }

    @Test
    void ayniZamana_cakisanRandevu_hataVermeli() throws Exception {
        Cookie rgCookie = loginYap(RG_EMAIL, RG_SIFRE);
        LocalDateTime randevuZamani = LocalDateTime.now().plusDays(2).withNano(0);
        RandevuCreateRequestDTO dto = gecerliRandevuDTO(randevuZamani);

        // İlk randevu başarılı
        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(rgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Aynı doktora, aynı saate ikinci randevu → hata
        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(rgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void kimlikSiz_doktorListele_403Doner() throws Exception {
        mockMvc.perform(get("/api/randevu-gorevlisi/doktorlar"))
                .andExpect(status().isForbidden());
    }

    @Test
    void kimlikSiz_randevuOlustur_403Doner() throws Exception {
        LocalDateTime randevuZamani = LocalDateTime.now().plusDays(1).withNano(0);

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliRandevuDTO(randevuZamani))))
                .andExpect(status().isForbidden());
    }

    @Test
    void hasta_randevuOlusturmaya_calisir_403Doner() throws Exception {
        Cookie hastaCookie = loginYap(HASTA_EMAIL, HASTA_SIFRE);
        LocalDateTime randevuZamani = LocalDateTime.now().plusDays(4).withNano(0);

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(hastaCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliRandevuDTO(randevuZamani))))
                .andExpect(status().isForbidden());
    }

    @Test
    void gecmisZaman_randevuOlustur_400Doner() throws Exception {
        Cookie rgCookie = loginYap(RG_EMAIL, RG_SIFRE);
        LocalDateTime gecmisZaman = LocalDateTime.now().minusDays(1);

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(rgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliRandevuDTO(gecmisZaman))))
                .andExpect(status().isBadRequest());
    }
}
