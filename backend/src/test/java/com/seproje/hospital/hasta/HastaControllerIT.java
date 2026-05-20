package com.seproje.hospital.hasta;

import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.common.IletisimBilgisi;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.session.SessionConstants;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class HastaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HastaRepository hastaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void should_login_and_fetch_self() throws Exception {

        IletisimBilgisi iletisim = IletisimBilgisi.builder()
                .isim("Test")
                .soyisim("Hasta")
                .tckno("12345678901")
                .telefon("5551234567")
                .adres("Test Adres")
                .doğumTarihi(LocalDate.of(1990, 1, 1))
                .build();

        Hasta hasta = Hasta.builder()
                .email("test@hasta.com")
                .password(passwordEncoder.encode("1234"))
                .boy(175.0)
                .kilo(70.0)
                .iletisimBilgisi(iletisim)
                .build();

        Hasta hastaSql = hastaRepository.save(hasta);

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .param("email", "test@hasta.com")
                        .param("password", "1234"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = loginResult.getResponse()
                .getCookie(SessionConstants.COOKIE_NAME);

        assertThat(cookie).isNotNull();

        var selfResult = mockMvc.perform(get("/api/hasta/self")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        HastaResponseDTO dto = objectMapper.readValue(
                selfResult.getResponse().getContentAsString(),
                HastaResponseDTO.class
        );

        assertThat(dto.getId()).isEqualTo(hastaSql.getId());
    }
}