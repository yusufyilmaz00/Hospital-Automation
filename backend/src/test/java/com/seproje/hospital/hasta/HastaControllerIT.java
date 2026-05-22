package com.seproje.hospital.hasta;

import tools.jackson.databind.ObjectMapper;
import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.common.IletisimBilgisi;
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
class HastaControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HastaRepository hastaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private IletisimBilgisi minimalIletisim(String tckno) {
        return IletisimBilgisi.builder()
                .isim("Test").soyisim("Kullanici").tckno(tckno)
                .telefon("05550000000").adres("Test Adres")
                .doğumTarihi(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void girisYapip_selfGetir() throws Exception {
        Hasta hasta = new Hasta();
        hasta.setEmail("test@hasta.com");
        hasta.setPassword(passwordEncoder.encode("Test1234!"));
        hasta.setBoy(170.0);
        hasta.setKilo(65.0);
        hasta.setIletisimBilgisi(minimalIletisim("11111111114"));
        hastaRepository.save(hasta);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@hasta.com");
        authRequest.setPassword("Test1234!");

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = loginResult.getResponse().getCookie(SessionConstants.COOKIE_NAME);
        assertThat(cookie).isNotNull();

        var selfResult = mockMvc.perform(get("/api/hasta/self")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();

        HastaResponseDTO dto = objectMapper.readValue(
                selfResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        assertThat(dto.getId()).isEqualTo(hastaRepository.findByEmail("test@hasta.com").get().getId());
    }

    @Test
    void cookieSiz_selfGetir_404Doner() throws Exception {
        mockMvc.perform(get("/api/hasta/self"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cikisYaptiktan_sonra_selfGetir_404Doner() throws Exception {
        Hasta hasta = new Hasta();
        hasta.setEmail("cikis@hasta.com");
        hasta.setPassword(passwordEncoder.encode("Test1234!"));
        hasta.setBoy(170.0);
        hasta.setKilo(65.0);
        hasta.setIletisimBilgisi(minimalIletisim("22222222226"));
        hastaRepository.save(hasta);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("cikis@hasta.com");
        authRequest.setPassword("Test1234!");

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = loginResult.getResponse().getCookie(SessionConstants.COOKIE_NAME);
        assertThat(cookie).isNotNull();

        mockMvc.perform(post("/api/auth/logout").cookie(cookie))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/hasta/self").cookie(cookie))
                .andExpect(status().isNotFound());
    }
}
