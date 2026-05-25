//
// Utkan Başurgan
//
//--------------------------------------------------------------------------------------------------------------------------------

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

function isValidTckn(tckn)
{
  if (!/^[1-9][0-9]{10}$/.test(tckn))
  {
    return false;
  }

  const total = tckn
    .slice(0, 10)
    .split("")
    .reduce(function (sum, digit)
    {
      return sum + Number(digit);
    }, 0);

  return total % 10 === Number(tckn[10]);
}

function requestErrorMessage(data, fallback)
{
  if (!data)
  {
    return fallback;
  }

  if (data.validationErrors && Object.keys(data.validationErrors).length > 0)
  {
    return Object.keys(data.validationErrors).map(function (key)
    {
      const value = data.validationErrors[key];
      const text = Array.isArray(value) ? value.join(", ") : value;

      return key + ": " + text;
    }).join(", ");
  }

  if (data.errors)
  {
    if (Array.isArray(data.errors))
    {
      return data.errors.join(", ");
    }

    return Object.values(data.errors).join(", ");
  }

  if (data.message)
  {
    return data.message;
  }

  if (data.detail)
  {
    return data.detail;
  }

  if (data.error)
  {
    return data.error;
  }

  if (data.title)
  {
    return data.title;
  }

  return fallback;
}

async function throwRequestError(res, fallback)
{
  if (res.ok)
  {
    return;
  }

  let message = fallback;
  if (!isTestMode() && (res.status === 401 || res.status === 403))
  {
    clearSession();
    message = "Sunucu oturumu geçersiz veya bu işlem için yetkiniz yok. Lütfen API ile tekrar giriş yapın.";
    throw new Error(message);
  }

  try
  {
    message = requestErrorMessage(await res.clone().json(), fallback);
  }
  catch(e)
  {
    try
    {
      const text = await res.text();
      message = text || fallback;
    }
    catch(innerError)
    {
    }
  }

  if (!message || message === fallback)
  {
    message = fallback + " (HTTP " + res.status + ")";
  }

  throw new Error(message);
}

function setFormMessage(form, message, isError)
{
  let el = form.querySelector("[data-form-message]");

  if (!message)
  {
    if (el)
    {
      el.remove();
    }

    return;
  }

  if (!el)
  {
    el = document.createElement("div");
    el.dataset.formMessage = "true";
    form.prepend(el);
  }

  el.textContent = message;
  el.style.cssText =
    "grid-column: 1 / -1; padding: 0.75rem 0.9rem; border-radius: 0.75rem; font-size: 0.92rem;";

  if (isError)
  {
    el.style.cssText += " background: #fef2f2; border: 1px solid #fecaca; color: #991b1b;";
  }
  else
  {
    el.style.cssText += " background: #ecfdf5; border: 1px solid #bbf7d0; color: #166534;";
  }
}

function clearFormMessage(form)
{
  setFormMessage(form, "", false);
}

function failForm(form, message)
{
  const text = message || "İşlem başarısız.";
  setFormMessage(form, text, true);
  toast("Hata: " + text, true);
}

const TEST_FIRST_NAMES = ["Ayşe", "Can", "Elif", "Mert", "Selin", "Bora", "Derya", "Eren", "Funda", "Gökhan", "Hale", "Kemal", "Leyla", "Murat", "Nil", "Pelin"];
const TEST_LAST_NAMES = ["Yılmaz", "Demir", "Arslan", "Şahin", "Korkmaz", "Çelik", "Aydın", "Yalçın", "Özkan", "Mutlu", "Kaplan", "Ergin", "Kaya", "Polat", "Işık", "Tan"];
const TEST_STREETS = ["Atatürk Mahallesi", "Cumhuriyet Caddesi", "Sağlık Sokak", "Barış Bulvarı", "Güneş Apartmanı", "Çınar Sitesi"];
const TEST_NOTES = ["Kontrol muayenesi yapıldı.", "Hasta takip planına alındı.", "Tetkik sonuçları değerlendirildi.", "Şikayetler azalmış, kontrol önerildi."];
const TEST_TREATMENTS = ["Fizik tedavi", "Tansiyon takibi", "Diyet programı", "Vitamin desteği", "Ağrı kontrolü", "Göz damlası"];
const TEST_DOSES = ["Günde 1 kez", "Günde 2 kez", "Sabah akşam", "10 gün", "2 hafta", "Kontrol amaçlı"];
const TEST_SPECIALS = ["!", "@", "#", "$", "%", "&", "*"];

