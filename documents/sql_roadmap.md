# Hastane Otomasyonu — PostgreSQL Tablo Yol Haritası

Sınıf diyagramı (`c1.violet`) temel alınarak tasarlanmıştır.

---

## İçindekiler

1. [Geliştirme Rehberi](#geliştirme-rehberi)
2. [ENUM Tipleri](#2-enum-tipleri)
3. [iletisim_bilgisi](#3-iletisim_bilgisi)
4. [hastane](#4-hastane)
5. [klinik](#5-klinik)
6. [personel](#6-personel)
7. [hasta](#7-hasta)
8. [hasta_hastalik](#8-hasta_hastalik)
9. [randevu](#9-randevu)
10. [tedavi](#10-tedavi)
11. [recete](#11-recete)
12. [recete_ilac](#12-recete_ilac)
13. [sigorta_sunucu](#13-sigorta_sunucu)
14. [sigorta_sunucu_iletisim](#14-sigorta_sunucu_iletisim)
15. [İlişki Diyagramı](#ilişki-diyagramı)
16. [Uygulama Sırası](#uygulama-sırası)

---

## Geliştirme Rehberi

Bu bölüm, diğer geliştiricilerin projeye dahil olurken framework'ün nasıl çalıştığını ve tabloların nasıl oluşturulduğunu anlaması için yazılmıştır.

---

### Teknoloji Yığını

| Katman | Teknoloji | Görev |
|---|---|---|
| Web API | Spring Boot (REST) | HTTP isteklerini karşılar |
| ORM | Hibernate (JPA) | Java ↔ SQL dönüşümü |
| Veritabanı | PostgreSQL | Veriyi kalıcı olarak saklar |
| Dönüşüm | Jackson | JSON ↔ Java nesnesi |

---

### SQL Tablolarını Kim Oluşturur?

**Elle SQL yazmak gerekmez.** Bu dosyadaki SQL komutları yalnızca tasarım referansıdır.

Uygulamayı çalıştırdığında Hibernate, `@Entity` annotasyonu taşıyan her Java sınıfını tarar ve PostgreSQL'de karşılık gelen tabloyu otomatik oluşturur. Bu davranış `application.properties` içindeki şu satırla kontrol edilir:

```properties
spring.jpa.hibernate.ddl-auto=update
```

| Değer | Davranış | Kullanım |
|---|---|---|
| `create` | Her başlangıçta tabloları sil, yeniden oluştur | Yalnızca test |
| `update` | Eksik tablo/sütun varsa ekle, var olanlara dokunma | **Geliştirme (mevcut ayar)** |
| `validate` | Tablo yapısını kontrol et, hiçbir şey değiştirme | Production |
| `none` | Hiçbir şey yapma | Production (Flyway/Liquibase ile) |

---

### Bir Entity Nasıl Yazılır?

Her veritabanı tablosunun karşılığı bir `@Entity` sınıfıdır. Minimum yapı:

```java
@Entity
@Table(name = "hasta")      // tablo adını açıkça belirt
public class Hasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // BIGSERIAL karşılığı
    private Long id;

    private Double boy;      // sütun adı alan adıyla aynı olur: "boy"
    private Double kilo;

    @ManyToOne
    @JoinColumn(name = "hastane_id")   // FK sütun adı
    private Hastane hastane;
}
```

Hibernate bu sınıftan şunu üretir:

```sql
CREATE TABLE hasta (
    id         BIGSERIAL PRIMARY KEY,
    boy        DOUBLE PRECISION,
    kilo       DOUBLE PRECISION,
    hastane_id BIGINT REFERENCES hastane(id)
);
```

---

### Personel Hiyerarşisi — Single Table Inheritance

Sınıf diyagramında `Personel` bir üst sınıf; `Doktor`, `KayıtGörevlisi`, `RandevuGörevlisi` ve `Veznedar` ondan türemektedir.

Bu hiyerarşi için **Single Table Inheritance** stratejisi seçilmiştir. Bunun anlamı: dört ayrı tablo yerine tek bir `personel` tablosu oluşturulur ve her satırın hangi role ait olduğu `personel_tipi` sütunuyla ayrışır.

```
personel tablosu:

| id | personel_tipi   | username | password | klinik_id |
|----|-----------------|----------|----------|-----------|
| 1  | DOKTOR          | drali    | ****     | 3         |
| 2  | VEZNEDAR        | mehmet   | ****     | NULL      |
| 3  | KAYIT_GOREVLISI | ayse     | ****     | NULL      |
```

`klinik_id` sütunu yalnızca `DOKTOR` satırlarında dolu olur; diğerleri için `NULL` kalır.

**Java tarafında nasıl yazılır:**

```java
// Üst sınıf — tek tablo bu sınıftan üretilir
@Entity
@Table(name = "personel")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "personel_tipi", discriminatorType = DiscriminatorType.STRING)
public abstract class Personel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String personelIdKodu;
    private String username;
    private String password;

    @ManyToOne
    @JoinColumn(name = "iletisim_bilgisi_id")
    private IletisimBilgisi iletisimBilgisi;

    @ManyToOne
    @JoinColumn(name = "hastane_id")
    private Hastane hastane;
}
```

```java
// Doktor alt sınıfı — discriminator değeri "DOKTOR" olan satırlar bu sınıfa map'lenir
@Entity
@DiscriminatorValue("DOKTOR")
public class Doktor extends Personel {

    @ManyToOne
    @JoinColumn(name = "klinik_id")   // sadece DOKTOR satırlarında dolu
    private Klinik klinik;
}
```

```java
@Entity
@DiscriminatorValue("VEZNEDAR")
public class Veznedar extends Personel {
    // Doktora özgü alan yok, Personel alanları yeterli
}
```

```java
@Entity
@DiscriminatorValue("KAYIT_GOREVLISI")
public class KayitGorevlisi extends Personel { }

@Entity
@DiscriminatorValue("RANDEVU_GOREVLISI")
public class RandevuGorevlisi extends Personel { }
```

Hibernate bu dört sınıftan **tek bir** `personel` tablosu üretir.

> **Neden Single Table?** Joined Table stratejisinde her sorgu JOIN gerektirir. Single Table'da tüm roller tek sorguda okunur. Bu projenin ölçeğinde daha sade ve yeterlidir.

---

### İlişkiler için JPA Annotasyonları

| İlişki | Annotasyon | Örnek |
|---|---|---|
| Bir tabloya FK (N→1) | `@ManyToOne` + `@JoinColumn` | `Randevu → Hasta` |
| FK'li listeyi oku (1→N) | `@OneToMany(mappedBy=...)` | `Hasta.randevular` |
| String listesi | `@ElementCollection` | `Hasta.hastaliklar` |
| Ara tablo (N→N) | `@ManyToMany` + `@JoinTable` | `SigortaSunucu ↔ IletisimBilgisi` |

---

### Repository ile Veri Okuma/Yazma

Tablo hazır olduktan sonra SQL yazmak gerekmez. Spring Data JPA arayüzü yeterlidir:

```java
public interface HastaRepository extends JpaRepository<Hasta, Long> {
    // Otomatik gelen metodlar:
    // hastaRepository.findAll()          → SELECT * FROM hasta
    // hastaRepository.findById(1L)       → SELECT * FROM hasta WHERE id=1
    // hastaRepository.save(hasta)        → INSERT / UPDATE
    // hastaRepository.deleteById(1L)     → DELETE FROM hasta WHERE id=1

    // Özel sorgu:
    List<Hasta> findByHastaneId(Long hastaneId);
    // → SELECT * FROM hasta WHERE hastane_id=?
}
```

---

### Geliştirme Akışı (Özet)

```
1. Sınıf diyagramına bak (c1.violet)
         ↓
2. Bu dosyadaki tablo tasarımını referans al (sql_roadmap.md)
         ↓
3. Java @Entity sınıfını yaz (annotasyonlarla)
         ↓
4. Uygulamayı çalıştır → Hibernate tabloyu otomatik oluşturur
         ↓
5. @Repository arayüzünü yaz → SQL yazmadan veri oku/yaz
         ↓
6. @Service içinde iş mantığını kodla
         ↓
7. @RestController ile API endpoint'i aç
```

---

## 2. ENUM Tipleri

PostgreSQL'de enum tipler tablo yaratılmadan önce tanımlanmalıdır.

### `tedavi_tipi`
`Tedavi` sınıfındaki `tedaviTipi: ENUM` alanına karşılık gelir.

```sql
CREATE TYPE tedavi_tipi AS ENUM (
    'AMELIYAT',
    'ILAC_TEDAVISI',
    'FIZIK_TEDAVI',
    'SERVIS_YATIS'
);
```

| Değer | Açıklama |
|---|---|
| `AMELIYAT` | Cerrahi müdahale |
| `ILAC_TEDAVISI` | İlaç ile tedavi |
| `FIZIK_TEDAVI` | Fizik tedavi ve rehabilitasyon |
| `SERVIS_YATIS` | Servise yatış ile tedavi |

---

### `personel_tipi`
`Personel` kalıtım hiyerarşisi için Single Table Inheritance discriminator'ı.

```sql
CREATE TYPE personel_tipi AS ENUM (
    'DOKTOR',
    'KAYIT_GOREVLISI',
    'RANDEVU_GOREVLISI',
    'VEZNEDAR'
);
```

| Değer | Diyagramdaki Sınıf |
|---|---|
| `DOKTOR` | `Doktor` |
| `KAYIT_GOREVLISI` | `Kayıt Görevlisi` |
| `RANDEVU_GOREVLISI` | `Randevü Görevlisi` |
| `VEZNEDAR` | `Veznedar` |

---

## 2. `iletisim_bilgisi`

**Diyagram sınıfı:** `İletişimBilgisi`

`Hasta`, `Personel` ve `SigortaSunucu` tarafından ortak kullanılan iletişim ve kimlik bilgilerini tutar.

```sql
CREATE TABLE iletisim_bilgisi (
    id              BIGSERIAL    PRIMARY KEY,
    isim            VARCHAR(100) NOT NULL,
    soyisim         VARCHAR(100) NOT NULL,
    tc_kimlik       CHAR(11)     NOT NULL UNIQUE,
    telefon         VARCHAR(20),
    dogum_tarihi    DATE,
    adres           TEXT
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Otomatik artan birincil anahtar |
| `isim` | VARCHAR(100) | Kişinin adı |
| `soyisim` | VARCHAR(100) | Kişinin soyadı |
| `tc_kimlik` | CHAR(11) | TC kimlik numarası, benzersiz olmalı |
| `telefon` | VARCHAR(20) | Telefon numarası |
| `dogum_tarihi` | DATE | Doğum tarihi |
| `adres` | TEXT | Açık adres |

---

## 3. `hastane`

**Diyagram sınıfı:** `Hastane`

Sistemdeki tüm varlıkların (personel, hasta, klinik) bağlı olduğu üst kapsayıcı.

```sql
CREATE TABLE hastane (
    id      BIGSERIAL    PRIMARY KEY,
    isim    VARCHAR(200) NOT NULL
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `isim` | VARCHAR(200) | Hastanenin adı |

---

## 4. `klinik`

**Diyagram sınıfı:** `Klinik`

Hastaneye bağlı klinik/bölüm bilgisi. Her klinik bir hastaneye ait olup birden fazla doktor barındırır.

```sql
CREATE TABLE klinik (
    id          BIGSERIAL    PRIMARY KEY,
    bolum_adi   VARCHAR(100) NOT NULL,
    hastane_id  BIGINT       NOT NULL REFERENCES hastane(id) ON DELETE CASCADE
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `bolum_adi` | VARCHAR(100) | Bölüm adı (örn. Kardiyoloji, Ortopedi) |
| `hastane_id` | BIGINT (FK) | Ait olduğu hastane |

**İlişki:** `hastane` ← `klinik` (1 hastane → 1..* klinik)

---

## 5. `personel`

**Diyagram sınıfları:** `Personel`, `Doktor`, `Kayıt Görevlisi`, `Randevü Görevlisi`, `Veznedar`

Dört alt sınıf **Single Table Inheritance** stratejisiyle tek tabloda tutulur. `personel_tipi` sütunu hangi rol olduğunu belirtir. Doktora özgü `klinik_id` sütunu doktor olmayan personel için `NULL` olur.

```sql
CREATE TABLE personel (
    id                   BIGSERIAL     PRIMARY KEY,
    personel_tipi        personel_tipi NOT NULL,
    personel_id_kodu     VARCHAR(50)   NOT NULL UNIQUE,
    username             VARCHAR(100)  NOT NULL UNIQUE,
    password             VARCHAR(255)  NOT NULL,
    iletisim_bilgisi_id  BIGINT        REFERENCES iletisim_bilgisi(id),
    hastane_id           BIGINT        REFERENCES hastane(id),
    -- Yalnızca DOKTOR satırları için (diğerleri NULL)
    klinik_id            BIGINT        REFERENCES klinik(id)
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `personel_tipi` | ENUM | Rol: DOKTOR, KAYIT_GOREVLISI, vb. |
| `personel_id_kodu` | VARCHAR(50) | Personel sicil numarası |
| `username` | VARCHAR(100) | Sisteme giriş kullanıcı adı |
| `password` | VARCHAR(255) | Şifrelenmiş parola (bcrypt vb.) |
| `iletisim_bilgisi_id` | BIGINT (FK) | İletişim bilgisi |
| `hastane_id` | BIGINT (FK) | Çalıştığı hastane |
| `klinik_id` | BIGINT (FK) | Çalıştığı klinik — **sadece DOKTOR** |

**İlişki:**
- `hastane` ← `personel` (1 hastane → 0..* personel)
- `klinik` ← `personel[DOKTOR]` (1 klinik → 1..* doktor)

> **Not:** Alternatif olarak Joined Table Inheritance kullanılabilir; `doktor`, `kayit_gorevlisi` gibi alt tablolar açılır. Daha temiz şema ama daha fazla JOIN gerektirir. Proje büyüdükçe bu strateji değerlendirilebilir.

---

## 6. `hasta`

**Diyagram sınıfı:** `Hasta`

Hastanede kayıtlı hastaların temel sağlık bilgileri.

```sql
CREATE TABLE hasta (
    id                   BIGSERIAL        PRIMARY KEY,
    boy                  DOUBLE PRECISION,
    kilo                 DOUBLE PRECISION,
    iletisim_bilgisi_id  BIGINT           REFERENCES iletisim_bilgisi(id),
    hastane_id           BIGINT           REFERENCES hastane(id)
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `boy` | DOUBLE PRECISION | Boy (cm) |
| `kilo` | DOUBLE PRECISION | Kilo (kg) |
| `iletisim_bilgisi_id` | BIGINT (FK) | İletişim ve kimlik bilgisi |
| `hastane_id` | BIGINT (FK) | Kayıtlı olduğu hastane |

**İlişki:** `hastane` ← `hasta` (1 hastane → 0..* hasta)

---

## 7. `hasta_hastalik`

**Diyagram:** `Hasta` sınıfındaki `hastalıklar[]: String` alanı

Bir hastanın bilinen hastalık geçmişi. Çoka çok yerine tek hasta için sıralı liste.

```sql
CREATE TABLE hasta_hastalik (
    id          BIGSERIAL    PRIMARY KEY,
    hasta_id    BIGINT       NOT NULL REFERENCES hasta(id) ON DELETE CASCADE,
    hastalik    VARCHAR(200) NOT NULL
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `hasta_id` | BIGINT (FK) | İlgili hasta |
| `hastalik` | VARCHAR(200) | Hastalık adı / tanısı |

---

## 8. `randevu`

**Diyagram sınıfı:** `Randevu`

Hasta ile doktor arasındaki randevu kaydı. Ücret, zaman ve tarafları içerir.

```sql
CREATE TABLE randevu (
    id              BIGSERIAL        PRIMARY KEY,
    randevu_zamani  TIMESTAMP        NOT NULL,
    hasta_id        BIGINT           NOT NULL REFERENCES hasta(id),
    doktor_id       BIGINT           NOT NULL REFERENCES personel(id),
    ucret           DOUBLE PRECISION
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `randevu_zamani` | TIMESTAMP | Randevu tarihi ve saati |
| `hasta_id` | BIGINT (FK) | Randevuya ait hasta |
| `doktor_id` | BIGINT (FK) | Randevuya ait doktor (personel_tipi='DOKTOR') |
| `ucret` | DOUBLE PRECISION | Randevu ücreti |

**İlişki:**
- `hasta` ← `randevu` (1 hasta → 0..* randevu)
- `personel[DOKTOR]` ← `randevu` (1 doktor → 0..* randevu)

---

## 9. `tedavi`

**Diyagram sınıfı:** `Tedavi`

Randevu sırasında uygulanan tedavi kaydı. Bir randevuya birden fazla tedavi bağlanabilir.

```sql
CREATE TABLE tedavi (
    id           BIGSERIAL    PRIMARY KEY,
    randevu_id   BIGINT       NOT NULL REFERENCES randevu(id) ON DELETE CASCADE,
    tedavi_tipi  tedavi_tipi  NOT NULL,
    aciklama     TEXT
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `randevu_id` | BIGINT (FK) | Ait olduğu randevu |
| `tedavi_tipi` | ENUM | Tedavi türü |
| `aciklama` | TEXT | Tedavi açıklaması / notları |

**İlişki:** `randevu` ← `tedavi` (1 randevu → 0..* tedavi)

---

## 10. `recete`

**Diyagram sınıfı:** `Reçete`

Tedaviye bağlı reçete. Barkod otomatik üretilir (Java tarafında `SecureRandom`).

```sql
CREATE TABLE recete (
    id          BIGSERIAL   PRIMARY KEY,
    tedavi_id   BIGINT      NOT NULL REFERENCES tedavi(id) ON DELETE CASCADE,
    barkod      VARCHAR(50) NOT NULL UNIQUE
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `tedavi_id` | BIGINT (FK) | Ait olduğu tedavi |
| `barkod` | VARCHAR(50) | Benzersiz reçete barkodu |

**İlişki:** `tedavi` ← `recete` (1 tedavi → 0..* reçete)

---

## 11. `recete_ilac`

**Diyagram:** `Reçete` sınıfındaki `ilaçlar[]: String` alanı

Reçeteye yazılan ilaçların listesi.

```sql
CREATE TABLE recete_ilac (
    id          BIGSERIAL    PRIMARY KEY,
    recete_id   BIGINT       NOT NULL REFERENCES recete(id) ON DELETE CASCADE,
    ilac        VARCHAR(200) NOT NULL
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `recete_id` | BIGINT (FK) | Ait olduğu reçete |
| `ilac` | VARCHAR(200) | İlaç adı |

---

## 12. `sigorta_sunucu`

**Diyagram sınıfı:** `SigortaSunucu`

Hastaların sigorta bilgilerini sağlayan harici sunucu kaydı.

```sql
CREATE TABLE sigorta_sunucu (
    id      BIGSERIAL    PRIMARY KEY,
    isim    VARCHAR(200) NOT NULL
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `id` | BIGSERIAL | Birincil anahtar |
| `isim` | VARCHAR(200) | Sigorta sunucu / şirket adı |

---

## 13. `sigorta_sunucu_iletisim`

**Diyagram:** `SigortaSunucu` sınıfındaki `iletişimler[]: İletişimBilgisi` alanı

Sigorta sunucusunun birden fazla iletişim kaydına bağlanmasını sağlayan ara tablo.

```sql
CREATE TABLE sigorta_sunucu_iletisim (
    sigorta_sunucu_id   BIGINT NOT NULL REFERENCES sigorta_sunucu(id) ON DELETE CASCADE,
    iletisim_bilgisi_id BIGINT NOT NULL REFERENCES iletisim_bilgisi(id),
    PRIMARY KEY (sigorta_sunucu_id, iletisim_bilgisi_id)
);
```

| Sütun | Tip | Açıklama |
|---|---|---|
| `sigorta_sunucu_id` | BIGINT (FK) | Sigorta sunucusu |
| `iletisim_bilgisi_id` | BIGINT (FK) | İletişim kaydı |

---

## İlişki Diyagramı

```
hastane (1)
  ├──(1..*)── klinik
  │               └──(1..*)── personel[DOKTOR]
  ├──(0..*)── personel (tüm roller)
  │               └── iletisim_bilgisi
  └──(0..*)── hasta
                  ├── iletisim_bilgisi
                  ├──(0..*)── hasta_hastalik
                  └──(0..*)── randevu
                                  ├── personel[DOKTOR]
                                  └──(0..*)── tedavi
                                                  └──(0..*)── recete
                                                                  └──(0..*)── recete_ilac

sigorta_sunucu
  └──(0..*)── sigorta_sunucu_iletisim ──► iletisim_bilgisi
```

---

## Uygulama Sırası

Yabancı anahtar kısıtları nedeniyle tablolar aşağıdaki sırayla oluşturulmalıdır:

```sql
-- 1. Bağımsız tablolar önce
CREATE TYPE tedavi_tipi ...;
CREATE TYPE personel_tipi ...;
CREATE TABLE iletisim_bilgisi ...;
CREATE TABLE hastane ...;

-- 2. hastane'ye bağlı
CREATE TABLE klinik ...;

-- 3. personel (klinik ve iletisim_bilgisi'ne bağlı)
CREATE TABLE personel ...;

-- 4. hasta (iletisim_bilgisi ve hastane'ye bağlı)
CREATE TABLE hasta ...;
CREATE TABLE hasta_hastalik ...;

-- 5. randevu zinciri
CREATE TABLE randevu ...;
CREATE TABLE tedavi ...;
CREATE TABLE recete ...;
CREATE TABLE recete_ilac ...;

-- 6. sigorta sunucu
CREATE TABLE sigorta_sunucu ...;
CREATE TABLE sigorta_sunucu_iletisim ...;
```

---

## Backend Notları

- ~~`Doctor.java` (`doctor` paketi) eski bir sınıf — silinmeli~~  → **Tamamlandı**, `personel/Doktor.java` aktif sınıf
- ~~`Hasta.java` sadece `boy` alanını içeriyor~~  → **Kısmen tamamlandı**, `@Entity` eklendi; `kilo` ve `iletisim_bilgisi_id` geliştiriciler tarafından eklenecek
- ~~`Randevu.java` içinde `getHasta()` yanlışlıkla `String` döndürüyor~~  → **Tamamlandı**, dönüş tipi `Hasta` olarak düzeltildi
- `personel` hiyerarşisi için `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` ve `@DiscriminatorColumn` henüz eklenmedi — geliştiriciler ekleyecek
- `application.properties` içinde `spring.jpa.show-sql=true` ve `logging.level.org.hibernate.SQL=DEBUG` ayarları şu an açık — production öncesi kapatılmalı

---

## Eksikler ve Dikkat Edilmesi Gerekenler

### 1. `bolum_tipi` ENUM Tanımlanmamış

Sınıf diyagramında hem `Doktor` hem `Klinik` sınıfı `bölüm: ENUM` alanı içeriyor. Bu ENUM'un hangi değerleri taşıyacağı (örn. `KARDIYOLOJI`, `ORTOPEDI`, `NOROLOJI` …) henüz belirlenmemiş.

Şu anki tasarımda `klinik.bolum_adi` sütunu bu bilgiyi `VARCHAR` olarak saklıyor. Bölüm listesi sabit ve kapalı bir küme olacaksa ayrı bir `bolum_tipi` ENUM tipi veya `bolum` lookup tablosu eklenmelidir.

**Yapılacak:** Proje ekibi bölüm listesini belirleyip aşağıdaki iki yoldan birini seçmeli:

```sql
-- Seçenek A: PostgreSQL ENUM (liste sabitse)
CREATE TYPE bolum_tipi AS ENUM ('KARDIYOLOJI', 'ORTOPEDI', 'NOROLOJI', ...);
ALTER TABLE klinik ALTER COLUMN bolum_adi TYPE bolum_tipi USING bolum_adi::bolum_tipi;

-- Seçenek B: Ayrı lookup tablosu (liste büyüyebilirse)
CREATE TABLE bolum (
    id    SERIAL       PRIMARY KEY,
    isim  VARCHAR(100) NOT NULL UNIQUE
);
ALTER TABLE klinik ADD COLUMN bolum_id INT REFERENCES bolum(id);
```

---

### 2. Hibernate ENUM Sütunları VARCHAR Olarak Oluşturur

Bu roadmap'teki `CREATE TYPE ... AS ENUM` tanımları **tasarım referansıdır**. Hibernate, `@Enumerated(EnumType.STRING)` ve `@DiscriminatorColumn` annotasyonlarıyla PostgreSQL ENUM tipi değil **VARCHAR** sütunu oluşturur.

Yani tablolar Hibernate tarafından oluşturulduğunda:

| Sütun | Roadmap'teki tip | Hibernate'in oluşturduğu tip |
|---|---|---|
| `tedavi.tedavi_tipi` | `tedavi_tipi` (PG ENUM) | `VARCHAR(255)` |
| `personel.personel_tipi` | `personel_tipi` (PG ENUM) | `VARCHAR(31)` |

Bu fonksiyonel olarak sorun çıkarmaz. Ancak veritabanı tarafında geçersiz değer koruması istiyorsan PostgreSQL ENUM'unu elle oluşturup Hibernate'i buna yönlendirmek gerekir — bu ileri düzey bir yapılandırmadır.

---

### 3. `Personel` Hiyerarşisi Henüz Tamamlanmadı

`Doktor.java` şu an tek başına `@Entity` olarak yazılmış, `Personel` üst sınıfı yoktur. Single Table Inheritance için yapılması gerekenler:

```
□ Personel.java  → @Entity + @Inheritance(SINGLE_TABLE) + @DiscriminatorColumn
□ Doktor.java    → extends Personel + @DiscriminatorValue("DOKTOR")
□ Veznedar.java  → extends Personel + @DiscriminatorValue("VEZNEDAR")
□ KayitGorevlisi.java   → extends Personel + @DiscriminatorValue("KAYIT_GOREVLISI")
□ RandevuGorevlisi.java → extends Personel + @DiscriminatorValue("RANDEVU_GOREVLISI")
```

Bu tamamlanana kadar veritabanında `personel` tablosu yerine `doktor` adında ayrı bir tablo oluşur.
