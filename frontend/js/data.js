window.HOSPITAL_DATA = {
  "data/projects.json": {
    "site": {
      "title": "Hastane Yönetim Sistemi",
      "subtitle": "Use case listesi — işlemler tamamlandıkça [x] güncellenir"
    },
    "useCases": [
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
        "subCases": [
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
        "subCases": [
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
        "subCases": [
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
  "data/patients.json": {
    "patients": [
      {
        "id": "P-1001",
        "tckn": "12345678901",
        "name": "Ayşe Yılmaz",
        "birthDate": "1988-04-12",
        "phone": "0532 111 2233",
        "insurance": "SGK",
        "registeredAt": "2026-01-15",
        "appointments": [
          {
            "id": "R-501",
            "doctor": "Dr. Mehmet Kaya",
            "department": "Dahiliye",
            "date": "2026-05-28",
            "time": "10:30",
            "status": "onaylı"
          }
        ],
        "records": [
          {
            "date": "2026-03-02",
            "note": "Tansiyon kontrolü — normal sınırlar."
          }
        ],
        "treatments": [],
        "prescriptions": []
      },
      {
        "id": "P-1002",
        "tckn": "98765432109",
        "name": "Can Demir",
        "birthDate": "1995-11-03",
        "phone": "0544 555 6677",
        "insurance": "özel",
        "registeredAt": "2026-02-20",
        "appointments": [],
        "records": [],
        "treatments": [],
        "prescriptions": []
      },
      {
        "id": "P-1003",
        "tckn": "11223344556",
        "name": "Elif Arslan",
        "birthDate": "1972-07-21",
        "phone": "0505 888 9900",
        "insurance": "SGK",
        "registeredAt": "2026-05-10",
        "appointments": [
          {
            "id": "R-502",
            "doctor": "Dr. Zeynep Ak",
            "department": "Kardiyoloji",
            "date": "2026-05-24",
            "time": "14:00",
            "status": "beklemede"
          }
        ],
        "records": [
          {
            "date": "2026-05-20",
            "note": "EKG çekildi — takip önerildi."
          }
        ],
        "treatments": [],
        "prescriptions": []
      }
    ],
    "doctors": [
      {
        "id": "D-01",
        "name": "Dr. Mehmet Kaya",
        "department": "Dahiliye",
        "availableSlots": [
          "2026-05-28 10:30",
          "2026-05-29 09:00"
        ]
      },
      {
        "id": "D-02",
        "name": "Dr. Zeynep Ak",
        "department": "Kardiyoloji",
        "availableSlots": [
          "2026-05-30 11:00"
        ],
        "alternativeSlots": [
          "2026-06-01 11:00",
          "2026-06-03 09:30"
        ]
      },
      {
        "id": "D-03",
        "name": "Dr. Ali Vural",
        "department": "Ortopedi",
        "availableSlots": [],
        "alternativeSlots": [
          "2026-06-02 10:00",
          "2026-06-05 15:30",
          "2026-06-08 08:00"
        ]
      }
    ],
    "payments": [
      {
        "id": "PY-801",
        "patientId": "P-1001",
        "amount": 450,
        "currency": "TRY",
        "insuranceQuery": "SGK aktif",
        "discount": 0.2,
        "paid": false
      }
    ]
  }
};
