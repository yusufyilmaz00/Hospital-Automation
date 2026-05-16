# Hospital Backend

Spring Boot 4 · Java 25 · PostgreSQL (Neon) · Hibernate JPA · Swagger UI

---

## Gereksinimler

- Java 25+
- Maven (proje içindeki `mvnw` wrapper ile Maven kurulumuna gerek yok)
- PostgreSQL veritabanı (önerilen: [Neon](https://neon.tech) ücretsiz bulut PostgreSQL)

---

## Kurulum

### 1. Veritabanı Bağlantısını Ayarla

Uygulama veritabanı bilgilerini `application-local.properties` dosyasından okur.  
Bu dosya git'e gitmez, her geliştirici kendi dosyasını oluşturur.

`backend/src/main/resources/` dizininde `application-local.properties` dosyası oluştur:

```properties
spring.datasource.url=jdbc:postgresql://<host>/<database>?sslmode=require
spring.datasource.username=<kullanici_adi>
spring.datasource.password=<sifre>
```

Neon kullanıyorsan bağlantı bilgilerini Neon Dashboard → Connection Details bölümünden alabilirsin.  
Dikkat: URL içine kullanıcı adı ve şifre **yazma**, bunları ayrı satırlara koy.

Referans format için `.env.example` dosyasına bakabilirsin.

> **Not:** Proje kökünde `.env` dosyası da bulunur ancak uygulama bunu okumaz.  
> Geçmişteki bir yapılandırma denemesinin kalıntısıdır, `application-local.properties` kullanılır.

---

### 2. Derleme ve Çalıştırma

Proje Maven Wrapper (`mvnw`) içerir — sisteme ayrıca Maven kurmana gerek yok.

**Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**
```bash
./mvnw spring-boot:run
```

`local` profili varsayılan olarak otomatik aktif olur, `-Dspring-boot.run.profiles=local` bayrağına gerek yok.

Uygulama `http://localhost:8080` adresinde başlar.

---

### 3. JAR Olarak Derle ve Çalıştır

Çalıştırılabilir JAR dosyası üretmek için:

```powershell
# Windows
.\mvnw.cmd clean package -DskipTests

# macOS / Linux
./mvnw clean package -DskipTests
```

Derleme tamamlanınca `target/hospital-0.0.1-SNAPSHOT.jar` oluşur.

JAR'ı çalıştırmak için:

```bash
java -jar target/hospital-0.0.1-SNAPSHOT.jar
```

---

### 4. Testleri Çalıştır

```powershell
# Windows
.\mvnw.cmd test

# macOS / Linux
./mvnw test
```

---

## Veritabanı Tabloları

Tablolar elle oluşturulmaz. Uygulama başladığında Hibernate, `@Entity` sınıflarını okuyarak  
PostgreSQL'de gerekli tabloları otomatik oluşturur (`ddl-auto=update`).

Tablo tasarımı ve ilişkiler için `documents/sql_roadmap.md` dosyasına bakabilirsin.

---

## API Dokümantasyonu

Uygulama ayağa kalktıktan sonra Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Profil Sistemi

| Profil | Ne Zaman Aktif | Yapılandırma Dosyası |
|---|---|---|
| `local` | Varsayılan (geliştirme) | `application-local.properties` |
| `prod` | `SPRING_PROFILES_ACTIVE=prod` env var ile | Sunucu ortam değişkenleri |

Production ortamında `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` ve `SPRING_PROFILES_ACTIVE=prod`  
değerlerini sunucu ortam değişkeni olarak set et.

---

## Sık Karşılaşılan Sorunlar

**Bağlantı zaman aşımı (SocketTimeoutException)**  
Neon free tier veritabanı hareketsizlik sonrası uyur. Neon Dashboard'dan kontrol et,  
gerekirse bir sorgu çalıştırarak uyandır. Port 5432'yi engelleyen ağlarda da bu hata alınır  
(okul/iş ağı gibi) — mobil veri veya VPN ile dene.

**`${DB_URL}` çözümlenemiyor hatası**  
`application-local.properties` dosyasının `src/main/resources/` içinde olduğundan emin ol.

**Tablolar oluşturulmuyor**  
`HospitalApplication.java` içinde `DataSourceAutoConfiguration` exclude edilmemiş olmalı.  
Sınıf başlığının sadece `@SpringBootApplication` olduğunu kontrol et.
