package com.seproje.hospital.workflow;

import com.seproje.hospital.Hastane;
import com.seproje.hospital.auth.dto.AuthRequest;
import com.seproje.hospital.common.dto.IletisimBilgisiDTO;
import com.seproje.hospital.hasta.dto.HastaRequestDTO;
import com.seproje.hospital.hasta.dto.HastaResponseDTO;
import com.seproje.hospital.hasta.dto.HastaTedaviDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorCreateDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorRandevuDTO;
import com.seproje.hospital.personel.doktor.dto.DoktorResponseDTO;
import com.seproje.hospital.personel.kayit.dto.KayitGorevlisiRequestDTO;
import com.seproje.hospital.personel.randevu.dto.AlternatifTarihDTO;
import com.seproje.hospital.personel.randevu.dto.RandevuGorevlisiCreateDTO;
import com.seproje.hospital.personel.vezne.dto.VeznedarCreateDTO;
import com.seproje.hospital.personel.personel.dto.PersonelCreateDTO;
import com.seproje.hospital.randevu.Randevu;
import com.seproje.hospital.randevu.RandevuRepository;
import com.seproje.hospital.randevu.TedaviTipi;
import com.seproje.hospital.randevu.dto.RandevuCreateRequestDTO;
import com.seproje.hospital.randevu.dto.RandevuCreateResponseDTO;
import com.seproje.hospital.randevu.dto.RandevuOdemeDTO;
import com.seproje.hospital.randevu.dto.RandevuSureGuncelleRequestDTO;
import com.seproje.hospital.randevu.dto.ReceteDTO;
import com.seproje.hospital.randevu.dto.ReceteRequestDTO;
import com.seproje.hospital.randevu.dto.TedaviRequestDTO;
import com.seproje.hospital.session.SessionConstants;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HospitalWorkflowIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RandevuRepository randevuRepository;

    private DoktorResponseDTO doktor;

    private static final String KAYIT_EMAIL = "workflow-kayit@test.com";
    private static final String RANDEVU_EMAIL = "workflow-randevu@test.com";
    private static final String DOKTOR_EMAIL = "workflow-doktor@test.com";
    private static final String VEZNE_EMAIL = "workflow-vezne@test.com";
    private static final String HASTA_EMAIL = "workflow-hasta@test.com";
    private static final String SIFRE = "Test1234!";

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        kayitGorevlisiKaydet();
        randevuGorevlisiKaydet();
        doktor = doktorKaydet();
        veznedarKaydet();
    }

    @Test
    void normalHastaAkisi_kayittanOdemeyeKadarBasarili() throws Exception {
        Cookie kayitCookie = loginYap(KAYIT_EMAIL, SIFRE);
        HastaResponseDTO hasta = hastaKaydet(kayitCookie);

        Cookie randevuCookie = loginYap(RANDEVU_EMAIL, SIFRE);
        LocalDateTime randevuZamani = gelecekRandevuZamani(3, 10, 0);
        RandevuCreateResponseDTO randevu = randevuOlustur(randevuCookie, hasta.getId(), doktor.getId(), randevuZamani);

        assertThat(randevu.getId()).isNotNull();
        assertThat(randevu.getHastaId()).isEqualTo(hasta.getId());
        assertThat(randevu.getDoktorId()).isEqualTo(doktor.getId());
        assertThat(randevu.getRandevuZamani()).isEqualTo(randevuZamani);
        assertThat(randevu.getSureDakika()).isEqualTo(30);

        Cookie hastaCookie = loginYap(HASTA_EMAIL, SIFRE);
        HastaResponseDTO hastaSelf = hastaSelfGetir(hastaCookie);
        assertThat(hastaSelf.getRandevular()).hasSize(1);
        assertThat(hastaSelf.getRandevular().get(0).getId()).isEqualTo(randevu.getId());

        Cookie doktorCookie = loginYap(DOKTOR_EMAIL, SIFRE);
        DoktorRandevuDTO doktorRandevu = randevuSuresiGuncelle(doktorCookie, randevu.getId(), 60);
        assertThat(doktorRandevu.getSureDakika()).isEqualTo(60);

        HastaResponseDTO doktorunGorduguHasta = doktorHastaGor(doktorCookie, randevu.getId());
        assertThat(doktorunGorduguHasta.getId()).isEqualTo(hasta.getId());

        HastaTedaviDTO ilacTedavisi = tedaviEkle(
                doktorCookie,
                randevu.getId(),
                new TedaviRequestDTO(TedaviTipi.İLAÇ_TEDAVİSİ, "İlaç tedavisi uygulandı")
        );
        assertThat(ilacTedavisi.getReceteler()).isEmpty();

        HastaTedaviDTO fizikTedavi = tedaviEkle(
                doktorCookie,
                randevu.getId(),
                new TedaviRequestDTO(TedaviTipi.FİZİK_TEDAVİ, "Fizik tedavi uygulandı")
        );

        ReceteDTO doluRecete = receteEkle(
                doktorCookie,
                randevu.getId(),
                fizikTedavi.getId(),
                new ReceteRequestDTO(List.of("Parasetamol", "Kas gevşetici"))
        );
        assertThat(doluRecete.getId()).isNotNull();
        assertThat(doluRecete.getIlaclar()).containsExactly("Parasetamol", "Kas gevşetici");

        ReceteDTO bosRecete = receteEkle(
                doktorCookie,
                randevu.getId(),
                ilacTedavisi.getId(),
                new ReceteRequestDTO(List.of())
        );
        assertThat(bosRecete.getId()).isNotNull();
        assertThat(bosRecete.getIlaclar()).isEmpty();

        HastaResponseDTO tedaviSonrasiHasta = hastaSelfGetir(hastaCookie);
        assertThat(tedaviSonrasiHasta.getRandevular().get(0).getTedaviler()).hasSize(2);

        Cookie vezneCookie = loginYap(VEZNE_EMAIL, SIFRE);
        RandevuOdemeDTO odeme = odemeAl(vezneCookie, randevu.getId());

        assertThat(odeme.getBrutUcret()).isEqualTo(2350.0);
        assertThat(odeme.getSigortaIndirimOrani()).isEqualTo(0.20);
        assertThat(odeme.getOdenecekUcret()).isEqualTo(1880.0);
        assertThat(odeme.getOdendi()).isTrue();

        Randevu odemesiAlinmisRandevu = randevuRepository.findById(randevu.getId()).orElseThrow();
        assertThat(odemesiAlinmisRandevu.getOdendi()).isTrue();
        assertThat(odemesiAlinmisRandevu.getÜcret()).isEqualTo(1880.0);
        assertThat(hastaSelfGetir(hastaCookie).getRandevular().get(0).getTedaviler()).hasSize(2);

        mockMvc.perform(post("/api/doktor/randevu/" + randevu.getId() + "/tedavi")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TedaviRequestDTO(TedaviTipi.SERVİS_YATIŞ, "Ödeme sonrası ekleme")
                        )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void istenenTarihteDoktorMusaitDegilse_alternatifTarihOnerilir() throws Exception {
        Cookie kayitCookie = loginYap(KAYIT_EMAIL, SIFRE);
        HastaResponseDTO ilkHasta = hastaKaydet(kayitCookie);

        Cookie randevuCookie = loginYap(RANDEVU_EMAIL, SIFRE);
        LocalDateTime istenenZaman = gelecekRandevuZamani(4, 11, 0);
        randevuOlustur(randevuCookie, ilkHasta.getId(), doktor.getId(), istenenZaman);

        HastaResponseDTO ikinciHasta = hastaKaydet(
                kayitCookie,
                "workflow-ikinci-hasta@test.com",
                "30000000148",
                "İkinci"
        );

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(randevuCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RandevuCreateRequestDTO(ikinciHasta.getId(), doktor.getId(), istenenZaman)
                        )))
                .andExpect(status().isBadRequest());

        var result = mockMvc.perform(get("/api/randevu-gorevlisi/alternatif-tarihler")
                        .cookie(randevuCookie)
                        .param("haftaBaslangic", istenenZaman.toLocalDate().toString()))
                .andExpect(status().isOk())
                .andReturn();

        AlternatifTarihDTO[] alternatifler = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AlternatifTarihDTO[].class
        );

        assertThat(alternatifler).hasSize(1);
        assertThat(alternatifler[0].getDoktor().getId()).isEqualTo(doktor.getId());
        assertThat(alternatifler[0].getTarihler()).doesNotContain(istenenZaman);
        assertThat(alternatifler[0].getTarihler())
                .anySatisfy(tarih -> assertThat(tarih.toLocalDate()).isEqualTo(istenenZaman.toLocalDate()));
    }

    @Test
    void workflowHatalari_apiUzerindenKontrolluResponseDoner() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest("yok@test.com", SIFRE))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/kayit/hasta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hastaRequest(
                                "yetkisiz-hasta@test.com",
                                "40000000150",
                                "Yetkisiz"
                        ))))
                .andExpect(status().isForbidden());

        Cookie kayitCookie = loginYap(KAYIT_EMAIL, SIFRE);
        HastaResponseDTO hasta = hastaKaydet(kayitCookie);

        Cookie randevuCookie = loginYap(RANDEVU_EMAIL, SIFRE);
        LocalDateTime randevuZamani = gelecekRandevuZamani(5, 9, 0);
        RandevuCreateResponseDTO randevu = randevuOlustur(randevuCookie, hasta.getId(), doktor.getId(), randevuZamani);

        mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(randevuCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RandevuCreateRequestDTO(hasta.getId(), doktor.getId(), randevuZamani)
                        )))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/randevu-gorevlisi/alternatif-tarihler")
                        .cookie(randevuCookie)
                        .param("haftaBaslangic", "gecersiz-tarih"))
                .andExpect(status().isBadRequest());

        Cookie doktorCookie = loginYap(DOKTOR_EMAIL, SIFRE);

        mockMvc.perform(put("/api/doktor/randevu/" + randevu.getId() + "/sure")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RandevuSureGuncelleRequestDTO(0))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/doktor/randevu/" + randevu.getId() + "/tedavi")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/doktor/randevu/" + randevu.getId() + "/tedavi/999999/recete")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReceteRequestDTO(List.of()))))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/veznedar/randevu/" + randevu.getId() + "/odeme"))
                .andExpect(status().isForbidden());

        Cookie vezneCookie = loginYap(VEZNE_EMAIL, SIFRE);
        odemeAl(vezneCookie, randevu.getId());

        mockMvc.perform(post("/api/veznedar/randevu/" + randevu.getId() + "/odeme")
                        .cookie(vezneCookie))
                .andExpect(status().isBadRequest());
    }

    private Cookie loginYap(String email, String sifre) throws Exception {
        var result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest(email, sifre))))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie(SessionConstants.COOKIE_NAME);
        assertThat(cookie).isNotNull();
        return cookie;
    }

    private AuthRequest authRequest(String email, String sifre) {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(email);
        authRequest.setPassword(sifre);
        return authRequest;
    }

    private void kayitGorevlisiKaydet() throws Exception {
        KayitGorevlisiRequestDTO request = KayitGorevlisiRequestDTO.builder()
                .email(KAYIT_EMAIL)
                .password(SIFRE)
                .iletisimBilgisi(iletisimDto("Kayıt", "Workflow", "10000000146"))
                .build();

        mockMvc.perform(post("/api/kayit/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void randevuGorevlisiKaydet() throws Exception {
        RandevuGorevlisiCreateDTO request = new RandevuGorevlisiCreateDTO();
        personelBilgileriDoldur(request, RANDEVU_EMAIL, "10000000214", "Randevu");

        mockMvc.perform(post("/api/randevu-gorevlisi/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private DoktorResponseDTO doktorKaydet() throws Exception {
        DoktorCreateDTO request = new DoktorCreateDTO();
        request.setEmail(DOKTOR_EMAIL);
        request.setPassword(SIFRE);
        request.setIletisimBilgisi(iletisimDto("Doktor", "Workflow", "10000000382"));
        request.setBolum(Hastane.Bolum.CARDIOLOGY);
        request.setUnvan(com.seproje.hospital.personel.doktor.Doktor.Unvan.SPECIALIST);

        var result = mockMvc.perform(post("/api/doktor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), DoktorResponseDTO.class);
    }

    private void veznedarKaydet() throws Exception {
        VeznedarCreateDTO request = new VeznedarCreateDTO();
        personelBilgileriDoldur(request, VEZNE_EMAIL, "10000000450", "Vezne");

        mockMvc.perform(post("/api/veznedar/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void personelBilgileriDoldur(PersonelCreateDTO dto, String email, String tckno, String isim) {
        dto.setEmail(email);
        dto.setPassword(SIFRE);
        dto.setContactInformation(iletisimDto(isim, "Workflow", tckno));
    }

    private HastaResponseDTO hastaKaydet(Cookie kayitCookie) throws Exception {
        return hastaKaydet(kayitCookie, HASTA_EMAIL, "20000000158", "Ayşe");
    }

    private HastaResponseDTO hastaKaydet(Cookie kayitCookie, String email, String tckno, String isim) throws Exception {
        HastaRequestDTO request = hastaRequest(email, tckno, isim);

        var result = mockMvc.perform(post("/api/kayit/hasta")
                        .cookie(kayitCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), HastaResponseDTO.class);
    }

    private HastaRequestDTO hastaRequest(String email, String tckno, String isim) {
        return HastaRequestDTO.builder()
                .iletisimBilgisi(IletisimBilgisiDTO.builder()
                        .isim(isim)
                        .soyisim("Workflow")
                        .tckno(tckno)
                        .telefon("05559998877")
                        .adres("Hasta Mah. No:2")
                        .doğumTarihi(LocalDate.of(1990, 6, 20))
                        .build())
                .boy(170.0)
                .kilo(65.0)
                .email(email)
                .password(SIFRE)
                .build();
    }

    private RandevuCreateResponseDTO randevuOlustur(
            Cookie randevuCookie,
            Long hastaId,
            Long doktorId,
            LocalDateTime randevuZamani
    ) throws Exception {
        var result = mockMvc.perform(post("/api/randevu-gorevlisi/randevu")
                        .cookie(randevuCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RandevuCreateRequestDTO(hastaId, doktorId, randevuZamani)
                        )))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), RandevuCreateResponseDTO.class);
    }

    private HastaResponseDTO hastaSelfGetir(Cookie hastaCookie) throws Exception {
        var result = mockMvc.perform(get("/api/hasta/self")
                        .cookie(hastaCookie))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), HastaResponseDTO.class);
    }

    private DoktorRandevuDTO randevuSuresiGuncelle(Cookie doktorCookie, Long randevuId, Integer sureDakika) throws Exception {
        var result = mockMvc.perform(put("/api/doktor/randevu/" + randevuId + "/sure")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RandevuSureGuncelleRequestDTO(sureDakika))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), DoktorRandevuDTO.class);
    }

    private HastaResponseDTO doktorHastaGor(Cookie doktorCookie, Long randevuId) throws Exception {
        var result = mockMvc.perform(get("/api/doktor/randevu/" + randevuId + "/hasta")
                        .cookie(doktorCookie))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), HastaResponseDTO.class);
    }

    private HastaTedaviDTO tedaviEkle(Cookie doktorCookie, Long randevuId, TedaviRequestDTO request) throws Exception {
        var result = mockMvc.perform(post("/api/doktor/randevu/" + randevuId + "/tedavi")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), HastaTedaviDTO.class);
    }

    private ReceteDTO receteEkle(Cookie doktorCookie, Long randevuId, Long tedaviId, ReceteRequestDTO request) throws Exception {
        var result = mockMvc.perform(post("/api/doktor/randevu/" + randevuId + "/tedavi/" + tedaviId + "/recete")
                        .cookie(doktorCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ReceteDTO.class);
    }

    private RandevuOdemeDTO odemeAl(Cookie vezneCookie, Long randevuId) throws Exception {
        var result = mockMvc.perform(post("/api/veznedar/randevu/" + randevuId + "/odeme")
                        .cookie(vezneCookie))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), RandevuOdemeDTO.class);
    }

    private IletisimBilgisiDTO iletisimDto(String isim, String soyisim, String tckno) {
        return IletisimBilgisiDTO.builder()
                .isim(isim)
                .soyisim(soyisim)
                .tckno(tckno)
                .telefon("05551112233")
                .adres("Personel Cad. No:1")
                .doğumTarihi(LocalDate.of(1975, 3, 15))
                .build();
    }

    private LocalDateTime gelecekRandevuZamani(int gunSonra, int saat, int dakika) {
        return LocalDate.now()
                .plusDays(gunSonra)
                .atTime(LocalTime.of(saat, dakika));
    }
}
