# Hospital Automation — Proje Rehberi

## Proje Yapısı

```
Hospital-Automation/
├── backend/        ← Spring Boot 4.0.6, Java 25, Maven
├── frontend/       ← (henüz başlanmadı)
└── documents/      ← UML diyagramları (.violet), proje ödevi PDF
```

## Backend Genel Bilgi

- **Framework:** Spring Boot 4.0.6 (Hibernate 7.x, Jackson 3.x → `tools.jackson.databind.ObjectMapper`)
- **DB:** PostgreSQL (Neon cloud) — bağlantı `.env` dosyasında
- **Auth:** Cookie tabanlı custom session auth (`SESSION_TOKEN` cookie, `SessionAuthFilter`)
- **ID üretimi:** `Personel` subclass'ları `GenerationType.SEQUENCE` kullanır (`personel_id_seq`)
- **Test:** H2 in-memory (PostgreSQL modu) + spring-security-test, test dosyaları `*IT.java`

## Önemli Teknik Notlar

### DB Şema Sorunları (Bilinen)
Uygulama eski bir JOINED inheritance şemasından `@MappedSuperclass`'a geçiş yaptı. Eski `personel` tablosuna referans veren FK'ler manuel olarak drop edildi:
- `kayit_gorevlisi` — drop edildi ✅
- `doktor`, `randevu_gorevlisi`, `veznedar`, `randevu` — drop edildi ✅

`hasta.email` unique constraint DB'ye manuel eklendi:
```sql
ALTER TABLE hasta ADD CONSTRAINT hasta_email_unique UNIQUE (email);
```

### Yeni Entity Kaydederken
`Personel` subclass'larında (`KayitGorevlisi`, `Doktor`, `RandevuGorevlisi`, `Veznedar`) `IletisimBilgisi`'ni **ayrıca kaydetme** — `CascadeType.ALL` halleder. Örnek:
```java
entity.setContactInformation(iletisimMapper.toEntity(dto.getIletisimBilgisi()));
repository.save(entity);  // cascade IletisimBilgisi'ni de kaydeder
```

### Test Komutları
```bash
# Testleri çalıştır (IT testleri dahil)
mvnw test

# Uygulamayı başlat
mvnw spring-boot:run
```

## Mevcut Endpointler

### Auth (`/api/auth`)
| Method | Path | Açıklama |
|--------|------|----------|
| POST | `/login` | Giriş yap (SESSION_TOKEN cookie set edilir) |
| POST | `/logout` | Çıkış yap |

### Hasta (`/api/hasta`)
| Method | Path | Yetki | Açıklama |
|--------|------|-------|----------|
| GET | `/self` | Hasta | Kendi bilgileri + randevular + hastalıklar |
| PUT | `/self/boy` | Hasta | Boy güncelle |
| PUT | `/self/kilo` | Hasta | Kilo güncelle |
| PUT | `/self/iletisim` | Hasta | İletişim bilgisi güncelle |

### Kayıt Görevlisi (`/api/kayit`)
| Method | Path | Yetki | Açıklama |
|--------|------|-------|----------|
| POST | `/register` | — | KG kaydı (admin endpoint) |
| POST | `/hasta` | KG | Hasta kaydet |
| GET | `/hastalar` | KG | Tüm hastaları listele |

### Doktor (`/api/doktor`)
| Method | Path | Yetki | Açıklama |
|--------|------|-------|----------|
| POST | `/register` | — | Doktor kaydı (admin endpoint) |
| GET | `/` | — | Tüm doktorları listele |
| GET | `/randevular` | Doktor | Kendi randevuları (hasta adı, tarih, ücret) |
| GET | `/randevu/{id}/hasta` | Doktor | Randevudaki hastanın tam kaydı (yalnızca kendi randevusu) |
| POST | `/randevu/{id}/tedavi` | Doktor | Randevuya tedavi ekle |
| DELETE | `/randevu/{id}/tedavi/{tedaviId}` | Doktor | Tedavi sil |
| POST | `/randevu/{id}/tedavi/{tedaviId}/recete` | Doktor | Tedaviye reçete ekle (barkod UUID ile üretilir) |
| DELETE | `/randevu/{id}/tedavi/{tedaviId}/recete/{receteId}` | Doktor | Reçete sil |
| POST | `/randevu/{id}/rapor` | Doktor | Randevuya rapor ekle |
| DELETE | `/randevu/{id}/rapor/{raporId}` | Doktor | Rapor sil |
| PUT | `/hasta/{hastaId}/boy` | Doktor | Hastanın boyunu güncelle |
| PUT | `/hasta/{hastaId}/kilo` | Doktor | Hastanın kilosunu güncelle |
| POST | `/hasta/{hastaId}/hastalik` | Doktor | Hastaya hastalık ekle |
| PUT | `/hasta/{hastaId}/hastalik/{hastalikId}` | Doktor | Hastalık güncelle |
| DELETE | `/hasta/{hastaId}/hastalik/{hastalikId}` | Doktor | Hastalık sil |