function randomInt(min, max)
{
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomItem(items)
{
  return items[randomInt(0, items.length - 1)];
}

function randomDigits(length)
{
  let value = "";

  for (let i = 0; i < length; i++)
  {
    value += String(randomInt(0, 9));
  }

  return value;
}

function randomTckn()
{
  let firstTen = String(randomInt(1, 9)) + randomDigits(9);
  const total = firstTen.split("").reduce(function (sum, digit)
  {
    return sum + Number(digit);
  }, 0);

  return firstTen + String(total % 10);
}

function randomEmail(firstName, lastName)
{
  return normalizeText(firstName + "." + lastName).toLowerCase() +
    randomInt(100, 9999) + "@gmail.com";
}

function normalizeText(text)
{
  return String(text)
    .replace(/ç/g, "c")
    .replace(/Ç/g, "C")
    .replace(/ğ/g, "g")
    .replace(/Ğ/g, "G")
    .replace(/ı/g, "i")
    .replace(/I/g, "I")
    .replace(/İ/g, "I")
    .replace(/ö/g, "o")
    .replace(/Ö/g, "O")
    .replace(/ş/g, "s")
    .replace(/Ş/g, "S")
    .replace(/ü/g, "u")
    .replace(/Ü/g, "U")
    .replace(/[^A-Za-z0-9.]/g, "");
}

function randomPassword()
{
  return randomItem(["Test", "Hasta", "Personel", "Doktor"]) +
    randomInt(1000, 9999) +
    randomItem(TEST_SPECIALS) +
    randomItem(["ab", "xy", "mn"]);
}

function randomBirthDate()
{
  return randomInt(1960, 2005) + "-" +
    String(randomInt(1, 12)).padStart(2, "0") + "-" +
    String(randomInt(1, 28)).padStart(2, "0");
}

function randomPhone()
{
  return "05" + randomInt(30, 55) + " " +
    randomInt(100, 999) + " " +
    randomInt(10, 99) + " " +
    randomInt(10, 99);
}

function randomAddress()
{
  return randomItem(TEST_STREETS) + " No: " + randomInt(1, 120) + " Daire: " + randomInt(1, 24);
}

function randomProfile()
{
  const firstName = randomItem(TEST_FIRST_NAMES);
  const lastName = randomItem(TEST_LAST_NAMES);

  return {
    firstName: firstName,
    lastName: lastName,
    email: randomEmail(firstName, lastName),
    password: randomPassword(),
    tckn: randomTckn(),
    birthDate: randomBirthDate(),
    phone: randomPhone(),
    address: randomAddress(),
    height: randomInt(145, 195),
    weight: randomInt(45, 110)
  };
}

function setFieldValue(form, name, value)
{
  const field = form.querySelector("[name=" + name + "]");

  if (field && !field.disabled)
  {
    field.value = value;
    field.dispatchEvent(new Event("change", { bubbles: true }));
  }
}

function pickRandomSelectValue(select)
{
  const options = Array.prototype.slice.call(select.options).filter(function (option)
  {
    return option.value !== "";
  });

  if (options.length > 0)
  {
    select.value = randomItem(options).value;
    select.dispatchEvent(new Event("change", { bubbles: true }));
  }
}

function fillCommonProfileFields(form)
{
  const profile = randomProfile();

  setFieldValue(form, "isim", profile.firstName);
  setFieldValue(form, "soyisim", profile.lastName);
  setFieldValue(form, "email", profile.email);
  setFieldValue(form, "password", profile.password);
  setFieldValue(form, "tckn", profile.tckn);
  setFieldValue(form, "birthDate", profile.birthDate);
  setFieldValue(form, "phone", profile.phone);
  setFieldValue(form, "adres", profile.address);
  setFieldValue(form, "boy", profile.height);
  setFieldValue(form, "kilo", profile.weight);
}

function fillTestData(form)
{
  const kind = form.dataset.form;

  clearFormMessage(form);
  fillCommonProfileFields(form);

  form.querySelectorAll("select").forEach(function (select)
  {
    if (!select.disabled)
    {
      pickRandomSelectValue(select);
    }
  });

  if (kind === "login-staff")
  {
    setFieldValue(form, "pin", STAFF_PIN);
  }
  else if (kind === "login-patient")
  {
    const store = getStore();
    const patient = randomItem(store.patients);
    setFieldValue(form, "tckn", patient.tckn);
  }
  else if (kind === "login-backend")
  {
    const profile = randomProfile();
    setFieldValue(form, "email", profile.email);
    setFieldValue(form, "password", profile.password);
  }
  else if (kind === "record")
  {
    setFieldValue(form, "note", randomItem(TEST_NOTES));
  }
  else if (kind === "treatment")
  {
    setFieldValue(form, "name", randomItem(TEST_TREATMENTS));
    setFieldValue(form, "dose", randomItem(TEST_DOSES));
  }
  else if (kind === "prescription")
  {
    setFieldValue(form, "text", randomItem(TEST_NOTES) + " " + randomItem(TEST_TREATMENTS) + " önerildi.");
  }
  else if (kind === "update-patient")
  {
    setFieldValue(form, "phone", randomPhone());
  }

  setFormMessage(form, "Test verisi forma yüklendi.", false);
}

function addTestDataButtons()
{
  document.querySelectorAll("form[data-form]").forEach(function (form)
  {
    if (form.dataset.testDataButton === "true")
    {
      return;
    }

    const button = document.createElement("button");
    button.type = "button";
    button.className = "btn btn-secondary";
    button.dataset.action = "fill-test-data";
    button.textContent = "Test Verisi Yükle";

    const row = form.querySelector(".btn-row");
    if (row)
    {
      row.insertBefore(button, row.firstChild);
    }
    else
    {
      button.style.gridColumn = "1 / -1";
      form.appendChild(button);
    }

    form.dataset.testDataButton = "true";
  });
}

function setupTestDataButtons()
{
  addTestDataButtons();

  const observer = new MutationObserver(function ()
  {
    addTestDataButtons();
  });

  observer.observe(document.body, { childList: true, subtree: true });
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
  if (action === "fill-test-data")
  {
    const form = btn.closest("form[data-form]");
    if (form)
    {
      fillTestData(form);
    }
    return;
  }
  if (action === "logout")
  {
    if (!isTestMode()) {
      fetch(apiUrl("/api/auth/logout"), {
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
    setSession({ kind: "staff", role: role, roleLabel: labels[role] || role, source: "demo" });
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
    const examId = btn.dataset.id;
    const appointmentId = btn.dataset.aid || "";
    const previousExamId = localStorage.getItem(MUAYENE_PID_KEY);
    const previousAppointmentId = localStorage.getItem(MUAYENE_AID_KEY);

    if (isTestMode())
    {
      const store = getStore();

      if (previousExamId && (previousExamId !== examId || previousAppointmentId !== appointmentId))
      {
        updateAppointmentStatus(store, previousExamId, previousAppointmentId, "onaylı", "muayenede");
      }

      localStorage.setItem(MUAYENE_PID_KEY, examId);

      const activeAppointmentId = updateAppointmentStatus(store, examId, appointmentId, "muayenede", "onaylı");

      if (activeAppointmentId)
      {
        localStorage.setItem(MUAYENE_AID_KEY, activeAppointmentId);
      }
      else
      {
        localStorage.removeItem(MUAYENE_AID_KEY);
      }
    }
    else
    {
      localStorage.setItem(MUAYENE_PID_KEY, examId);
      localStorage.setItem(MUAYENE_AID_KEY, appointmentId || examId);
    }

    if (document.body.dataset.page === "doktor-panel")
    {
      window.location.href = "muayene.html";
      return;
    }
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
    if (isTestMode())
    {
      const store = getStore();
      const patientId = localStorage.getItem(MUAYENE_PID_KEY);
      const appointmentId = localStorage.getItem(MUAYENE_AID_KEY);

      if (patientId)
      {
        updateAppointmentStatus(store, patientId, appointmentId, "onaylı", "muayenede");
      }
    }

    localStorage.removeItem(MUAYENE_PID_KEY);
    localStorage.removeItem(MUAYENE_AID_KEY);
    refreshPage();
    return;
  }
  if (action === "finish-muayene")
  {
    if (!isTestMode())
    {
      const appointmentId = localStorage.getItem(MUAYENE_AID_KEY) ||
        localStorage.getItem(MUAYENE_PID_KEY) ||
        btn.dataset.id;

      rememberFinishedAppointment(appointmentId);
      localStorage.removeItem(MUAYENE_PID_KEY);
      localStorage.removeItem(MUAYENE_AID_KEY);
      toast("Muayene tamamlandı.");
      refreshPage();
      return;
    }

    const store = getStore();
    const p = findPatient(store, btn.dataset.id);
    if (p && p.records && p.records.length > 0)
    {
      const appointmentId = localStorage.getItem(MUAYENE_AID_KEY);

      updateAppointmentStatus(store, btn.dataset.id, appointmentId, "tamamlandı", "muayenede");
      localStorage.removeItem(MUAYENE_PID_KEY);
      localStorage.removeItem(MUAYENE_AID_KEY);
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
      fetch(apiUrl("/api/veznedar/randevu/1/odeme"), {
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

document.addEventListener("change", function (e)
{
  if (e.target.id === "test-mode-toggle")
  {
    const currentSession = getSession();
    const enabled = e.target.checked;
    localStorage.setItem(TEST_MODE_KEY, enabled);

    if (!enabled && currentSession && currentSession.source !== "api")
    {
      clearSession();
      toast("Test Mode OFF. Demo oturumu kapatıldı.");
    }
    else
    {
      toast("Test Mode " + (enabled ? "ON" : "OFF"));
    }

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
  clearFormMessage(form);
  if (kind === "login-backend")
  {
    const email = String(fd.get("email")).trim();
    const password = String(fd.get("password")).trim();
    
    fetch(apiUrl("/api/auth/login"), {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ email: email, password: password }),
      credentials: "include"
    }).then(async res => {
      await throwRequestError(res, "Giriş başarısız. Bilgileri kontrol edin.");
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
        email: email,
        source: "api"
      };
      if (isPatient)
      {
        sessionData.name = email;
      }
      else
      {
        sessionData.roleLabel = labelMap[role] || role;
        sessionData.staffId = data.id || data.personelId || data.staffId || data.doctorId || "";
        if (role === "DR" && typeof normalizeDoctorProfile === "function")
        {
          sessionData.staffProfile = normalizeDoctorProfile(data, sessionData);
        }
      }
      setSession(sessionData);
      toast("Giriş başarılı.");
      window.location.href = (window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/")) ? "index.html" : "../index.html";
    }).catch(err => {
      failForm(form, err.message);
    });
    return;
  }
  if (kind === "login-staff")
  {
    if (fd.get("pin") !== STAFF_PIN)
    {
      failForm(form, "Hatalı şifre. Demo: 1234");
      return;
    }
    const role = fd.get("role");
    const labels = { KG: "Kayıt Görevlisi", RG: "Randevu Görevlisi", DR: "Doktor", VZ: "Veznedar" };
    setSession({ kind: "staff", role: role, roleLabel: labels[role], source: "demo" });
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
      failForm(form, "Hasta bulunamadı.");
      return;
    }
    setSession({ kind: "patient", patientId: p.id, name: p.name, tckn: p.tckn, source: "demo" });
    toast("Hoş geldiniz, " + p.name);
    window.location.href = (window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/")) ? "pages/profil.html" : "profil.html";
  }
  if (kind === "register-staff")
  {
    e.preventDefault();
    if (isTestMode())
    {
       failForm(form, "Personel kaydı sadece API açıkken çalışır.");
       return;
    }
    const roleType = fd.get("roleType");
    let endpoint = "";
    if (roleType === "KG") endpoint = apiUrl("/api/kayit/register");
    else if (roleType === "RG") endpoint = apiUrl("/api/randevu-gorevlisi/register");
    else if (roleType === "VZ") endpoint = apiUrl("/api/veznedar/register");
    else if (roleType === "DR") endpoint = apiUrl("/api/doktor/register");

    const tckn = String(fd.get("tckn")).trim();
    if (!isValidTckn(tckn))
    {
      failForm(form, "Geçerli TCKN girin. Örnek: 10000000001");
      return;
    }

    const contactInformation = {
      isim: String(fd.get("isim")).trim(),
      soyisim: String(fd.get("soyisim")).trim(),
      tckno: tckn,
      telefon: String(fd.get("phone")).trim(),
      adres: String(fd.get("adres")).trim(),
      doğumTarihi: fd.get("birthDate")
    };

    const payload = {
      email: fd.get("email"),
      password: fd.get("password")
    };

    if (roleType === "RG" || roleType === "VZ")
    {
      payload.contactInformation = contactInformation;
    }
    else
    {
      payload.iletisimBilgisi = contactInformation;
    }

    if (roleType === "DR")
    {
      payload.bolum = fd.get("bolum");
      payload.unvan = fd.get("unvan");
    }

    fetch(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    }).then(async res => {
      if (!res.ok)
      {
         await throwRequestError(res, "Personel kaydı başarısız");
      }
      toast("Personel başarıyla kaydedildi! Şimdi giriş yapabilirsiniz.");
      setFormMessage(form, "Personel başarıyla kaydedildi. Şimdi giriş yapabilirsiniz.", false);
      form.reset();
    }).catch(err => {
      failForm(form, err.message);
    });
    return;
  }
  if (kind === "register")
  {
    e.preventDefault();
    if (!hasRole(getSession(), ["KG"]))
    {
      failForm(form, "Kayıt için KG olarak giriş yapın.");
      return;
    }
    const tckn = String(fd.get("tckn")).trim();
    if (!isValidTckn(tckn))
    {
      failForm(form, "Geçerli TCKN girin. Örnek: 10000000001");
      return;
    }

    if (!isTestMode())
    {
      const payload = {
        iletisimBilgisi: {
          isim: fd.get("isim"),
          soyisim: fd.get("soyisim"),
          tckno: tckn,
          telefon: fd.get("phone"),
          adres: fd.get("adres"),
          doğumTarihi: fd.get("birthDate")
        },
        boy: parseFloat(fd.get("boy")),
        kilo: parseFloat(fd.get("kilo")),
        email: fd.get("email"),
        password: fd.get("password")
      };
      fetch(apiUrl("/api/kayit/hasta"), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        credentials: "include"
      }).then(async res => {
        if (!res.ok)
        {
           await throwRequestError(res, "Hasta kaydı başarısız");
        }
        return res.json();
      }).then(data => {
        toast("Hasta kaydedildi: " + payload.iletisimBilgisi.isim);
        setFormMessage(form, "Hasta başarıyla kaydedildi.", false);
        refreshPage();
      }).catch(err => {
        failForm(form, err.message);
      });
      return;
    }
    const store = getStore();
    if (store.patients.some(function (x)
    {
      return x.tckn === tckn;
    }))
    {
      failForm(form, "Bu TCKN zaten kayıtlı.");
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
    toast("Hasta kaydedildi: " + p.id);
    refreshPage();
    return;
  }
  if (kind === "appointment")
  {
    e.preventDefault();
    if (!hasRole(getSession(), ["RG"]))
    {
      failForm(form, "Randevu için RG olarak giriş yapın.");
      return;
    }
    const patientId = fd.get("patientId");
    const slotRaw = String(fd.get("slot"));
    if (!slotRaw || slotRaw.indexOf("|") < 0)
    {
      failForm(form, "Slot yok — Doktoru kontrol et, alternatif tarih öner.");
      return;
    }
    const parts = slotRaw.split("|");
    if (!isTestMode())
    {
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      const dateStr = tomorrow.toISOString().split('T')[0];
      const payload = {
        hastaId: parseInt(patientId),
        doktorId: parseInt(parts[0]),
        randevuZamani: dateStr + "T" + parts[1] + ":00"
      };
      fetch(apiUrl("/api/randevu-gorevlisi/randevu"), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        credentials: "include"
      }).then(async res => {
        await throwRequestError(res, "Randevu oluşturulamadı.");
        return res.json();
      }).then(data => {
        form.reset();
        localStorage.removeItem("rg-pick-doctor");
        toast("Randevu oluşturuldu.");
        refreshPage();
      }).catch(err => {
        failForm(form, err.message);
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
      failForm(form, "Muayene için DR olarak giriş yapın.");
      return;
    }
    const patientId = fd.get("patientId");
    if (!isTestMode())
    {
      fetch(apiUrl("/api/doktor/hasta/" + patientId + "/hastalik"), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ detay: fd.get("note") }),
        credentials: "include"
      }).then(async res => {
        await throwRequestError(res, "Muayene kaydı eklenemedi.");
        form.reset();
        toast("Muayene kaydı eklendi.");
        refreshPage();
      }).catch(err => failForm(form, err.message));
      return;
    }
    const store = getStore();
    const patient = findPatient(store, patientId);
    if (!patient)
    {
      failForm(form, "Hasta bulunamadı.");
      return;
    }
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
      failForm(form, "DR olarak giriş yapın.");
      return;
    }
    if (!isTestMode())
    {
      const activePid = localStorage.getItem(MUAYENE_PID_KEY);
      fetch(apiUrl("/api/doktor/randevu/" + activePid + "/tedavi"), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tedaviTipi: "ILAC_TEDAVISI", aciklama: fd.get("name") + " - " + fd.get("dose") }),
        credentials: "include"
      }).then(async res => {
        await throwRequestError(res, "Tedavi eklenemedi.");
        form.reset();
        toast("Tedavi eklendi.");
        refreshPage();
      }).catch(err => failForm(form, err.message));
      return;
    }
    const store = getStore();
    const patient = findPatient(store, fd.get("patientId"));
    if (!patient)
    {
      failForm(form, "Hasta bulunamadı.");
      return;
    }
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
      failForm(form, "DR olarak giriş yapın.");
      return;
    }
    if (!isTestMode())
    {
      const activePid = localStorage.getItem(MUAYENE_PID_KEY);
      fetch(apiUrl("/api/doktor/randevu/" + activePid + "/rapor"), {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ icerik: fd.get("type") + ": " + fd.get("text") }),
        credentials: "include"
      }).then(async res => {
        await throwRequestError(res, "Rapor eklenemedi.");
        form.reset();
        toast("Rapor eklendi.");
        refreshPage();
      }).catch(err => failForm(form, err.message));
      return;
    }
    const store = getStore();
    const patient = findPatient(store, fd.get("patientId"));
    if (!patient)
    {
      failForm(form, "Hasta bulunamadı.");
      return;
    }
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
      failForm(form, "DR olarak giriş yapın.");
      return;
    }
    const store = getStore();
    const patient = findPatient(store, fd.get("patientId"));
    if (!patient)
    {
      failForm(form, "Hasta bulunamadı.");
      return;
    }
    patient.phone = String(fd.get("phone")).trim();
    patient.insurance = fd.get("insurance");
    saveStore(store);
    toast("Hasta bilgileri güncellendi.");
    refreshPage();
  }
});

setupTestDataButtons();

}

//--------------------------------------------------------------------------------------------------------------------------------