
function apptFormPatientDoctor() {
  const form = document.getElementById("appt-form");
  if (!form) {
    return { patientId: "", doctorId: "" };
  }
  return {
    patientId: form.querySelector("[name=patientId]").value,
    doctorId: form.querySelector("[name=doctorId]").value
  };
}

function setupEvents() {
document.addEventListener("click", function (e)
{
  const btn = e.target.closest("[data-action]");
  if (!btn)
  {
    return;
  }
  const action = btn.dataset.action;
  if (action === "logout")
  {
    if (!isTestMode()) {
      fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        credentials: "include"
      }).catch(console.error);
    }
    clearSession();
    toast("Çıkış yapıldı.");
    refreshPage();
    return;
  }
  if (action === "demo-login")
  {
    const role = btn.dataset.role;
    const labels = { KG: "Kayıt Görevlisi", RG: "Randevu Görevlisi", DR: "Doktor", VZ: "Veznedar" };
    setSession({ kind: "staff", role: role, roleLabel: labels[role] || role });
    toast(labels[role] + " olarak giriş yapıldı.");
    const pages = { KG: "kayit.html", RG: "rezervasyon.html", DR: "muayene.html", VZ: "odeme.html" };
    if (pages[role])
    {
      window.location.href = pages[role];
    }
    return;
  }
  if (action === "view-patient")
  {
    showPatientDetail(btn.dataset.id);
    return;
  }
  if (action === "close-detail")
  {
    const box = document.getElementById("patient-detail");
    if (box)
    {
      box.innerHTML = "";
    }
    return;
  }
  if (action === "tab-login")
  {
    const tab = btn.dataset.tab;
    document.querySelectorAll(".login-tabs button").forEach(function (b)
    {
      b.classList.toggle("active", b.dataset.tab === tab);
    });
    document.getElementById("login-staff").hidden = tab !== "staff";
    document.getElementById("login-patient").hidden = tab !== "patient";
    return;
  }
  if (action === "reset-store")
  {
    resetStore();
    toast("Veriler başlangıç haline getirildi.");
    refreshPage();
    return;
  }
  if (action === "pick-doctor")
  {
    const fd = apptFormPatientDoctor();
    localStorage.setItem("rg-pick-doctor", fd.doctorId);
    refreshPage();
    return;
  }
  if (action === "book-alt")
  {
    if (!hasRole(getSession(), ["RG"]))
    {
      toast("RG olarak giriş yapın.", true);
      return;
    }
    const fd = apptFormPatientDoctor();
    if (!fd.patientId)
    {
      toast("Önce hasta seçin.", true);
      return;
    }
    if (bookSlot(fd.patientId, btn.dataset.did, btn.dataset.slot, true))
    {
      toast("Alternatif tarih ile randevu oluşturuldu.");
      localStorage.removeItem("rg-pick-doctor");
      refreshPage();
    }
    return;
  }
  if (action === "open-muayene")
  {
    localStorage.setItem(MUAYENE_PID_KEY, btn.dataset.id);
    refreshPage();
    return;
  }
  if (action === "view-dossier")
  {
    const store = getStore();
    const p = findPatient(store, btn.dataset.id);
    if (p)
    {
      toast("Hasta dosyası görüntülendi: " + p.name);
    }
    if (document.body.dataset.page === "muayene")
    {
      localStorage.setItem(MUAYENE_PID_KEY, btn.dataset.id);
      refreshPage();
    }
    return;
  }
  if (action === "close-muayene")
  {
    localStorage.removeItem(MUAYENE_PID_KEY);
    refreshPage();
    return;
  }
  if (action === "finish-muayene")
  {
    const store = getStore();
    const p = findPatient(store, btn.dataset.id);
    if (p && p.records && p.records.length > 0)
    {
      localStorage.removeItem(MUAYENE_PID_KEY);
      toast("Muayene tamamlandı.");
      refreshPage();
    }
    else
    {
      toast("Önce muayene notu ekleyin.", true);
    }
    return;
  }
  if (action === "calc-fee")
  {
    const store = getStore();
    const pay = store.payments.find(function (x)
    {
      return x.id === btn.dataset.pay;
    });
    const patient = pay && findPatient(store, pay.patientId);
    if (pay && patient)
    {
      const base = 200;
      const extra = (patient.records || []).length * 80 + (patient.treatments || []).length * 120;
      pay.amount = base + extra;
      saveStore(store);
      toast("Ücret hesaplandı: " + pay.amount + " TRY");
      refreshPage();
    }
    return;
  }
  if (action === "confirm-appt")
  {
    const store = getStore();
    const p = findPatient(store, btn.dataset.pid);
    const a = p && (p.appointments || []).find(function (x)
    {
      return x.id === btn.dataset.aid;
    });
    if (a)
    {
      a.status = "onaylı";
      saveStore(store);
      toast("Randevu onaylandı — doktor muayeneye alabilir.");
      refreshPage();
    }
    return;
  }
  if (action === "cancel-appt")
  {
    const store = getStore();
    const p = findPatient(store, btn.dataset.pid);
    if (p)
    {
      p.appointments = (p.appointments || []).filter(function (x)
      {
        return x.id !== btn.dataset.aid;
      });
      saveStore(store);
      toast("Randevu iptal edildi.");
      refreshPage();
    }
    return;
  }
  if (action === "query-insurance")
  {
    const store = getStore();
    const pay = store.payments.find(function (x)
    {
      return x.id === btn.dataset.pay;
    });
    const patient = pay && findPatient(store, pay.patientId);
    if (pay && patient)
    {
      pay.insuranceQuery = patient.insurance === "SGK" ? "SGK aktif" : "Özel sigorta";
      pay.tcknSent = btn.dataset.tckn;
      saveStore(store);
      toast("TCKN " + btn.dataset.tckn + " sunucuya gönderildi → " + pay.insuranceQuery);
      refreshPage();
    }
    return;
  }
  if (action === "apply-discount")
  {
    const store = getStore();
    const pay = store.payments.find(function (x)
    {
      return x.id === btn.dataset.pay;
    });
    if (pay && pay.insuranceQuery && pay.insuranceQuery !== "—")
    {
      pay.discount = pay.insuranceQuery.indexOf("SGK") >= 0 ? 0.2 : 0.1;
      saveStore(store);
      toast("İndirim uygulandı: %" + Math.round(pay.discount * 100));
      refreshPage();
    }
    else
    {
      toast("Önce sigorta sorgulayın.", true);
    }
    return;
  }
  if (action === "take-payment")
  {
    if (!isTestMode()) {
      fetch("http://localhost:8080/api/veznedar/randevu/1/odeme", {
        method: "POST",
        credentials: "include"
      }).then(res => {
        if (!res.ok) throw new Error("Ödeme alınamadı. (Randevu ID: 1 yetkisi veya durumu geçersiz olabilir)");
        toast("API üzerinden ödeme alındı.");
      }).catch(err => toast("Hata: " + err.message, true));
      return;
    }
    const store = getStore();
    const pay = store.payments.find(function (x)
    {
      return x.id === btn.dataset.pay;
    });
    if (pay && !pay.paid)
    {
      pay.paid = true;
      saveStore(store);
      toast("Ödeme alındı.");
      refreshPage();
    }
    return;
  }
});