### Randevu Görevlisi (`/api/randevu-gorevlisi`)
| Method | Path | Yetki | Açıklama |
|--------|------|-------|----------|
| POST | `/register` | — | RG kaydı (admin endpoint) |
| GET | `/doktorlar` | RG | Doktor listesi |
| POST | `/randevu` | RG | Randevu oluştur |
| DELETE | `/randevu/{id}` | RG | Randevu iptal et |

## Use Case Listesi (s1.violet)

### Ana Use Case'ler
- [x] **Hasta Sisteme Kayıt Edilir** — KG, hasta kaydeder; hasta login olup kendi bilgilerini görür
- [x] **Rezervasyon Alınması** — RG, doktor seçip randevu oluşturur; hasta randevusunu görür
  - [ ] «extend» **Alternatif Tarih Önerilmesi** — Uygun doktor yoksa RG alternatif tarih önerir
- [ ] **Muayene** — Doktor, randevuya gelen hastanın bilgilerini görür ve muayene yapar
  - [x] «include» **Hasta Bilgilerinin Görüntülenmesi** — Doktor hastanın tüm kaydını görür
  - [x] «extend» **Hasta Kaydına Tedavi Ekleme** — Doktor tedaviyi hasta kaydına ekler
  - [x] «extend» **Rapor/Reçete Verilmesi** — Doktor rapor veya reçete yazar
  - [x] «extend» **Hasta Bilgilerinin Yönetimi** — Doktor hasta bilgilerini günceller
- [ ] **Ödeme İşleminin Yapılması** — Veznedar ücret hesaplar, ödeme alır
  - [ ] «include» **Sigorta Durumunun Sorgulanması** — Sosyal sigorta sunucusuna TCKN gönderilir
  - [ ] «extend» **İndirim Uygulanır** — Sigorta durumuna göre indirim uygulanır

### Aktörler
| Aktör | Rolleri |
|-------|---------|
| Kayıt Görevlisi | Hasta kayıt, hasta listesi |
| Randevü Görevlisi | Rezervasyon oluştur/iptal, doktor listesi |
| Doktor | Muayene, tedavi, reçete, hasta bilgisi görüntüle/güncelle |
| Hasta | Kendi bilgilerini görüntüle/güncelle, ödeme |
| Veznedar | Ödeme al, sigorta sorgula |

## Teknik Notlar — Manuel Test

### DTO Alan Adı Tutarsızlığı
`KayitGorevlisiRequestDTO` → alan adı `iletisimBilgisi`  
`PersonelCreateDTO` (RG, Veznedar) → alan adı `contactInformation`  
Swagger/Postman'da hangi endpoint'e hangi alan adını gönderdiğine dikkat et.

### TCKN Doğrulama Kuralı
`IletisimBilgisiDTO` üzerinde `@ValidTckn` aktif:
- 11 hane, rakam, 0 ile başlayamaz
- **Son hane = (ilk 10 hanenin toplamı) % 10**
- Geçerli örnek TCKNler: `10000000001`, `20000000002`, `30000000003`, `40000000004`

### Test Script Encoding
`backend/test_endpoints.ps1` UTF-8 BOM **ile** kaydedilmeli. BOM'suz kaydedilirse PowerShell 5.1 Türkçe karakterleri Windows-1252 olarak okur ve HTTP body bozulur → 500.

## Aktif Branch

`main` — Muayene use case implementasyonu devam ediyor.

