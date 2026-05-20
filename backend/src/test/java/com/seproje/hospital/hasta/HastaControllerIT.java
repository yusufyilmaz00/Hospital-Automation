package com.seproje.hospital.hasta;

import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.session.SessionConstants;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Rollback
class HastaControllerIT {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private HastaRepository hastaRepository;

    @Test
    void should_login_and_fetch_self() throws Exception {

        Hasta hasta = new Hasta();
        hasta.setEmail("test@hasta.com");
        hasta.setPassword("1234");

        Hasta hastaSql = hastaRepository.save(hasta);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@hasta.com");
        authRequest.setPassword("1234");

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
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