package com.seproje.hospital.personel.kayit;

import tools.jackson.databind.ObjectMapper;
import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KayitGorevlisiControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KayitGorevlisiRepository kayitGorevlisiRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String KG_EMAIL = "kayitgorevlisi@test.com";
    private static final String KG_SIFRE = "Test1234!";

    private static final String HASTA_EMAIL = "yenihasta@test.com";
    private static final String HASTA_SIFRE = "Hasta1234!";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        KayitGorevlisi kg = new KayitGorevlisi();
        kg.setEmail(KG_EMAIL);
        kg.setPassword(passwordEncoder.encode(KG_SIFRE));
        kayitGorevlisiRepository.save(kg);
    }

    // ─── Yardımcı metodlar ──────────────────────────────────────────────────

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

    private HastaRequestDTO gecerliHastaDTO() {
        IletisimBilgisiDTO iletisim = IletisimBilgisiDTO.builder()
                .isim("Test")
                .soyisim("Hasta")
                .tckno("10000000146")
                .telefon("05551234567")
                .adres("Test Mahallesi, Test Caddesi No:1")
                .doğumTarihi(LocalDate.of(1990, 1, 1))
                .build();

        return HastaRequestDTO.builder()
                .iletisimBilgisi(iletisim)
                .boy(175.0)
                .kilo(70.0)
                .email(HASTA_EMAIL)
                .password(HASTA_SIFRE)
                .build();
    }

    // ─── Testler ────────────────────────────────────────────────────────────

    @Test
    void kayitGorevlisi_hastaKayit_basarili() throws Exception {
        Cookie kgCookie = loginYap(KG_EMAIL, KG_SIFRE);

        var kayitResult = mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(kgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliHastaDTO())))
                .andExpect(status().isCreated())
                .andReturn();

        HastaResponseDTO responseDTO = objectMapper.readValue(
                kayitResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        assertThat(responseDTO.getId()).isNotNull();
    }

    @Test
    void kayitSonrasi_hastaGirisYapip_selfGetir() throws Exception {
        Cookie kgCookie = loginYap(KG_EMAIL, KG_SIFRE);

        var kayitResult = mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(kgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliHastaDTO())))
                .andExpect(status().isCreated())
                .andReturn();

        HastaResponseDTO kayitResponse = objectMapper.readValue(
                kayitResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        Cookie hastaCookie = loginYap(HASTA_EMAIL, HASTA_SIFRE);

        var selfResult = mockMvc.perform(get("/api/hasta/self").cookie(hastaCookie))
                .andExpect(status().isOk())
                .andReturn();

        HastaResponseDTO selfResponse = objectMapper.readValue(
                selfResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        assertThat(selfResponse.getId()).isEqualTo(kayitResponse.getId());
    }

    @Test
    void kimlikDogrulamaYok_hastaKayit_403Doner() throws Exception {
        mockMvc.perform(post("/api/kayit/hasta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliHastaDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    void hasta_hastaKayit_403Doner() throws Exception {
        // Önce kayıt görevlisi bir hasta kaydeder
        Cookie kgCookie = loginYap(KG_EMAIL, KG_SIFRE);
        mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(kgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliHastaDTO())))
                .andExpect(status().isCreated());

        // Hasta kendi hesabıyla giriş yapıp başka hasta kaydetmeye çalışır
        Cookie hastaCookie = loginYap(HASTA_EMAIL, HASTA_SIFRE);

        HastaRequestDTO ikinci = gecerliHastaDTO();
        ikinci.setEmail("ikinci@hasta.com");

        mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(hastaCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ikinci)))
                .andExpect(status().isForbidden());
    }

    @Test
    void gecersizVeri_hastaKayit_400Doner() throws Exception {
        Cookie kgCookie = loginYap(KG_EMAIL, KG_SIFRE);

        HastaRequestDTO gecersiz = HastaRequestDTO.builder()
                .email("gecersiz-email")
                .password("kisa")
                .build();

        mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(kgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecersiz)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ayniEmail_tekrarKayit_409Doner() throws Exception {
        Cookie kgCookie = loginYap(KG_EMAIL, KG_SIFRE);

        mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(kgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliHastaDTO())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(kgCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gecerliHastaDTO())))
                .andExpect(status().isConflict());
    }
}