document.addEventListener("change", function(e) {
  if (e.target.id === "test-mode-toggle") {
    localStorage.setItem(TEST_MODE_KEY, e.target.checked);
    toast("Test Mode " + (e.target.checked ? "ON" : "OFF"));
    refreshPage();
  }
});

document.addEventListener("submit", function (e)
{
  const form = e.target.closest("form[data-form]");
  if (!form)
  {
    return;
  }
  e.preventDefault();
  const kind = form.dataset.form;
  const fd = new FormData(form);
  if (kind === "login-backend")
  {
    const email = String(fd.get("email")).trim();
    const password = String(fd.get("password")).trim();
    
    fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ email: email, password: password }),
      credentials: "include"
    }).then(res => {
      if (!res.ok) throw new Error("Giriş başarısız");
      return res.json();
    }).then(data => {
      const roleMap = {
        'DOKTOR': 'DR',
        'KAYIT_GOREVLISI': 'KG',
        'RANDEVU_GOREVLISI': 'RG',
        'VEZNEDAR': 'VZ',
        'HASTA': 'HASTA'
      };
      const labelMap = {
        'DR': 'Doktor',
        'KG': 'Kayıt Görevlisi',
        'RG': 'Randevu Görevlisi',
        'VZ': 'Veznedar'
      };
      const role = roleMap[data.type] || data.type;
      const isPatient = data.type === 'HASTA';
      const sessionData = {
        kind: isPatient ? 'patient' : 'staff',
        role: role,
        email: email
      };
      if (isPatient) {
        sessionData.name = email;
      } else {
        sessionData.roleLabel = labelMap[role] || role;
      }
      setSession(sessionData);
      toast("Giriş başarılı.");
      window.location.href = (window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/")) ? "index.html" : "../index.html";
    }).catch(err => {
      toast("Giriş başarısız. Bilgileri kontrol edin.", true);
    });
    return;
  }
  if (kind === "login-staff")
  {
    if (fd.get("pin") !== STAFF_PIN)
    {
      toast("Hatalı şifre. Demo: 1234", true);
      return;
    }
    const role = fd.get("role");
    const labels = { KG: "Kayıt Görevlisi", RG: "Randevu Görevlisi", DR: "Doktor", VZ: "Veznedar" };
    setSession({ kind: "staff", role: role, roleLabel: labels[role] });
    toast("Giriş başarılı.");
    window.location.href = (window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/")) ? "index.html" : "../index.html";
    return;
  }
  if (kind === "login-patient")
  {
    const store = getStore();
    const tckn = String(fd.get("tckn")).trim();
    const p = store.patients.find(function (x)
    {
      return x.tckn === tckn;
    });
    if (!p)
    {
      toast("Hasta bulunamadı.", true);
      return;
    }
    setSession({ kind: "patient", patientId: p.id, name: p.name, tckn: p.tckn });
    toast("Hoş geldiniz, " + p.name);
    window.location.href = (window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/")) ? "pages/kayit.html" : "kayit.html";
    return;
  }
  if (kind === "register")
  {
    e.preventDefault();
    if (!hasRole(getSession(), ["KG"]))
    {
      toast("Kayıt için KG olarak giriş yapın.", true);
      return;
    }
    const tckn = String(fd.get("tckn")).trim();
    if (!isTestMode()) {
      const payload = {
        iletisimBilgisi: {
          isim: fd.get("isim"),
          soyisim: fd.get("soyisim"),
          tckno: tckn,
          telefon: fd.get("phone"),
          adres: fd.get("adres"),
          dogumTarihi: fd.get("birthDate")
        },
        boy: parseFloat(fd.get("boy")),
        kilo: parseFloat(fd.get("kilo")),
        email: fd.get("email"),
        password: fd.get("password")
      };
      fetch("http://localhost:8080/api/kayit/hasta", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        credentials: "include"
      }).then(res => {
        if (!res.ok) throw new Error("Kayıt başarısız");
        return res.json();
      }).then(data => {
        toast("Hasta kaydedildi: " + payload.iletisimBilgisi.isim);
        refreshPage();
      }).catch(err => {
        toast("Hata: " + err.message, true);
      });
      return;
    }
    const store = getStore();
    if (store.patients.some(function (x)
    {
      return x.tckn === tckn;
    }))
    {
      toast("Bu TCKN zaten kayıtlı.", true);
      return;
    }
    const p = {
      id: "P" + (store.patients.length + 101),
      name: fd.get("isim") + " " + fd.get("soyisim"),
      tckn: tckn,
      birthDate: fd.get("birthDate"),
      phone: fd.get("phone"),
      insurance: fd.get("insurance"),
      appointments: [],
      records: [],
      treatments: [],
      prescriptions: []
    };
    store.patients.push(p);
    store.payments.push({
      id: nextPayId(store),
      patientId: p.id,
      amount: 350,
      currency: "TRY",
      insuranceQuery: "—",
      discount: 0,
      paid: false
    });
    saveStore(store);
    form.reset();
    toast("Hasta kaydedildi: " + id);
    refreshPage();
    return;
  }
  if (kind === "appointment")
  {
    e.preventDefault();
    if (!hasRole(getSession(), ["RG"]))
    {
      toast("Randevu için RG olarak giriş yapın.", true);
      return;
    }
    const patientId = fd.get("patientId");
    const slotRaw = String(fd.get("slot"));
    if (!slotRaw || slotRaw.indexOf("|") < 0)
    {
      toast("Slot yok — Doktoru kontrol et, alternatif tarih öner.", true);
      return;
    }
    const parts = slotRaw.split("|");
    if (!isTestMode()) {
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      const dateStr = tomorrow.toISOString().split('T')[0];
      const payload = {
        hastaId: parseInt(patientId),
        doktorId: parseInt(parts[0]),
        randevuZamani: dateStr + "T" + parts[1] + ":00"
      };
      fetch("http://localhost:8080/api/randevu-gorevlisi/randevu", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        credentials: "include"
      }).then(res => {
        if (!res.ok) throw new Error("Müsait değil veya geçersiz tarih.");
        return res.json();
      }).then(data => {
        form.reset();
        localStorage.removeItem("rg-pick-doctor");
        toast("Randevu oluşturuldu.");
        refreshPage();
      }).catch(err => {
        toast("Hata: " + err.message, true);
      });
      return;
    }
    if (bookSlot(patientId, parts[0], parts[1], false))
    {
      form.reset();
      localStorage.removeItem("rg-pick-doctor");
      toast("Randevu oluşturuldu.");
      refreshPage();
    }
    return;
  }
  if (kind === "record")
  {
    e.preventDefault();
    if (!hasRole(getSession(), ["DR"]))
    {
      toast("Muayene için DR olarak giriş yapın.", true);
      return;
    }
    const patientId = fd.get("patientId");
    if (!isTestMode()) {
      fetch("http://localhost:8080/api/doktor/hasta/" + patientId + "/hastalik", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ detay: fd.get("note") }),
        credentials: "include"
      }).then(res => {
        if (!res.ok) throw new Error("Kayıt başarısız");
        form.reset();
        toast("Muayene kaydı eklendi.");
        refreshPage();
      }).catch(err => toast("Hata: " + err.message, true));
      return;
    }
    const store = getStore();
    const patient = findPatient(store, patientId);
    if (!patient) return;
    if (!patient.records) patient.records = [];
    patient.records.push({
      date: todayStr(),
      note: String(fd.get("note")).trim()
    });
    saveStore(store);
    form.reset();
    toast("Muayene kaydı eklendi.");
    refreshPage();
    return;
  }
  if (kind === "treatment")
  {
    e.preventDefault();
    if (!hasRole(getSession(), ["DR"]))
    {
      toast("DR olarak giriş yapın.", true);
      return;
    }
    if (!isTestMode()) {
      const activePid = localStorage.getItem(MUAYENE_PID_KEY);
      fetch("http://localhost:8080/api/doktor/randevu/" + activePid + "/tedavi", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tedaviTipi: "ILAC_TEDAVISI", aciklama: fd.get("name") + " - " + fd.get("dose") }),
        credentials: "include"
      }).then(res => {
        if (!res.ok) throw new Error("Tedavi eklenemedi");
        form.reset();
        toast("Tedavi eklendi.");
        refreshPage();
      }).catch(err => toast("Hata: " + err.message, true));
      return;
    }
    const store = getStore();
    const patient = findPatient(store, fd.get("patientId"));
    if (!patient) return;
    if (!patient.treatments) patient.treatments = [];
    patient.treatments.push({
      date: todayStr(),
      name: String(fd.get("name")).trim(),
      dose: String(fd.get("dose")).trim()
    });
    saveStore(store);
    form.reset();
    toast("Tedavi eklendi.");
    refreshPage();
    return;
  }
  if (kind === "prescription")
  {
    e.preventDefault();
    if (!hasRole(getSession(), ["DR"]))
    {
      toast("DR olarak giriş yapın.", true);
      return;
    }
    if (!isTestMode()) {
      const activePid = localStorage.getItem(MUAYENE_PID_KEY);
      fetch("http://localhost:8080/api/doktor/randevu/" + activePid + "/rapor", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ icerik: fd.get("type") + ": " + fd.get("text") }),
        credentials: "include"
      }).then(res => {
        if (!res.ok) throw new Error("Rapor eklenemedi");
        form.reset();
        toast("Rapor eklendi.");
        refreshPage();
      }).catch(err => toast("Hata: " + err.message, true));
      return;
    }
    const store = getStore();
    const patient = findPatient(store, fd.get("patientId"));
    if (!patient) return;
    if (!patient.prescriptions) patient.prescriptions = [];
    patient.prescriptions.push({
      date: todayStr(),
      type: String(fd.get("type")),
      text: String(fd.get("text")).trim()
    });
    saveStore(store);
    form.reset();
    toast("Rapor / reçete eklendi.");
    refreshPage();
    return;
  }
  if (kind === "update-patient")
  {
    if (!hasRole(getSession(), ["DR"]))
    {
      toast("DR olarak giriş yapın.", true);
      return;
    }
    const store = getStore();
    const patient = findPatient(store, fd.get("patientId"));
    if (!patient)
    {
      return;
    }
    patient.phone = String(fd.get("phone")).trim();
    patient.insurance = fd.get("insurance");
    saveStore(store);
    toast("Hasta bilgileri güncellendi.");
    refreshPage();
  }
});

}
