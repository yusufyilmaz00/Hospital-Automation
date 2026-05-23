package com.seproje.hospital.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hospitalOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hospital Automation API")
                        .version("1.0")
                        .description("""
                                Hastane akışı /api endpointleri ile yönetilir:
                                1. Personel register endpointleriyle kayıt görevlisi, randevu görevlisi, doktor ve veznedar oluşturulur.
                                2. /api/auth/login çağrısı session cookie döner.
                                3. Kayıt görevlisi /api/kayit/hasta ile hasta oluşturur.
                                4. Randevu görevlisi /api/randevu-gorevlisi/randevu ile randevu oluşturur; müsaitlik yoksa /api/randevu-gorevlisi/alternatif-tarihler kullanılır.
                                5. Doktor tedavi, reçete, rapor ve süre bilgilerini /api/doktor endpointleriyle girer.
                                6. Veznedar /api/veznedar/randevu/{randevuId}/odeme ile ücreti hesaplayıp ödemeyi alır.
                                Hata cevapları ApiErrorResponse formatındadır.
                                """))
                .servers(List.of(new Server().url("/").description("Current host")));
    }
}
