window.HOSPITAL_DATA =
{
  "data/projects.json":
  {
    "site":
    {
      "title": "Hastane Yönetim Sistemi",
      "subtitle": "Use case listesi — işlemler tamamlandıkça [x] güncellenir"
    },
    "useCases":
    [
      {
        "id": "kayit",
        "slug": "kayit",
        "title": "Hasta Sisteme Kayıt Edilir",
        "done": true,
        "role": "KG",
        "roleLabel": "Kayıt Görevlisi",
        "description": "KG hasta kaydeder; hasta login olup kendi bilgilerini görür.",
        "page": "kayit.html",
        "subCases": []
      },
      {
        "id": "rezervasyon",
        "slug": "rezervasyon",
        "title": "Rezervasyon Alınması",
        "done": true,
        "role": "RG",
        "roleLabel": "Randevu Görevlisi",
        "description": "RG doktor seçip randevu oluşturur; hasta randevusunu görür.",
        "page": "rezervasyon.html",
        "subCases":
        [
          {
            "relation": "extend",
            "title": "Alternatif Tarih Önerilmesi",
            "done": false,
            "description": "Uygun doktor yoksa RG alternatif tarih önerir."
          }
        ]
      },
      {
        "id": "muayene",
        "slug": "muayene",
        "title": "Muayene",
        "done": false,
        "role": "DR",
        "roleLabel": "Doktor",
        "description": "Doktor randevuya gelen hastanın bilgilerini görür ve muayene yapar.",
        "page": "muayene.html",
        "subCases":
        [
          {
            "relation": "include",
            "title": "Hasta Bilgilerinin Görüntülenmesi",
            "done": true,
            "description": "Doktor hastanın tüm kaydını görür."
          },
          {
            "relation": "extend",
            "title": "Hasta Kaydına Tedavi Ekleme",
            "done": false,
            "description": "Doktor tedaviyi hasta kaydına ekler."
          },
          {
            "relation": "extend",
            "title": "Rapor / Reçete Verilmesi",
            "done": false,
            "description": "Doktor rapor veya reçete yazar."
          },
          {
            "relation": "extend",
            "title": "Hasta Bilgilerinin Yönetimi",
            "done": false,
            "description": "Doktor hasta bilgilerini günceller."
          }
        ]
      },
      {
        "id": "odeme",
        "slug": "odeme",
        "title": "Ödeme İşleminin Yapılması",
        "done": false,
        "role": "VZ",
        "roleLabel": "Veznedar",
        "description": "Veznedar ücret hesaplar, ödeme alır.",
        "page": "odeme.html",
        "subCases":
        [
          {
            "relation": "include",
            "title": "Sigorta Durumunun Sorgulanması",
            "done": false,
            "description": "Sosyal sigorta sunucusuna TCKN gönderilir."
          },
          {
            "relation": "extend",
            "title": "İndirim Uygulanır",
            "done": false,
            "description": "Sigorta durumuna göre indirim uygulanır."
          }
        ]
      }
    ]
  },
  "data/patients.json":
  {
    "patients":
    [
      {
        "id": "P-1001",
        "tckn": "12345678901",
        "name": "Ayşe Yılmaz",
        "birthDate": "1960-01-01",
        "phone": "0530 100 20 30",
        "insurance": "SGK",
        "registeredAt": "2026-01-01",
        "appointments":
        [
          {
            "id": "R-501",
            "doctor": "Dr. Mehmet Kaya",
            "department": "Dahiliye",
            "date": "2026-06-01",
            "time": "08:30",
            "status": "onaylı"
          },
          {
            "id": "R-701",
            "doctor": "Dr. Ceren Polat",
            "department": "Üroloji",
            "date": "2026-08-01",
            "time": "09:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-01",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          },
          {
            "date": "2026-05-02",
            "note": "Ağrı şikayeti için kontrol planlandı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-01",
            "name": "Tansiyon takibi",
            "dose": "Günde 1 ölçüm"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-01",
            "type": "rapor",
            "text": "Amlodipin 5 mg, 30 tablet"
          }
        ]
      },
      {
        "id": "P-1002",
        "tckn": "98765432109",
        "name": "Can Demir",
        "birthDate": "1961-02-02",
        "phone": "0531 101 21 31",
        "insurance": "özel",
        "registeredAt": "2026-02-02",
        "appointments":
        [
          {
            "id": "R-502",
            "doctor": "Dr. Zeynep Ak",
            "department": "Kardiyoloji",
            "date": "2026-06-02",
            "time": "09:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-02",
            "note": "Kan tahlili değerlendirildi."
          },
          {
            "date": "2026-05-03",
            "note": "Alerji bulguları değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-02",
            "name": "Efor testi",
            "dose": "Kontrol amaçlı"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-02",
            "type": "reçete",
            "text": "Kardiyoloji kontrol raporu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1003",
        "tckn": "11223344556",
        "name": "Elif Arslan",
        "birthDate": "1962-03-03",
        "phone": "0532 102 22 32",
        "insurance": "SGK",
        "registeredAt": "2026-03-03",
        "appointments":
        [
          {
            "id": "R-503",
            "doctor": "Dr. Ali Vural",
            "department": "Ortopedi",
            "date": "2026-06-03",
            "time": "10:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-03",
            "note": "EKG sonucu takip için dosyaya eklendi."
          },
          {
            "date": "2026-05-04",
            "note": "Göz tansiyonu kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-03",
            "name": "Fizik tedavi",
            "dose": "10 seans"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-03",
            "type": "reçete",
            "text": "Ağrı kesici jel, günde 2 uygulama"
          }
        ]
      },
      {
        "id": "P-1004",
        "tckn": "10000000004",
        "name": "Mert Şahin",
        "birthDate": "1963-04-04",
        "phone": "0533 103 23 33",
        "insurance": "SGK",
        "registeredAt": "2026-04-04",
        "appointments":
        [
          {
            "id": "R-504",
            "doctor": "Dr. Deniz Eren",
            "department": "Nöroloji",
            "date": "2026-06-04",
            "time": "11:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-04",
            "note": "Ağrı şikayeti için kontrol planlandı."
          },
          {
            "date": "2026-05-05",
            "note": "Solunum testi yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-04",
            "name": "Migren profilaksisi",
            "dose": "Akşam 1 tablet"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-04",
            "type": "rapor",
            "text": "Migren ilacı, ihtiyaç halinde"
          }
        ]
      },
      {
        "id": "P-1005",
        "tckn": "10000000005",
        "name": "Selin Korkmaz",
        "birthDate": "1964-05-05",
        "phone": "0534 104 24 34",
        "insurance": "SGK",
        "registeredAt": "2026-05-05",
        "appointments":
        [
          {
            "id": "R-505",
            "doctor": "Dr. Burcu Sönmez",
            "department": "Dermatoloji",
            "date": "2026-06-05",
            "time": "13:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-05",
            "note": "Alerji bulguları değerlendirildi."
          },
          {
            "date": "2026-05-06",
            "note": "Ultrason sonucu incelendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-05",
            "name": "Topikal krem",
            "dose": "Sabah akşam"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-05",
            "type": "reçete",
            "text": "Antihistaminik, 7 gün"
          }
        ]
      },
      {
        "id": "P-1006",
        "tckn": "10000000006",
        "name": "Oğuzhan Çelik",
        "birthDate": "1965-06-06",
        "phone": "0535 105 25 35",
        "insurance": "özel",
        "registeredAt": "2026-01-06",
        "appointments":
        [
          {
            "id": "R-506",
            "doctor": "Dr. Ece Tan",
            "department": "Göz Hastalıkları",
            "date": "2026-06-06",
            "time": "14:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-06",
            "note": "Göz tansiyonu kontrol edildi."
          },
          {
            "date": "2026-05-07",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-06",
            "name": "Göz damlası",
            "dose": "Günde 2 damla"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-06",
            "type": "reçete",
            "text": "Göz kontrolü 1 ay sonra tekrarlanacak."
          }
        ]
      },
      {
        "id": "P-1007",
        "tckn": "10000000007",
        "name": "Nazlı Aydın",
        "birthDate": "1966-07-07",
        "phone": "0536 106 26 36",
        "insurance": "SGK",
        "registeredAt": "2026-02-07",
        "appointments":
        [
          {
            "id": "R-507",
            "doctor": "Dr. Baran Koç",
            "department": "Kulak Burun Boğaz",
            "date": "2026-06-07",
            "time": "15:30",
            "status": "onaylı"
          },
          {
            "id": "R-707",
            "doctor": "Dr. İrem Yıldız",
            "department": "Göğüs Hastalıkları",
            "date": "2026-08-07",
            "time": "14:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-07",
            "note": "Solunum testi yapıldı."
          },
          {
            "date": "2026-05-08",
            "note": "Aşı takvimi kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-07",
            "name": "Nazal sprey",
            "dose": "Günde 2 kullanım"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-07",
            "type": "rapor",
            "text": "Burun spreyi, 10 gün"
          }
        ]
      },
      {
        "id": "P-1008",
        "tckn": "10000000008",
        "name": "Bora Yalçın",
        "birthDate": "1967-08-08",
        "phone": "0537 107 27 37",
        "insurance": "SGK",
        "registeredAt": "2026-03-08",
        "appointments":
        [
          {
            "id": "R-508",
            "doctor": "Dr. Ceren Polat",
            "department": "Üroloji",
            "date": "2026-06-08",
            "time": "16:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-08",
            "note": "Ultrason sonucu incelendi."
          },
          {
            "date": "2026-05-09",
            "note": "Rutin kontrol yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-08",
            "name": "Sıvı tüketimi takibi",
            "dose": "Günlük 2 litre"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-08",
            "type": "reçete",
            "text": "Tetkik sonuçlarıyla kontrol önerildi."
          }
        ]
      },
      {
        "id": "P-1009",
        "tckn": "10000000009",
        "name": "Derya Özkan",
        "birthDate": "1968-09-09",
        "phone": "0538 108 28 38",
        "insurance": "SGK",
        "registeredAt": "2026-04-09",
        "appointments":
        [
          {
            "id": "R-509",
            "doctor": "Dr. Levent Sarı",
            "department": "Psikiyatri",
            "date": "2026-06-09",
            "time": "08:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-09",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          },
          {
            "date": "2026-05-10",
            "note": "Diyabet kontrolü için HbA1c istendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-09",
            "name": "Terapi takibi",
            "dose": "Haftada 1 görüşme"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-09",
            "type": "reçete",
            "text": "Psikiyatri kontrol notu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1010",
        "tckn": "10000000010",
        "name": "Eren Mutlu",
        "birthDate": "1969-10-10",
        "phone": "0539 109 29 39",
        "insurance": "özel",
        "registeredAt": "2026-05-10",
        "appointments":
        [
          {
            "id": "R-510",
            "doctor": "Dr. Aslı Güner",
            "department": "Çocuk Sağlığı",
            "date": "2026-06-10",
            "time": "09:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-10",
            "note": "Aşı takvimi kontrol edildi."
          },
          {
            "date": "2026-05-11",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-10",
            "name": "Vitamin desteği",
            "dose": "Günde 1 ölçek"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-10",
            "type": "rapor",
            "text": "Vitamin şurubu, 1 ay"
          }
        ]
      },
      {
        "id": "P-1011",
        "tckn": "10000000011",
        "name": "Funda Kaplan",
        "birthDate": "1970-11-11",
        "phone": "0540 110 30 40",
        "insurance": "SGK",
        "registeredAt": "2026-01-11",
        "appointments":
        [
          {
            "id": "R-511",
            "doctor": "Dr. Onur Taş",
            "department": "Kadın Hastalıkları",
            "date": "2026-06-11",
            "time": "10:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-11",
            "note": "Rutin kontrol yapıldı."
          },
          {
            "date": "2026-05-12",
            "note": "Kan tahlili değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-11",
            "name": "Kontrol planı",
            "dose": "6 ay sonra tekrar"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-11",
            "type": "reçete",
            "text": "Rutin kontrol raporu tamamlandı."
          }
        ]
      },
      {
        "id": "P-1012",
        "tckn": "10000000012",
        "name": "Gökhan Ergin",
        "birthDate": "1971-12-12",
        "phone": "0541 111 31 41",
        "insurance": "SGK",
        "registeredAt": "2026-02-12",
        "appointments":
        [
          {
            "id": "R-512",
            "doctor": "Dr. Sema Işık",
            "department": "Endokrinoloji",
            "date": "2026-06-12",
            "time": "11:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-12",
            "note": "Diyabet kontrolü için HbA1c istendi."
          },
          {
            "date": "2026-05-13",
            "note": "EKG sonucu takip için dosyaya eklendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-12",
            "name": "Diyet takibi",
            "dose": "Günlük glukoz ölçümü"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-12",
            "type": "reçete",
            "text": "Metformin 500 mg, günde 2"
          }
        ]
      },
      {
        "id": "P-1013",
        "tckn": "10000000013",
        "name": "Hale Toprak",
        "birthDate": "1972-01-13",
        "phone": "0542 112 32 42",
        "insurance": "SGK",
        "registeredAt": "2026-03-13",
        "appointments":
        [
          {
            "id": "R-513",
            "doctor": "Dr. Tuna Erbay",
            "department": "Genel Cerrahi",
            "date": "2026-06-13",
            "time": "13:00",
            "status": "onaylı"
          },
          {
            "id": "R-713",
            "doctor": "Dr. Gizem Karaca",
            "department": "Enfeksiyon",
            "date": "2026-08-13",
            "time": "09:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-13",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          },
          {
            "date": "2026-05-14",
            "note": "Ağrı şikayeti için kontrol planlandı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-13",
            "name": "Tansiyon takibi",
            "dose": "Günde 1 ölçüm"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-13",
            "type": "rapor",
            "text": "Amlodipin 5 mg, 30 tablet"
          }
        ]
      },
      {
        "id": "P-1014",
        "tckn": "10000000014",
        "name": "İlker Bozkurt",
        "birthDate": "1973-02-14",
        "phone": "0543 113 33 43",
        "insurance": "özel",
        "registeredAt": "2026-04-14",
        "appointments":
        [
          {
            "id": "R-514",
            "doctor": "Dr. İrem Yıldız",
            "department": "Göğüs Hastalıkları",
            "date": "2026-06-14",
            "time": "14:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-14",
            "note": "Kan tahlili değerlendirildi."
          },
          {
            "date": "2026-05-15",
            "note": "Alerji bulguları değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-14",
            "name": "Efor testi",
            "dose": "Kontrol amaçlı"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-14",
            "type": "reçete",
            "text": "Kardiyoloji kontrol raporu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1015",
        "tckn": "10000000015",
        "name": "Jale Aksoy",
        "birthDate": "1974-03-15",
        "phone": "0544 114 34 44",
        "insurance": "SGK",
        "registeredAt": "2026-05-15",
        "appointments":
        [
          {
            "id": "R-515",
            "doctor": "Dr. Kaan Özdemir",
            "department": "Fizik Tedavi",
            "date": "2026-06-15",
            "time": "15:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-15",
            "note": "EKG sonucu takip için dosyaya eklendi."
          },
          {
            "date": "2026-05-16",
            "note": "Göz tansiyonu kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-15",
            "name": "Fizik tedavi",
            "dose": "10 seans"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-15",
            "type": "reçete",
            "text": "Ağrı kesici jel, günde 2 uygulama"
          }
        ]
      },
      {
        "id": "P-1016",
        "tckn": "10000000016",
        "name": "Kemal Kara",
        "birthDate": "1975-04-16",
        "phone": "0545 115 35 45",
        "insurance": "SGK",
        "registeredAt": "2026-01-16",
        "appointments":
        [
          {
            "id": "R-516",
            "doctor": "Dr. Melis Arı",
            "department": "Beslenme ve Diyet",
            "date": "2026-06-16",
            "time": "16:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-16",
            "note": "Ağrı şikayeti için kontrol planlandı."
          },
          {
            "date": "2026-05-17",
            "note": "Solunum testi yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-16",
            "name": "Migren profilaksisi",
            "dose": "Akşam 1 tablet"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-16",
            "type": "rapor",
            "text": "Migren ilacı, ihtiyaç halinde"
          }
        ]
      },
      {
        "id": "P-1017",
        "tckn": "10000000017",
        "name": "Lale Taş",
        "birthDate": "1976-05-17",
        "phone": "0546 116 36 46",
        "insurance": "SGK",
        "registeredAt": "2026-02-17",
        "appointments":
        [
          {
            "id": "R-517",
            "doctor": "Dr. Yusuf Keskin",
            "department": "Acil Tıp",
            "date": "2026-06-17",
            "time": "08:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-17",
            "note": "Alerji bulguları değerlendirildi."
          },
          {
            "date": "2026-05-18",
            "note": "Ultrason sonucu incelendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-17",
            "name": "Topikal krem",
            "dose": "Sabah akşam"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-17",
            "type": "reçete",
            "text": "Antihistaminik, 7 gün"
          }
        ]
      },
      {
        "id": "P-1018",
        "tckn": "10000000018",
        "name": "Mina Bulut",
        "birthDate": "1977-06-18",
        "phone": "0547 117 37 47",
        "insurance": "özel",
        "registeredAt": "2026-03-18",
        "appointments":
        [
          {
            "id": "R-518",
            "doctor": "Dr. Pelin Duran",
            "department": "Radyoloji",
            "date": "2026-06-18",
            "time": "09:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-18",
            "note": "Göz tansiyonu kontrol edildi."
          },
          {
            "date": "2026-05-19",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-18",
            "name": "Göz damlası",
            "dose": "Günde 2 damla"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-18",
            "type": "reçete",
            "text": "Göz kontrolü 1 ay sonra tekrarlanacak."
          }
        ]
      },
      {
        "id": "P-1019",
        "tckn": "10000000019",
        "name": "Nuri Koç",
        "birthDate": "1978-07-19",
        "phone": "0548 118 38 48",
        "insurance": "SGK",
        "registeredAt": "2026-04-19",
        "appointments":
        [
          {
            "id": "R-519",
            "doctor": "Dr. Emre Güneş",
            "department": "Anestezi",
            "date": "2026-06-19",
            "time": "10:30",
            "status": "onaylı"
          },
          {
            "id": "R-719",
            "doctor": "Dr. Zeynep Ak",
            "department": "Kardiyoloji",
            "date": "2026-08-19",
            "time": "14:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-19",
            "note": "Solunum testi yapıldı."
          },
          {
            "date": "2026-05-20",
            "note": "Aşı takvimi kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-19",
            "name": "Nazal sprey",
            "dose": "Günde 2 kullanım"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-19",
            "type": "rapor",
            "text": "Burun spreyi, 10 gün"
          }
        ]
      },
      {
        "id": "P-1020",
        "tckn": "10000000020",
        "name": "Özge Erdem",
        "birthDate": "1979-08-20",
        "phone": "0549 119 39 49",
        "insurance": "SGK",
        "registeredAt": "2026-05-20",
        "appointments":
        [
          {
            "id": "R-520",
            "doctor": "Dr. Gizem Karaca",
            "department": "Enfeksiyon",
            "date": "2026-06-20",
            "time": "11:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-20",
            "note": "Ultrason sonucu incelendi."
          },
          {
            "date": "2026-05-21",
            "note": "Rutin kontrol yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-20",
            "name": "Sıvı tüketimi takibi",
            "dose": "Günlük 2 litre"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-20",
            "type": "reçete",
            "text": "Tetkik sonuçlarıyla kontrol önerildi."
          }
        ]
      },
      {
        "id": "P-1021",
        "tckn": "10000000021",
        "name": "Poyraz Sarı",
        "birthDate": "1980-09-21",
        "phone": "0550 120 40 50",
        "insurance": "SGK",
        "registeredAt": "2026-01-21",
        "appointments":
        [
          {
            "id": "R-521",
            "doctor": "Dr. Arda Bilgin",
            "department": "Hematoloji",
            "date": "2026-06-21",
            "time": "13:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-21",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          },
          {
            "date": "2026-05-22",
            "note": "Diyabet kontrolü için HbA1c istendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-21",
            "name": "Terapi takibi",
            "dose": "Haftada 1 görüşme"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-21",
            "type": "reçete",
            "text": "Psikiyatri kontrol notu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1022",
        "tckn": "10000000022",
        "name": "Rana Turan",
        "birthDate": "1981-10-22",
        "phone": "0551 121 41 51",
        "insurance": "özel",
        "registeredAt": "2026-02-22",
        "appointments":
        [
          {
            "id": "R-522",
            "doctor": "Dr. Dilan Ekinci",
            "department": "Nefroloji",
            "date": "2026-06-22",
            "time": "14:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-22",
            "note": "Aşı takvimi kontrol edildi."
          },
          {
            "date": "2026-05-23",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-22",
            "name": "Vitamin desteği",
            "dose": "Günde 1 ölçek"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-22",
            "type": "rapor",
            "text": "Vitamin şurubu, 1 ay"
          }
        ]
      },
      {
        "id": "P-1023",
        "tckn": "10000000023",
        "name": "Sarp Keser",
        "birthDate": "1982-11-23",
        "phone": "0552 122 42 52",
        "insurance": "SGK",
        "registeredAt": "2026-03-23",
        "appointments":
        [
          {
            "id": "R-523",
            "doctor": "Dr. Koray Çetin",
            "department": "Romatoloji",
            "date": "2026-06-23",
            "time": "15:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-23",
            "note": "Rutin kontrol yapıldı."
          },
          {
            "date": "2026-05-24",
            "note": "Kan tahlili değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-23",
            "name": "Kontrol planı",
            "dose": "6 ay sonra tekrar"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-23",
            "type": "reçete",
            "text": "Rutin kontrol raporu tamamlandı."
          }
        ]
      },
      {
        "id": "P-1024",
        "tckn": "10000000024",
        "name": "Tuğçe Alkan",
        "birthDate": "1983-12-24",
        "phone": "0553 123 43 53",
        "insurance": "SGK",
        "registeredAt": "2026-04-24",
        "appointments":
        [
          {
            "id": "R-524",
            "doctor": "Dr. Buse Sezer",
            "department": "Algoloji",
            "date": "2026-06-24",
            "time": "16:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-24",
            "note": "Diyabet kontrolü için HbA1c istendi."
          },
          {
            "date": "2026-05-25",
            "note": "EKG sonucu takip için dosyaya eklendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-24",
            "name": "Diyet takibi",
            "dose": "Günlük glukoz ölçümü"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-24",
            "type": "reçete",
            "text": "Metformin 500 mg, günde 2"
          }
        ]
      },
      {
        "id": "P-1025",
        "tckn": "10000000025",
        "name": "Umut Güneş",
        "birthDate": "1984-01-25",
        "phone": "0554 124 44 54",
        "insurance": "SGK",
        "registeredAt": "2026-05-25",
        "appointments":
        [
          {
            "id": "R-525",
            "doctor": "Dr. Mehmet Kaya",
            "department": "Dahiliye",
            "date": "2026-06-25",
            "time": "08:30",
            "status": "beklemede"
          },
          {
            "id": "R-725",
            "doctor": "Dr. Ceren Polat",
            "department": "Üroloji",
            "date": "2026-08-05",
            "time": "09:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-01",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          },
          {
            "date": "2026-05-02",
            "note": "Ağrı şikayeti için kontrol planlandı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-01",
            "name": "Tansiyon takibi",
            "dose": "Günde 1 ölçüm"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-01",
            "type": "rapor",
            "text": "Amlodipin 5 mg, 30 tablet"
          }
        ]
      },
      {
        "id": "P-1026",
        "tckn": "10000000026",
        "name": "Vera Polat",
        "birthDate": "1985-02-26",
        "phone": "0530 125 45 55",
        "insurance": "özel",
        "registeredAt": "2026-01-26",
        "appointments":
        [
          {
            "id": "R-526",
            "doctor": "Dr. Zeynep Ak",
            "department": "Kardiyoloji",
            "date": "2026-06-26",
            "time": "09:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-02",
            "note": "Kan tahlili değerlendirildi."
          },
          {
            "date": "2026-05-03",
            "note": "Alerji bulguları değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-02",
            "name": "Efor testi",
            "dose": "Kontrol amaçlı"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-02",
            "type": "reçete",
            "text": "Kardiyoloji kontrol raporu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1027",
        "tckn": "10000000027",
        "name": "Yağız Uslu",
        "birthDate": "1986-03-27",
        "phone": "0531 126 46 56",
        "insurance": "SGK",
        "registeredAt": "2026-02-27",
        "appointments":
        [
          {
            "id": "R-527",
            "doctor": "Dr. Ali Vural",
            "department": "Ortopedi",
            "date": "2026-06-27",
            "time": "10:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-03",
            "note": "EKG sonucu takip için dosyaya eklendi."
          },
          {
            "date": "2026-05-04",
            "note": "Göz tansiyonu kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-03",
            "name": "Fizik tedavi",
            "dose": "10 seans"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-03",
            "type": "reçete",
            "text": "Ağrı kesici jel, günde 2 uygulama"
          }
        ]
      },
      {
        "id": "P-1028",
        "tckn": "10000000028",
        "name": "Zehra Işık",
        "birthDate": "1987-04-01",
        "phone": "0532 127 47 57",
        "insurance": "SGK",
        "registeredAt": "2026-03-01",
        "appointments":
        [
          {
            "id": "R-528",
            "doctor": "Dr. Deniz Eren",
            "department": "Nöroloji",
            "date": "2026-06-28",
            "time": "11:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-04",
            "note": "Ağrı şikayeti için kontrol planlandı."
          },
          {
            "date": "2026-05-05",
            "note": "Solunum testi yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-04",
            "name": "Migren profilaksisi",
            "dose": "Akşam 1 tablet"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-04",
            "type": "rapor",
            "text": "Migren ilacı, ihtiyaç halinde"
          }
        ]
      },
      {
        "id": "P-1029",
        "tckn": "10000000029",
        "name": "Ada Sezer",
        "birthDate": "1988-05-02",
        "phone": "0533 128 48 58",
        "insurance": "SGK",
        "registeredAt": "2026-04-02",
        "appointments":
        [
          {
            "id": "R-529",
            "doctor": "Dr. Burcu Sönmez",
            "department": "Dermatoloji",
            "date": "2026-06-01",
            "time": "13:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-05",
            "note": "Alerji bulguları değerlendirildi."
          },
          {
            "date": "2026-05-06",
            "note": "Ultrason sonucu incelendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-05",
            "name": "Topikal krem",
            "dose": "Sabah akşam"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-05",
            "type": "reçete",
            "text": "Antihistaminik, 7 gün"
          }
        ]
      },
      {
        "id": "P-1030",
        "tckn": "10000000030",
        "name": "Berk Duman",
        "birthDate": "1989-06-03",
        "phone": "0534 129 49 59",
        "insurance": "özel",
        "registeredAt": "2026-05-03",
        "appointments":
        [
          {
            "id": "R-530",
            "doctor": "Dr. Ece Tan",
            "department": "Göz Hastalıkları",
            "date": "2026-06-02",
            "time": "14:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-06",
            "note": "Göz tansiyonu kontrol edildi."
          },
          {
            "date": "2026-05-07",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-06",
            "name": "Göz damlası",
            "dose": "Günde 2 damla"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-06",
            "type": "reçete",
            "text": "Göz kontrolü 1 ay sonra tekrarlanacak."
          }
        ]
      },
      {
        "id": "P-1031",
        "tckn": "10000000031",
        "name": "Cansu Kılıç",
        "birthDate": "1990-07-04",
        "phone": "0535 130 50 60",
        "insurance": "SGK",
        "registeredAt": "2026-01-04",
        "appointments":
        [
          {
            "id": "R-531",
            "doctor": "Dr. Baran Koç",
            "department": "Kulak Burun Boğaz",
            "date": "2026-07-03",
            "time": "15:30",
            "status": "onaylı"
          },
          {
            "id": "R-731",
            "doctor": "Dr. İrem Yıldız",
            "department": "Göğüs Hastalıkları",
            "date": "2026-08-11",
            "time": "14:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-07",
            "note": "Solunum testi yapıldı."
          },
          {
            "date": "2026-05-08",
            "note": "Aşı takvimi kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-07",
            "name": "Nazal sprey",
            "dose": "Günde 2 kullanım"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-07",
            "type": "rapor",
            "text": "Burun spreyi, 10 gün"
          }
        ]
      },
      {
        "id": "P-1032",
        "tckn": "10000000032",
        "name": "Doruk Acar",
        "birthDate": "1991-08-05",
        "phone": "0536 131 51 61",
        "insurance": "SGK",
        "registeredAt": "2026-02-05",
        "appointments":
        [
          {
            "id": "R-532",
            "doctor": "Dr. Ceren Polat",
            "department": "Üroloji",
            "date": "2026-07-04",
            "time": "16:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-08",
            "note": "Ultrason sonucu incelendi."
          },
          {
            "date": "2026-05-09",
            "note": "Rutin kontrol yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-08",
            "name": "Sıvı tüketimi takibi",
            "dose": "Günlük 2 litre"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-08",
            "type": "reçete",
            "text": "Tetkik sonuçlarıyla kontrol önerildi."
          }
        ]
      },
      {
        "id": "P-1033",
        "tckn": "10000000033",
        "name": "Esra Durmaz",
        "birthDate": "1992-09-06",
        "phone": "0537 132 52 62",
        "insurance": "SGK",
        "registeredAt": "2026-03-06",
        "appointments":
        [
          {
            "id": "R-533",
            "doctor": "Dr. Levent Sarı",
            "department": "Psikiyatri",
            "date": "2026-07-05",
            "time": "08:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-09",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          },
          {
            "date": "2026-05-10",
            "note": "Diyabet kontrolü için HbA1c istendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-09",
            "name": "Terapi takibi",
            "dose": "Haftada 1 görüşme"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-09",
            "type": "reçete",
            "text": "Psikiyatri kontrol notu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1034",
        "tckn": "10000000034",
        "name": "Ferhat Çetin",
        "birthDate": "1993-10-07",
        "phone": "0538 133 53 63",
        "insurance": "özel",
        "registeredAt": "2026-04-07",
        "appointments":
        [
          {
            "id": "R-534",
            "doctor": "Dr. Aslı Güner",
            "department": "Çocuk Sağlığı",
            "date": "2026-07-06",
            "time": "09:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-10",
            "note": "Aşı takvimi kontrol edildi."
          },
          {
            "date": "2026-05-11",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-10",
            "name": "Vitamin desteği",
            "dose": "Günde 1 ölçek"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-10",
            "type": "rapor",
            "text": "Vitamin şurubu, 1 ay"
          }
        ]
      },
      {
        "id": "P-1035",
        "tckn": "10000000035",
        "name": "Gamze Ekinci",
        "birthDate": "1994-11-08",
        "phone": "0539 134 54 64",
        "insurance": "SGK",
        "registeredAt": "2026-05-08",
        "appointments":
        [
          {
            "id": "R-535",
            "doctor": "Dr. Onur Taş",
            "department": "Kadın Hastalıkları",
            "date": "2026-07-07",
            "time": "10:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-11",
            "note": "Rutin kontrol yapıldı."
          },
          {
            "date": "2026-05-12",
            "note": "Kan tahlili değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-11",
            "name": "Kontrol planı",
            "dose": "6 ay sonra tekrar"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-11",
            "type": "reçete",
            "text": "Rutin kontrol raporu tamamlandı."
          }
        ]
      },
      {
        "id": "P-1036",
        "tckn": "10000000036",
        "name": "Hakan Bilgin",
        "birthDate": "1995-12-09",
        "phone": "0540 135 55 65",
        "insurance": "SGK",
        "registeredAt": "2026-01-09",
        "appointments":
        [
          {
            "id": "R-536",
            "doctor": "Dr. Sema Işık",
            "department": "Endokrinoloji",
            "date": "2026-07-08",
            "time": "11:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-12",
            "note": "Diyabet kontrolü için HbA1c istendi."
          },
          {
            "date": "2026-05-13",
            "note": "EKG sonucu takip için dosyaya eklendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-12",
            "name": "Diyet takibi",
            "dose": "Günlük glukoz ölçümü"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-12",
            "type": "reçete",
            "text": "Metformin 500 mg, günde 2"
          }
        ]
      },
      {
        "id": "P-1037",
        "tckn": "10000000037",
        "name": "Işıl Güler",
        "birthDate": "1996-01-10",
        "phone": "0541 136 56 66",
        "insurance": "SGK",
        "registeredAt": "2026-02-10",
        "appointments":
        [
          {
            "id": "R-537",
            "doctor": "Dr. Tuna Erbay",
            "department": "Genel Cerrahi",
            "date": "2026-07-09",
            "time": "13:00",
            "status": "onaylı"
          },
          {
            "id": "R-737",
            "doctor": "Dr. Gizem Karaca",
            "department": "Enfeksiyon",
            "date": "2026-08-17",
            "time": "09:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-13",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          },
          {
            "date": "2026-05-14",
            "note": "Ağrı şikayeti için kontrol planlandı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-13",
            "name": "Tansiyon takibi",
            "dose": "Günde 1 ölçüm"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-13",
            "type": "rapor",
            "text": "Amlodipin 5 mg, 30 tablet"
          }
        ]
      },
      {
        "id": "P-1038",
        "tckn": "10000000038",
        "name": "İpek Karaca",
        "birthDate": "1997-02-11",
        "phone": "0542 137 57 67",
        "insurance": "özel",
        "registeredAt": "2026-03-11",
        "appointments":
        [
          {
            "id": "R-538",
            "doctor": "Dr. İrem Yıldız",
            "department": "Göğüs Hastalıkları",
            "date": "2026-07-10",
            "time": "14:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-14",
            "note": "Kan tahlili değerlendirildi."
          },
          {
            "date": "2026-05-15",
            "note": "Alerji bulguları değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-14",
            "name": "Efor testi",
            "dose": "Kontrol amaçlı"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-14",
            "type": "reçete",
            "text": "Kardiyoloji kontrol raporu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1039",
        "tckn": "10000000039",
        "name": "Kubilay Sönmez",
        "birthDate": "1998-03-12",
        "phone": "0543 138 58 68",
        "insurance": "SGK",
        "registeredAt": "2026-04-12",
        "appointments":
        [
          {
            "id": "R-539",
            "doctor": "Dr. Kaan Özdemir",
            "department": "Fizik Tedavi",
            "date": "2026-07-11",
            "time": "15:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-15",
            "note": "EKG sonucu takip için dosyaya eklendi."
          },
          {
            "date": "2026-05-16",
            "note": "Göz tansiyonu kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-15",
            "name": "Fizik tedavi",
            "dose": "10 seans"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-15",
            "type": "reçete",
            "text": "Ağrı kesici jel, günde 2 uygulama"
          }
        ]
      },
      {
        "id": "P-1040",
        "tckn": "10000000040",
        "name": "Leyla Eren",
        "birthDate": "1999-04-13",
        "phone": "0544 139 59 69",
        "insurance": "SGK",
        "registeredAt": "2026-05-13",
        "appointments":
        [
          {
            "id": "R-540",
            "doctor": "Dr. Melis Arı",
            "department": "Beslenme ve Diyet",
            "date": "2026-07-12",
            "time": "16:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-16",
            "note": "Ağrı şikayeti için kontrol planlandı."
          },
          {
            "date": "2026-05-17",
            "note": "Solunum testi yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-16",
            "name": "Migren profilaksisi",
            "dose": "Akşam 1 tablet"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-16",
            "type": "rapor",
            "text": "Migren ilacı, ihtiyaç halinde"
          }
        ]
      },
      {
        "id": "P-1041",
        "tckn": "10000000041",
        "name": "Murat Öztürk",
        "birthDate": "2000-05-14",
        "phone": "0545 140 60 70",
        "insurance": "SGK",
        "registeredAt": "2026-01-14",
        "appointments":
        [
          {
            "id": "R-541",
            "doctor": "Dr. Yusuf Keskin",
            "department": "Acil Tıp",
            "date": "2026-07-13",
            "time": "08:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-17",
            "note": "Alerji bulguları değerlendirildi."
          },
          {
            "date": "2026-05-18",
            "note": "Ultrason sonucu incelendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-17",
            "name": "Topikal krem",
            "dose": "Sabah akşam"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-17",
            "type": "reçete",
            "text": "Antihistaminik, 7 gün"
          }
        ]
      },
      {
        "id": "P-1042",
        "tckn": "10000000042",
        "name": "Nil Yıldız",
        "birthDate": "2001-06-15",
        "phone": "0546 141 61 71",
        "insurance": "özel",
        "registeredAt": "2026-02-15",
        "appointments":
        [
          {
            "id": "R-542",
            "doctor": "Dr. Pelin Duran",
            "department": "Radyoloji",
            "date": "2026-07-14",
            "time": "09:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-18",
            "note": "Göz tansiyonu kontrol edildi."
          },
          {
            "date": "2026-05-19",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-18",
            "name": "Göz damlası",
            "dose": "Günde 2 damla"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-18",
            "type": "reçete",
            "text": "Göz kontrolü 1 ay sonra tekrarlanacak."
          }
        ]
      },
      {
        "id": "P-1043",
        "tckn": "10000000043",
        "name": "Orhan Kaya",
        "birthDate": "2002-07-16",
        "phone": "0547 142 62 72",
        "insurance": "SGK",
        "registeredAt": "2026-03-16",
        "appointments":
        [
          {
            "id": "R-543",
            "doctor": "Dr. Emre Güneş",
            "department": "Anestezi",
            "date": "2026-07-15",
            "time": "10:30",
            "status": "onaylı"
          },
          {
            "id": "R-743",
            "doctor": "Dr. Zeynep Ak",
            "department": "Kardiyoloji",
            "date": "2026-08-03",
            "time": "14:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-19",
            "note": "Solunum testi yapıldı."
          },
          {
            "date": "2026-05-20",
            "note": "Aşı takvimi kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-19",
            "name": "Nazal sprey",
            "dose": "Günde 2 kullanım"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-19",
            "type": "rapor",
            "text": "Burun spreyi, 10 gün"
          }
        ]
      },
      {
        "id": "P-1044",
        "tckn": "10000000044",
        "name": "Pelin Özer",
        "birthDate": "2003-08-17",
        "phone": "0548 143 63 73",
        "insurance": "SGK",
        "registeredAt": "2026-04-17",
        "appointments":
        [
          {
            "id": "R-544",
            "doctor": "Dr. Gizem Karaca",
            "department": "Enfeksiyon",
            "date": "2026-07-16",
            "time": "11:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-20",
            "note": "Ultrason sonucu incelendi."
          },
          {
            "date": "2026-05-21",
            "note": "Rutin kontrol yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-20",
            "name": "Sıvı tüketimi takibi",
            "dose": "Günlük 2 litre"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-20",
            "type": "reçete",
            "text": "Tetkik sonuçlarıyla kontrol önerildi."
          }
        ]
      },
      {
        "id": "P-1045",
        "tckn": "10000000045",
        "name": "Rıza Güner",
        "birthDate": "2004-09-18",
        "phone": "0549 144 64 74",
        "insurance": "SGK",
        "registeredAt": "2026-05-18",
        "appointments":
        [
          {
            "id": "R-545",
            "doctor": "Dr. Arda Bilgin",
            "department": "Hematoloji",
            "date": "2026-07-17",
            "time": "13:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-21",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          },
          {
            "date": "2026-05-22",
            "note": "Diyabet kontrolü için HbA1c istendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-21",
            "name": "Terapi takibi",
            "dose": "Haftada 1 görüşme"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-21",
            "type": "reçete",
            "text": "Psikiyatri kontrol notu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1046",
        "tckn": "10000000046",
        "name": "Seda Avcı",
        "birthDate": "2005-10-19",
        "phone": "0550 145 65 75",
        "insurance": "özel",
        "registeredAt": "2026-01-19",
        "appointments":
        [
          {
            "id": "R-546",
            "doctor": "Dr. Dilan Ekinci",
            "department": "Nefroloji",
            "date": "2026-07-18",
            "time": "14:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-22",
            "note": "Aşı takvimi kontrol edildi."
          },
          {
            "date": "2026-05-23",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-22",
            "name": "Vitamin desteği",
            "dose": "Günde 1 ölçek"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-22",
            "type": "rapor",
            "text": "Vitamin şurubu, 1 ay"
          }
        ]
      },
      {
        "id": "P-1047",
        "tckn": "10000000047",
        "name": "Tarık Başar",
        "birthDate": "2006-11-20",
        "phone": "0551 146 66 76",
        "insurance": "SGK",
        "registeredAt": "2026-02-20",
        "appointments":
        [
          {
            "id": "R-547",
            "doctor": "Dr. Koray Çetin",
            "department": "Romatoloji",
            "date": "2026-07-19",
            "time": "15:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-23",
            "note": "Rutin kontrol yapıldı."
          },
          {
            "date": "2026-05-24",
            "note": "Kan tahlili değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-23",
            "name": "Kontrol planı",
            "dose": "6 ay sonra tekrar"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-23",
            "type": "reçete",
            "text": "Rutin kontrol raporu tamamlandı."
          }
        ]
      },
      {
        "id": "P-1048",
        "tckn": "10000000048",
        "name": "Ulvi Duran",
        "birthDate": "2007-12-21",
        "phone": "0552 147 67 77",
        "insurance": "SGK",
        "registeredAt": "2026-03-21",
        "appointments":
        [
          {
            "id": "R-548",
            "doctor": "Dr. Buse Sezer",
            "department": "Algoloji",
            "date": "2026-07-20",
            "time": "16:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-24",
            "note": "Diyabet kontrolü için HbA1c istendi."
          },
          {
            "date": "2026-05-25",
            "note": "EKG sonucu takip için dosyaya eklendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-24",
            "name": "Diyet takibi",
            "dose": "Günlük glukoz ölçümü"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-24",
            "type": "reçete",
            "text": "Metformin 500 mg, günde 2"
          }
        ]
      },
      {
        "id": "P-1049",
        "tckn": "10000000049",
        "name": "Volkan Efe",
        "birthDate": "1960-01-22",
        "phone": "0553 148 68 78",
        "insurance": "SGK",
        "registeredAt": "2026-04-22",
        "appointments":
        [
          {
            "id": "R-549",
            "doctor": "Dr. Mehmet Kaya",
            "department": "Dahiliye",
            "date": "2026-07-21",
            "time": "08:30",
            "status": "onaylı"
          },
          {
            "id": "R-749",
            "doctor": "Dr. Ceren Polat",
            "department": "Üroloji",
            "date": "2026-08-09",
            "time": "09:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-01",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          },
          {
            "date": "2026-05-02",
            "note": "Ağrı şikayeti için kontrol planlandı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-01",
            "name": "Tansiyon takibi",
            "dose": "Günde 1 ölçüm"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-01",
            "type": "rapor",
            "text": "Amlodipin 5 mg, 30 tablet"
          }
        ]
      },
      {
        "id": "P-1050",
        "tckn": "10000000050",
        "name": "Yasemin Tan",
        "birthDate": "1961-02-23",
        "phone": "0554 149 69 79",
        "insurance": "özel",
        "registeredAt": "2026-05-23",
        "appointments":
        [
          {
            "id": "R-550",
            "doctor": "Dr. Zeynep Ak",
            "department": "Kardiyoloji",
            "date": "2026-07-22",
            "time": "09:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-02",
            "note": "Kan tahlili değerlendirildi."
          },
          {
            "date": "2026-05-03",
            "note": "Alerji bulguları değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-02",
            "name": "Efor testi",
            "dose": "Kontrol amaçlı"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-02",
            "type": "reçete",
            "text": "Kardiyoloji kontrol raporu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1051",
        "tckn": "10000000051",
        "name": "Zafer Uçar",
        "birthDate": "1962-03-24",
        "phone": "0530 150 70 80",
        "insurance": "SGK",
        "registeredAt": "2026-01-24",
        "appointments":
        [
          {
            "id": "R-551",
            "doctor": "Dr. Ali Vural",
            "department": "Ortopedi",
            "date": "2026-07-23",
            "time": "10:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-03",
            "note": "EKG sonucu takip için dosyaya eklendi."
          },
          {
            "date": "2026-05-04",
            "note": "Göz tansiyonu kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-03",
            "name": "Fizik tedavi",
            "dose": "10 seans"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-03",
            "type": "reçete",
            "text": "Ağrı kesici jel, günde 2 uygulama"
          }
        ]
      },
      {
        "id": "P-1052",
        "tckn": "10000000052",
        "name": "Aylin Yener",
        "birthDate": "1963-04-25",
        "phone": "0531 151 71 81",
        "insurance": "SGK",
        "registeredAt": "2026-02-25",
        "appointments":
        [
          {
            "id": "R-552",
            "doctor": "Dr. Deniz Eren",
            "department": "Nöroloji",
            "date": "2026-07-24",
            "time": "11:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-04",
            "note": "Ağrı şikayeti için kontrol planlandı."
          },
          {
            "date": "2026-05-05",
            "note": "Solunum testi yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-04",
            "name": "Migren profilaksisi",
            "dose": "Akşam 1 tablet"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-04",
            "type": "rapor",
            "text": "Migren ilacı, ihtiyaç halinde"
          }
        ]
      },
      {
        "id": "P-1053",
        "tckn": "10000000053",
        "name": "Burak Ceylan",
        "birthDate": "1964-05-26",
        "phone": "0532 152 72 82",
        "insurance": "SGK",
        "registeredAt": "2026-03-26",
        "appointments":
        [
          {
            "id": "R-553",
            "doctor": "Dr. Burcu Sönmez",
            "department": "Dermatoloji",
            "date": "2026-07-25",
            "time": "13:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-05",
            "note": "Alerji bulguları değerlendirildi."
          },
          {
            "date": "2026-05-06",
            "note": "Ultrason sonucu incelendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-05",
            "name": "Topikal krem",
            "dose": "Sabah akşam"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-05",
            "type": "reçete",
            "text": "Antihistaminik, 7 gün"
          }
        ]
      },
      {
        "id": "P-1054",
        "tckn": "10000000054",
        "name": "Çağla Ateş",
        "birthDate": "1965-06-27",
        "phone": "0533 153 73 83",
        "insurance": "özel",
        "registeredAt": "2026-04-27",
        "appointments":
        [
          {
            "id": "R-554",
            "doctor": "Dr. Ece Tan",
            "department": "Göz Hastalıkları",
            "date": "2026-07-26",
            "time": "14:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-06",
            "note": "Göz tansiyonu kontrol edildi."
          },
          {
            "date": "2026-05-07",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-06",
            "name": "Göz damlası",
            "dose": "Günde 2 damla"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-06",
            "type": "reçete",
            "text": "Göz kontrolü 1 ay sonra tekrarlanacak."
          }
        ]
      },
      {
        "id": "P-1055",
        "tckn": "10000000055",
        "name": "Demir Baran",
        "birthDate": "1966-07-01",
        "phone": "0534 154 74 84",
        "insurance": "SGK",
        "registeredAt": "2026-05-01",
        "appointments":
        [
          {
            "id": "R-555",
            "doctor": "Dr. Baran Koç",
            "department": "Kulak Burun Boğaz",
            "date": "2026-07-27",
            "time": "15:30",
            "status": "beklemede"
          },
          {
            "id": "R-755",
            "doctor": "Dr. İrem Yıldız",
            "department": "Göğüs Hastalıkları",
            "date": "2026-08-15",
            "time": "14:00",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-07",
            "note": "Solunum testi yapıldı."
          },
          {
            "date": "2026-05-08",
            "note": "Aşı takvimi kontrol edildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-07",
            "name": "Nazal sprey",
            "dose": "Günde 2 kullanım"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-07",
            "type": "rapor",
            "text": "Burun spreyi, 10 gün"
          }
        ]
      },
      {
        "id": "P-1056",
        "tckn": "10000000056",
        "name": "Eylül Kurt",
        "birthDate": "1967-08-02",
        "phone": "0535 155 75 85",
        "insurance": "SGK",
        "registeredAt": "2026-01-02",
        "appointments":
        [
          {
            "id": "R-556",
            "doctor": "Dr. Ceren Polat",
            "department": "Üroloji",
            "date": "2026-07-28",
            "time": "16:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-08",
            "note": "Ultrason sonucu incelendi."
          },
          {
            "date": "2026-05-09",
            "note": "Rutin kontrol yapıldı."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-08",
            "name": "Sıvı tüketimi takibi",
            "dose": "Günlük 2 litre"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-08",
            "type": "reçete",
            "text": "Tetkik sonuçlarıyla kontrol önerildi."
          }
        ]
      },
      {
        "id": "P-1057",
        "tckn": "10000000057",
        "name": "Fikret Önal",
        "birthDate": "1968-09-03",
        "phone": "0536 156 76 86",
        "insurance": "SGK",
        "registeredAt": "2026-02-03",
        "appointments":
        [
          {
            "id": "R-557",
            "doctor": "Dr. Levent Sarı",
            "department": "Psikiyatri",
            "date": "2026-07-01",
            "time": "08:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-09",
            "note": "Uyku düzeni ve stres takibi başlatıldı."
          },
          {
            "date": "2026-05-10",
            "note": "Diyabet kontrolü için HbA1c istendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-09",
            "name": "Terapi takibi",
            "dose": "Haftada 1 görüşme"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-09",
            "type": "reçete",
            "text": "Psikiyatri kontrol notu oluşturuldu."
          }
        ]
      },
      {
        "id": "P-1058",
        "tckn": "10000000058",
        "name": "Gül Şimşek",
        "birthDate": "1969-10-04",
        "phone": "0537 157 77 87",
        "insurance": "özel",
        "registeredAt": "2026-03-04",
        "appointments":
        [
          {
            "id": "R-558",
            "doctor": "Dr. Aslı Güner",
            "department": "Çocuk Sağlığı",
            "date": "2026-07-02",
            "time": "09:00",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-10",
            "note": "Aşı takvimi kontrol edildi."
          },
          {
            "date": "2026-05-11",
            "note": "Tansiyon kontrolü normal sınırlar içinde."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-10",
            "name": "Vitamin desteği",
            "dose": "Günde 1 ölçek"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-10",
            "type": "rapor",
            "text": "Vitamin şurubu, 1 ay"
          }
        ]
      },
      {
        "id": "P-1059",
        "tckn": "10000000059",
        "name": "Harun Tekin",
        "birthDate": "1970-11-05",
        "phone": "0538 158 78 88",
        "insurance": "SGK",
        "registeredAt": "2026-04-05",
        "appointments":
        [
          {
            "id": "R-559",
            "doctor": "Dr. Onur Taş",
            "department": "Kadın Hastalıkları",
            "date": "2026-07-03",
            "time": "10:30",
            "status": "onaylı"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-11",
            "note": "Rutin kontrol yapıldı."
          },
          {
            "date": "2026-05-12",
            "note": "Kan tahlili değerlendirildi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-11",
            "name": "Kontrol planı",
            "dose": "6 ay sonra tekrar"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-11",
            "type": "reçete",
            "text": "Rutin kontrol raporu tamamlandı."
          }
        ]
      },
      {
        "id": "P-1060",
        "tckn": "10000000060",
        "name": "İdil Yücel",
        "birthDate": "1971-12-06",
        "phone": "0539 159 79 89",
        "insurance": "SGK",
        "registeredAt": "2026-05-06",
        "appointments":
        [
          {
            "id": "R-560",
            "doctor": "Dr. Sema Işık",
            "department": "Endokrinoloji",
            "date": "2026-07-04",
            "time": "11:30",
            "status": "beklemede"
          }
        ],
        "records":
        [
          {
            "date": "2026-05-12",
            "note": "Diyabet kontrolü için HbA1c istendi."
          },
          {
            "date": "2026-05-13",
            "note": "EKG sonucu takip için dosyaya eklendi."
          }
        ],
        "treatments":
        [
          {
            "date": "2026-05-12",
            "name": "Diyet takibi",
            "dose": "Günlük glukoz ölçümü"
          }
        ],
        "prescriptions":
        [
          {
            "date": "2026-05-12",
            "type": "reçete",
            "text": "Metformin 500 mg, günde 2"
          }
        ]
      }
    ],
    "staffProfiles":
    {
      "KG":
      {
        "id": "KG-01",
        "role": "KG",
        "roleLabel": "Kayıt Görevlisi",
        "name": "Nihan Kaya",
        "email": "kayit@hastane.com",
        "department": "Hasta Kayıt",
        "title": "Kayıt Görevlisi",
        "phone": "0532 100 1100",
        "tckn": "40000000004",
        "birthDate": "1992-02-13",
        "address": "Hastane Merkez Kayıt Birimi"
      },
      "RG":
      {
        "id": "RG-01",
        "role": "RG",
        "roleLabel": "Randevu Görevlisi",
        "name": "Kerem Uslu",
        "email": "randevu@hastane.com",
        "department": "Randevu Birimi",
        "title": "Randevu Görevlisi",
        "phone": "0532 100 2200",
        "tckn": "50000000005",
        "birthDate": "1989-10-04",
        "address": "Hastane Merkez Randevu Birimi"
      },
      "DR":
      {
        "id": "D-01",
        "role": "DR",
        "roleLabel": "Doktor",
        "name": "Dr. Mehmet Kaya",
        "email": "doktor@hastane.com",
        "department": "Dahiliye",
        "title": "Uzman",
        "phone": "0532 222 3344",
        "tckn": "30000000003",
        "birthDate": "1980-06-18",
        "address": "Hastane Merkez Poliklinik"
      },
      "VZ":
      {
        "id": "VZ-01",
        "role": "VZ",
        "roleLabel": "Veznedar",
        "name": "Seda Altun",
        "email": "vezne@hastane.com",
        "department": "Vezne",
        "title": "Veznedar",
        "phone": "0532 100 3300",
        "tckn": "60000000006",
        "birthDate": "1987-07-28",
        "address": "Hastane Merkez Vezne"
      }
    },
    "doctors":
    [
      {
        "id": "D-01",
        "name": "Dr. Mehmet Kaya",
        "department": "Dahiliye",
        "availableSlots":
        [
          "2026-06-10 09:00",
          "2026-06-10 10:30",
          "2026-06-11 14:00",
          "2026-06-12 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-10 09:30",
          "2026-07-11 13:30",
          "2026-07-12 16:00"
        ]
      },
      {
        "id": "D-02",
        "name": "Dr. Zeynep Ak",
        "department": "Kardiyoloji",
        "availableSlots":
        [
          "2026-06-11 09:00",
          "2026-06-11 10:30",
          "2026-06-12 14:00",
          "2026-06-13 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-11 09:30",
          "2026-07-12 13:30",
          "2026-07-13 16:00"
        ]
      },
      {
        "id": "D-03",
        "name": "Dr. Ali Vural",
        "department": "Ortopedi",
        "availableSlots": [],
        "alternativeSlots":
        [
          "2026-07-12 09:30",
          "2026-07-13 13:30",
          "2026-07-14 16:00"
        ]
      },
      {
        "id": "D-04",
        "name": "Dr. Deniz Eren",
        "department": "Nöroloji",
        "availableSlots":
        [
          "2026-06-13 09:00",
          "2026-06-13 10:30",
          "2026-06-14 14:00",
          "2026-06-15 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-13 09:30",
          "2026-07-14 13:30",
          "2026-07-15 16:00"
        ]
      },
      {
        "id": "D-05",
        "name": "Dr. Burcu Sönmez",
        "department": "Dermatoloji",
        "availableSlots":
        [
          "2026-06-14 09:00",
          "2026-06-14 10:30",
          "2026-06-15 14:00",
          "2026-06-16 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-14 09:30",
          "2026-07-15 13:30",
          "2026-07-16 16:00"
        ]
      },
      {
        "id": "D-06",
        "name": "Dr. Ece Tan",
        "department": "Göz Hastalıkları",
        "availableSlots":
        [
          "2026-06-15 09:00",
          "2026-06-15 10:30",
          "2026-06-16 14:00",
          "2026-06-17 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-15 09:30",
          "2026-07-16 13:30",
          "2026-07-17 16:00"
        ]
      },
      {
        "id": "D-07",
        "name": "Dr. Baran Koç",
        "department": "Kulak Burun Boğaz",
        "availableSlots":
        [
          "2026-06-16 09:00",
          "2026-06-16 10:30",
          "2026-06-17 14:00",
          "2026-06-18 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-16 09:30",
          "2026-07-17 13:30",
          "2026-07-18 16:00"
        ]
      },
      {
        "id": "D-08",
        "name": "Dr. Ceren Polat",
        "department": "Üroloji",
        "availableSlots":
        [
          "2026-06-17 09:00",
          "2026-06-17 10:30",
          "2026-06-18 14:00",
          "2026-06-19 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-17 09:30",
          "2026-07-18 13:30",
          "2026-07-19 16:00"
        ]
      },
      {
        "id": "D-09",
        "name": "Dr. Levent Sarı",
        "department": "Psikiyatri",
        "availableSlots":
        [
          "2026-06-18 09:00",
          "2026-06-18 10:30",
          "2026-06-19 14:00",
          "2026-06-20 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-18 09:30",
          "2026-07-19 13:30",
          "2026-07-20 16:00"
        ]
      },
      {
        "id": "D-10",
        "name": "Dr. Aslı Güner",
        "department": "Çocuk Sağlığı",
        "availableSlots":
        [
          "2026-06-19 09:00",
          "2026-06-19 10:30",
          "2026-06-20 14:00",
          "2026-06-21 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-19 09:30",
          "2026-07-20 13:30",
          "2026-07-21 16:00"
        ]
      },
      {
        "id": "D-11",
        "name": "Dr. Onur Taş",
        "department": "Kadın Hastalıkları",
        "availableSlots":
        [
          "2026-06-20 09:00",
          "2026-06-20 10:30",
          "2026-06-21 14:00",
          "2026-06-22 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-20 09:30",
          "2026-07-21 13:30",
          "2026-07-22 16:00"
        ]
      },
      {
        "id": "D-12",
        "name": "Dr. Sema Işık",
        "department": "Endokrinoloji",
        "availableSlots":
        [
          "2026-06-21 09:00",
          "2026-06-21 10:30",
          "2026-06-22 14:00",
          "2026-06-23 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-21 09:30",
          "2026-07-22 13:30",
          "2026-07-23 16:00"
        ]
      },
      {
        "id": "D-13",
        "name": "Dr. Tuna Erbay",
        "department": "Genel Cerrahi",
        "availableSlots": [],
        "alternativeSlots":
        [
          "2026-07-22 09:30",
          "2026-07-23 13:30",
          "2026-07-24 16:00"
        ]
      },
      {
        "id": "D-14",
        "name": "Dr. İrem Yıldız",
        "department": "Göğüs Hastalıkları",
        "availableSlots":
        [
          "2026-06-23 09:00",
          "2026-06-23 10:30",
          "2026-06-24 14:00",
          "2026-06-25 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-23 09:30",
          "2026-07-24 13:30",
          "2026-07-25 16:00"
        ]
      },
      {
        "id": "D-15",
        "name": "Dr. Kaan Özdemir",
        "department": "Fizik Tedavi",
        "availableSlots":
        [
          "2026-06-24 09:00",
          "2026-06-24 10:30",
          "2026-06-25 14:00",
          "2026-06-26 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-24 09:30",
          "2026-07-25 13:30",
          "2026-07-26 16:00"
        ]
      },
      {
        "id": "D-16",
        "name": "Dr. Melis Arı",
        "department": "Beslenme ve Diyet",
        "availableSlots":
        [
          "2026-06-25 09:00",
          "2026-06-25 10:30",
          "2026-06-26 14:00",
          "2026-06-27 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-25 09:30",
          "2026-07-26 13:30",
          "2026-07-27 16:00"
        ]
      },
      {
        "id": "D-17",
        "name": "Dr. Yusuf Keskin",
        "department": "Acil Tıp",
        "availableSlots":
        [
          "2026-06-26 09:00",
          "2026-06-26 10:30",
          "2026-06-27 14:00",
          "2026-06-28 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-26 09:30",
          "2026-07-27 13:30",
          "2026-07-28 16:00"
        ]
      },
      {
        "id": "D-18",
        "name": "Dr. Pelin Duran",
        "department": "Radyoloji",
        "availableSlots":
        [
          "2026-06-27 09:00",
          "2026-06-27 10:30",
          "2026-06-28 14:00",
          "2026-06-29 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-27 09:30",
          "2026-07-28 13:30",
          "2026-07-29 16:00"
        ]
      },
      {
        "id": "D-19",
        "name": "Dr. Emre Güneş",
        "department": "Anestezi",
        "availableSlots":
        [
          "2026-07-10 09:00",
          "2026-07-10 10:30",
          "2026-07-11 14:00",
          "2026-07-12 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-28 09:30",
          "2026-07-29 13:30",
          "2026-07-30 16:00"
        ]
      },
      {
        "id": "D-20",
        "name": "Dr. Gizem Karaca",
        "department": "Enfeksiyon",
        "availableSlots":
        [
          "2026-07-11 09:00",
          "2026-07-11 10:30",
          "2026-07-12 14:00",
          "2026-07-13 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-29 09:30",
          "2026-07-30 13:30",
          "2026-07-31 16:00"
        ]
      },
      {
        "id": "D-21",
        "name": "Dr. Arda Bilgin",
        "department": "Hematoloji",
        "availableSlots": [],
        "alternativeSlots":
        [
          "2026-07-30 09:30",
          "2026-07-31 13:30",
          "2026-07-32 16:00"
        ]
      },
      {
        "id": "D-22",
        "name": "Dr. Dilan Ekinci",
        "department": "Nefroloji",
        "availableSlots":
        [
          "2026-07-13 09:00",
          "2026-07-13 10:30",
          "2026-07-14 14:00",
          "2026-07-15 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-31 09:30",
          "2026-07-32 13:30",
          "2026-07-33 16:00"
        ]
      },
      {
        "id": "D-23",
        "name": "Dr. Koray Çetin",
        "department": "Romatoloji",
        "availableSlots":
        [
          "2026-07-14 09:00",
          "2026-07-14 10:30",
          "2026-07-15 14:00",
          "2026-07-16 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-32 09:30",
          "2026-07-33 13:30",
          "2026-07-34 16:00"
        ]
      },
      {
        "id": "D-24",
        "name": "Dr. Buse Sezer",
        "department": "Algoloji",
        "availableSlots":
        [
          "2026-07-15 09:00",
          "2026-07-15 10:30",
          "2026-07-16 14:00",
          "2026-07-17 15:30"
        ],
        "alternativeSlots":
        [
          "2026-07-33 09:30",
          "2026-07-34 13:30",
          "2026-07-35 16:00"
        ]
      }
    ],
    "payments":
    [
      {
        "id": "PY-801",
        "patientId": "P-1001",
        "amount": 280,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-802",
        "patientId": "P-1002",
        "amount": 335,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-803",
        "patientId": "P-1003",
        "amount": 390,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-804",
        "patientId": "P-1004",
        "amount": 445,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-805",
        "patientId": "P-1005",
        "amount": 500,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-806",
        "patientId": "P-1006",
        "amount": 555,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-807",
        "patientId": "P-1007",
        "amount": 610,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": true
      },
      {
        "id": "PY-808",
        "patientId": "P-1008",
        "amount": 665,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-809",
        "patientId": "P-1009",
        "amount": 720,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-810",
        "patientId": "P-1010",
        "amount": 775,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-811",
        "patientId": "P-1011",
        "amount": 830,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-812",
        "patientId": "P-1012",
        "amount": 885,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-813",
        "patientId": "P-1013",
        "amount": 940,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-814",
        "patientId": "P-1014",
        "amount": 995,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": false
      },
      {
        "id": "PY-815",
        "patientId": "P-1015",
        "amount": 1050,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-816",
        "patientId": "P-1016",
        "amount": 1105,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-817",
        "patientId": "P-1017",
        "amount": 1160,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-818",
        "patientId": "P-1018",
        "amount": 315,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-819",
        "patientId": "P-1019",
        "amount": 370,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-820",
        "patientId": "P-1020",
        "amount": 425,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-821",
        "patientId": "P-1021",
        "amount": 480,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": false
      },
      {
        "id": "PY-822",
        "patientId": "P-1022",
        "amount": 535,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-823",
        "patientId": "P-1023",
        "amount": 590,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-824",
        "patientId": "P-1024",
        "amount": 645,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-825",
        "patientId": "P-1025",
        "amount": 700,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-826",
        "patientId": "P-1026",
        "amount": 755,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-827",
        "patientId": "P-1027",
        "amount": 810,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-828",
        "patientId": "P-1028",
        "amount": 865,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": false
      },
      {
        "id": "PY-829",
        "patientId": "P-1029",
        "amount": 920,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-830",
        "patientId": "P-1030",
        "amount": 975,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-831",
        "patientId": "P-1031",
        "amount": 1030,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-832",
        "patientId": "P-1032",
        "amount": 1085,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-833",
        "patientId": "P-1033",
        "amount": 1140,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-834",
        "patientId": "P-1034",
        "amount": 295,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-835",
        "patientId": "P-1035",
        "amount": 350,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": true
      },
      {
        "id": "PY-836",
        "patientId": "P-1036",
        "amount": 405,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-837",
        "patientId": "P-1037",
        "amount": 460,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-838",
        "patientId": "P-1038",
        "amount": 515,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-839",
        "patientId": "P-1039",
        "amount": 570,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-840",
        "patientId": "P-1040",
        "amount": 625,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-841",
        "patientId": "P-1041",
        "amount": 680,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-842",
        "patientId": "P-1042",
        "amount": 735,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": false
      },
      {
        "id": "PY-843",
        "patientId": "P-1043",
        "amount": 790,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-844",
        "patientId": "P-1044",
        "amount": 845,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-845",
        "patientId": "P-1045",
        "amount": 900,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-846",
        "patientId": "P-1046",
        "amount": 955,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-847",
        "patientId": "P-1047",
        "amount": 1010,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-848",
        "patientId": "P-1048",
        "amount": 1065,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-849",
        "patientId": "P-1049",
        "amount": 1120,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": false
      },
      {
        "id": "PY-850",
        "patientId": "P-1050",
        "amount": 1175,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-851",
        "patientId": "P-1051",
        "amount": 330,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-852",
        "patientId": "P-1052",
        "amount": 385,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-853",
        "patientId": "P-1053",
        "amount": 440,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-854",
        "patientId": "P-1054",
        "amount": 495,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-855",
        "patientId": "P-1055",
        "amount": 550,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-856",
        "patientId": "P-1056",
        "amount": 605,
        "currency": "TRY",
        "insuranceQuery": "—",
        "discount": 0,
        "paid": false
      },
      {
        "id": "PY-857",
        "patientId": "P-1057",
        "amount": 660,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      },
      {
        "id": "PY-858",
        "patientId": "P-1058",
        "amount": 715,
        "currency": "TRY",
        "insuranceQuery": "Özel sigorta",
        "discount": 0.1,
        "paid": false
      },
      {
        "id": "PY-859",
        "patientId": "P-1059",
        "amount": 770,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": true
      },
      {
        "id": "PY-860",
        "patientId": "P-1060",
        "amount": 825,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      }
    ]
  }
};
