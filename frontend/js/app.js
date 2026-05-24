//
// Utkan Başurgan
//
//--------------------------------------------------------------------------------------------------------------------------------

const STORE_KEY = "hospital-store-v1";
const SESSION_KEY = "hospital-session-v1";
const PROGRESS_KEY = "hospital-progress-v1";
const MUAYENE_PID_KEY = "hospital-muayene-pid";
const STAFF_PIN = "1234";

const DEFAULT_PROGRESS = {
  kayit: true,
  rezervasyon: true,
  rezervasyonAlt: false,
  muayene: false,
  muayeneView: true,
  muayeneTreatment: false,
  muayeneRx: false,
  muayeneManage: false,
  odeme: false,
  odemeInsurance: false,
  odemeDiscount: false
};

function loadJson(path)
{
  const key = path.replace(/^\.\//, "");
  if (window.HOSPITAL_DATA && window.HOSPITAL_DATA[key])
  {
    return Promise.resolve(window.HOSPITAL_DATA[key]);
  }
  return fetch(path).then(function (res)
  {
    if (!res.ok)
    {
      throw new Error("JSON yüklenemedi: " + path);
    }
    return res.json();
  });
}

function esc(s)
{
  if (s === null || s === undefined)
  {
    return "";
  }
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function normalizeStore(store)
{
  store.patients.forEach(function (p)
  {
    if (!p.appointments)
    {
      p.appointments = [];
    }
    if (!p.records)
    {
      p.records = [];
    }
    if (!p.treatments)
    {
      p.treatments = [];
    }
    if (!p.prescriptions)
    {
      p.prescriptions = [];
    }
  });
  store.doctors.forEach(function (d)
  {
    if (!d.availableSlots)
    {
      d.availableSlots = [];
    }
    if (!d.alternativeSlots)
    {
      d.alternativeSlots = [];
    }
  });
  return store;
}

function getStore()
{
  const raw = localStorage.getItem(STORE_KEY);
  if (raw)
  {
    return normalizeStore(JSON.parse(raw));
  }
  const base = normalizeStore(JSON.parse(
    JSON.stringify(window.HOSPITAL_DATA["data/patients.json"])
  ));
  saveStore(base);
  return base;
}

function saveStore(store)
{
  localStorage.setItem(STORE_KEY, JSON.stringify(store));
}

function resetStore()
{
  localStorage.removeItem(STORE_KEY);
  localStorage.removeItem(PROGRESS_KEY);
  localStorage.removeItem(MUAYENE_PID_KEY);
  getStore();
}

function getProgress()
{
  const raw = localStorage.getItem(PROGRESS_KEY);
  if (!raw)
  {
    return Object.assign({}, DEFAULT_PROGRESS);
  }
  return Object.assign({}, DEFAULT_PROGRESS, JSON.parse(raw));
}

function setProgress(key, value)
{
  const p = getProgress();
  p[key] = value;
  localStorage.setItem(PROGRESS_KEY, JSON.stringify(p));
}

function markProgress(key)
{
  setProgress(key, true);
}

async function loadProjectsMerged()
{
  const data = JSON.parse(JSON.stringify(
    await loadJson("data/projects.json")
  ));
  const p = getProgress();
  data.useCases.forEach(function (uc)
  {
    if (uc.slug === "kayit")
    {
      uc.done = p.kayit;
    }
    if (uc.slug === "rezervasyon")
    {
      uc.done = p.rezervasyon;
      if (uc.subCases[0])
      {
        uc.subCases[0].done = p.rezervasyonAlt;
      }
    }
    if (uc.slug === "muayene")
    {
      uc.done = p.muayene;
      if (uc.subCases[0])
      {
        uc.subCases[0].done = p.muayeneView;
      }
      if (uc.subCases[1])
      {
        uc.subCases[1].done = p.muayeneTreatment;
      }
      if (uc.subCases[2])
      {
        uc.subCases[2].done = p.muayeneRx;
      }
      if (uc.subCases[3])
      {
        uc.subCases[3].done = p.muayeneManage;
      }
    }
    if (uc.slug === "odeme")
    {
      uc.done = p.odeme;
      if (uc.subCases[0])
      {
        uc.subCases[0].done = p.odemeInsurance;
      }
      if (uc.subCases[1])
      {
        uc.subCases[1].done = p.odemeDiscount;
      }
    }
  });
  return data;
}

function checkMark(done)
{
  return done ? "[x]" : "[ ]";
}

function renderChecklist()
{
  const el = document.getElementById("use-case-checklist");
  if (!el)
  {
    return;
  }
  const p = getProgress();
  const rows = [
    { done: p.kayit, text: "Hasta Sisteme Kayıt Edilir – KG, hasta kaydeder; hasta login olup kendi bilgilerini görür" },
    { done: p.rezervasyon, text: "Rezervasyon Alınması – RG, doktor seçip randevu oluşturur; hasta randevusunu görür" },
    { done: p.rezervasyonAlt, text: "«extend» Alternatif Tarih Önerilmesi – Uygun doktor yoksa RG alternatif tarih önerir", sub: true },
    { done: p.muayene, text: "Muayene – Doktor, randevuya gelen hastanın bilgilerini görür ve muayene yapar" },
    { done: p.muayeneView, text: "«include» Hasta Bilgilerinin Görüntülenmesi – Doktor hastanın tüm kaydını görür", sub: true },
    { done: p.muayeneTreatment, text: "«extend» Hasta Kaydına Tedavi Ekleme – Doktor tedaviyi hasta kaydına ekler", sub: true },
    { done: p.muayeneRx, text: "«extend» Rapor/Reçete Verilmesi – Doktor rapor veya reçete yazar", sub: true },
    { done: p.muayeneManage, text: "«extend» Hasta Bilgilerinin Yönetimi – Doktor hasta bilgilerini günceller", sub: true },
    { done: p.odeme, text: "Ödeme İşleminin Yapılması – Veznedar ücret hesaplar, ödeme alır" },
    { done: p.odemeInsurance, text: "«include» Sigorta Durumunun Sorgulanması – Sosyal sigorta sunucusuna TCKN gönderilir", sub: true },
    { done: p.odemeDiscount, text: "«extend» İndirim Uygulanır – Sigorta durumuna göre indirim uygulanır", sub: true }
  ];
  const items = rows.map(function (row)
  {
    const cls = (row.sub ? "sub " : "") + (row.done ? "done" : "");
    return (
      '<li class="' + cls + '">' +
      '<span class="chk">' + checkMark(row.done) + "</span> " +
      esc(row.text) +
      "</li>"
    );
  }).join("");
  el.innerHTML =
    "<h2>Ana Use Case'ler</h2>" +
    '<ul class="uc-checklist">' + items + "</ul>";
}

function getSession()
{
  const raw = localStorage.getItem(SESSION_KEY);
  return raw ? JSON.parse(raw) : null;
}

function setSession(session)
{
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

function clearSession()
{
  localStorage.removeItem(SESSION_KEY);
}

function toast(msg, isError)
{
  const old = document.querySelector(".toast");
  if (old)
  {
    old.remove();
  }
  const el = document.createElement("div");
  el.className = "toast" + (isError ? " error" : "");
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(function ()
  {
    el.remove();
  }, 2800);
}

function todayStr()
{
  const d = new Date();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return d.getFullYear() + "-" + m + "-" + day;
}

function nextPatientId(store)
{
  let max = 1000;
  store.patients.forEach(function (p)
  {
    const n = parseInt(p.id.replace("P-", ""), 10);
    if (!isNaN(n) && n > max)
    {
      max = n;
    }
  });
  return "P-" + (max + 1);
}

function nextApptId(store)
{
  let max = 500;
  store.patients.forEach(function (p)
  {
    (p.appointments || []).forEach(function (a)
    {
      const n = parseInt(String(a.id).replace("R-", ""), 10);
      if (!isNaN(n) && n > max)
      {
        max = n;
      }
    });
  });
  return "R-" + (max + 1);
}

function nextPayId(store)
{
  let max = 800;
  store.payments.forEach(function (pay)
  {
    const n = parseInt(String(pay.id).replace("PY-", ""), 10);
    if (!isNaN(n) && n > max)
    {
      max = n;
    }
  });
  return "PY-" + (max + 1);
}

function findPatient(store, id)
{
  return store.patients.find(function (p)
  {
    return p.id === id;
  });
}

function sessionLabel(session)
{
  if (!session)
  {
    return "";
  }
  if (session.kind === "patient")
  {
    return session.name + " (Hasta)";
  }
  return session.roleLabel + " (" + session.role + ")";
}

function hasRole(session, roles)
{
  if (!session)
  {
    return false;
  }
  if (session.kind === "patient")
  {
    return roles.indexOf("patient") >= 0;
  }
  return roles.indexOf(session.role) >= 0;
}

function renderSessionBar()
{
  const el = document.getElementById("session-bar");
  if (!el)
  {
    return;
  }
  const session = getSession();
  if (!session)
  {
    el.innerHTML =
      '<span>Giriş yapılmadı — işlemler için oturum açın.</span>' +
      '<a href="giris.html" class="btn btn-primary btn-sm">Giriş yap</a>';
    return;
  }
  el.innerHTML =
    "<span>Oturum: <strong>" + esc(sessionLabel(session)) + "</strong></span>" +
    '<div class="btn-row" style="margin:0">' +
    '<button type="button" class="btn btn-ghost btn-sm" data-action="logout">Çıkış</button>' +
    "</div>";
}

function badge(done)
{
  const cls = done ? "done" : "pending";
  const label = done ? "Tamamlandı" : "Bekliyor";
  return '<span class="badge ' + cls + '">' + label + "</span>";
}

function relationTag(relation)
{
  if (!relation)
  {
    return "";
  }
  const label = relation === "extend" ? "extend" : "include";
  return '<span class="relation-tag ' + relation + '">«' + label + "»</span>";
}

function renderSubCases(subCases)
{
  if (!subCases || subCases.length === 0)
  {
    return "";
  }
  const items = subCases.map(function (sub)
  {
    const rel = sub.relation || "";
    return (
      '<li class="' + rel + '">' +
      relationTag(rel) +
      badge(sub.done) +
      " <strong>" + esc(sub.title) + "</strong>" +
      '<p style="margin:0.35rem 0 0;color:var(--muted);font-size:0.9rem">' +
      esc(sub.description) +
      "</p></li>"
    );
  }).join("");
  return (
    '<h3>Alt use case\'ler</h3><ul class="sub-list">' + items + "</ul>"
  );
}

function roleHint(needed, session)
{
  if (hasRole(session, needed))
  {
    return "";
  }
  return (
    '<p class="hint">Bu işlem için ' + esc(needed.join(" veya ")) +
    " olarak giriş yapın.</p>"
  );
}

async function renderUseCasePage(slug)
{
  const data = await loadProjectsMerged();
  const uc = data.useCases.find(function (item)
  {
    return item.slug === slug;
  });
  const root = document.getElementById("use-case-detail");
  if (!uc || !root)
  {
    return;
  }
  document.title = uc.title + " — " + data.site.title;
  const heading = document.getElementById("page-heading");
  if (heading)
  {
    heading.textContent = uc.title;
  }
  root.innerHTML =
    '<div class="panel">' +
    badge(uc.done) +
    '<span class="badge role">' + esc(uc.role) + " · " + esc(uc.roleLabel) + "</span>" +
    '<p style="margin-top:1rem">' + esc(uc.description) + "</p>" +
    renderSubCases(uc.subCases) +
    "</div>";
}

async function renderIndex()
{
  const data = await loadProjectsMerged();
  const grid = document.getElementById("use-case-grid");
  const title = document.getElementById("site-title");
  const subtitle = document.getElementById("site-subtitle");
  if (title)
  {
    title.textContent = data.site.title;
  }
  if (subtitle)
  {
    subtitle.textContent = data.site.subtitle;
  }
  renderSessionBar();
  if (!grid)
  {
    return;
  }
  grid.innerHTML = data.useCases.map(function (uc)
  {
    return (
      '<article class="card">' +
      "<h2>" + esc(uc.title) + "</h2>" +
      badge(uc.done) +
      '<span class="badge role">' + esc(uc.role) + " · " + esc(uc.roleLabel) + "</span>" +
      "<p>" + esc(uc.description) + "</p>" +
      '<div class="btn-row">' +
      '<a class="btn btn-primary btn-sm" href="' + esc(uc.page) + '">Sayfaya git</a>' +
      '<button type="button" class="btn btn-secondary btn-sm" data-action="demo-login" data-role="' +
      esc(uc.role) + '">' + esc(uc.role) + " olarak giriş</button>" +
      "</div>" +
      "</article>"
    );
  }).join("");
}

function renderPatientsTableRows(patients, session)
{
  if (patients.length === 0)
  {
    return '<p class="empty">Kayıt bulunamadı.</p>';
  }
  const rows = patients.map(function (p)
  {
    const appt = p.appointments && p.appointments.length > 0
      ? p.appointments[0].date + " " + p.appointments[0].time
      : "—";
    return (
      "<tr>" +
      "<td>" + esc(p.id) + "</td>" +
      "<td>" + esc(p.name) + "</td>" +
      "<td>" + esc(p.tckn) + "</td>" +
      "<td>" + esc(p.insurance) + "</td>" +
      "<td>" + esc(appt) + "</td>" +
      '<td><button type="button" class="btn btn-secondary btn-sm" data-action="view-patient" data-id="' +
      esc(p.id) + '">Detay</button></td>' +
      "</tr>"
    );
  }).join("");
  return (
    '<table class="data-table"><thead><tr>' +
    "<th>ID</th><th>Ad</th><th>TCKN</th><th>Sigorta</th><th>Randevu</th><th></th>" +
    "</tr></thead><tbody>" + rows + "</tbody></table>"
  );
}

async function renderKayit()
{
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  await renderUseCasePage("kayit");
  const el = document.getElementById("dummy-data");
  if (!el)
  {
    return;
  }
  let list = store.patients;
  if (session && session.kind === "patient")
  {
    list = list.filter(function (p)
    {
      return p.id === session.patientId;
    });
  }
  const canRegister = hasRole(session, ["KG"]);
  el.innerHTML =
    roleHint(["KG"], session) +
    '<div class="panel"><h2>Yeni hasta kaydı</h2>' +
  '<form class="form-grid two-col" data-form="register">' +
    '<label class="field"><span>Ad soyad</span><input name="name" required' +
    (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>TCKN</span><input name="tckn" required maxlength="11"' +
    (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Doğum tarihi</span><input type="date" name="birthDate" required' +
    (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Telefon</span><input name="phone" required' +
    (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Sigorta</span><select name="insurance"' +
    (canRegister ? "" : " disabled") + '>' +
    '<option value="SGK">SGK</option><option value="özel">Özel</option></select></label>' +
    '<div class="btn-row" style="align-self:end">' +
    '<button type="submit" class="btn btn-primary"' +
    (canRegister ? "" : " disabled") + ">Kaydet</button>" +
    "</div></form></div>" +
    '<div class="panel"><h2>Kayıtlı hastalar</h2>' +
    '<div id="patient-detail"></div>' +
    renderPatientsTableRows(list, session) +
    "</div>";
}

function buildSlotOptions(store, doctorId)
{
  let html = "";
  store.doctors.forEach(function (d)
  {
    if (doctorId && d.id !== doctorId)
    {
      return;
    }
    d.availableSlots.forEach(function (slot)
    {
      html +=
        '<option value="' + esc(d.id) + "|" + esc(slot) + '">' +
        esc(d.name) + " — " + esc(slot) + "</option>";
    });
  });
  return html;
}

function buildAltPanel(store, doctorId, canBook)
{
  const doctor = store.doctors.find(function (d)
  {
    return d.id === doctorId;
  });
  if (!doctor || !doctor.alternativeSlots || doctor.alternativeSlots.length === 0)
  {
    return "";
  }
  let btns = "";
  doctor.alternativeSlots.forEach(function (slot)
  {
    btns +=
      '<button type="button" class="btn btn-secondary btn-sm" data-action="book-alt" data-did="' +
      esc(doctor.id) + '" data-slot="' + esc(slot) + '"' +
      (canBook ? "" : " disabled") + ">Öner: " + esc(slot) + "</button>";
  });
  return (
    '<div class="panel" id="alt-dates-panel" style="background:#faf8ff">' +
    "<h3>«extend» Alternatif Tarih Önerilmesi</h3>" +
    '<p class="hint">' + esc(doctor.name) + " için müsait slot yok. Alternatif tarihler:</p>" +
    '<div class="btn-row">' + btns + "</div></div>"
  );
}

async function renderRezervasyon()
{
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  await renderUseCasePage("rezervasyon");
  const el = document.getElementById("dummy-data");
  if (!el)
  {
    return;
  }
  const canBook = hasRole(session, ["RG"]);
  const pickDoctor = localStorage.getItem("rg-pick-doctor") || "";
  let patientOptions = store.patients.map(function (p)
  {
    return '<option value="' + esc(p.id) + '">' + esc(p.name) + "</option>";
  }).join("");
  let doctorOptions = store.doctors.map(function (d)
  {
    const sel = d.id === pickDoctor ? " selected" : "";
    const free = d.availableSlots.length;
    return (
      '<option value="' + esc(d.id) + '"' + sel + ">" +
      esc(d.name) + " — " + esc(d.department) +
      " (" + free + " slot)</option>"
    );
  }).join("");
  const slotOptions = buildSlotOptions(store, pickDoctor || null);
  const picked = store.doctors.find(function (d)
  {
    return d.id === pickDoctor;
  });
  const showAlt = picked && picked.availableSlots.length === 0;
  let apptHtml = "";
  store.patients.forEach(function (p)
  {
    if (session && session.kind === "patient" && p.id !== session.patientId)
    {
      return;
    }
    (p.appointments || []).forEach(function (a)
    {
      const actions = canBook
        ? '<button type="button" class="btn btn-primary btn-sm" data-action="confirm-appt" data-pid="' +
          esc(p.id) + '" data-aid="' + esc(a.id) + '">Onayla</button>' +
          '<button type="button" class="btn btn-danger btn-sm" data-action="cancel-appt" data-pid="' +
          esc(p.id) + '" data-aid="' + esc(a.id) + '">İptal</button>'
        : "";
      apptHtml +=
        '<div class="panel" style="padding:1rem">' +
        "<p><strong>" + esc(p.name) + "</strong> — " + esc(a.doctor) +
        " (" + esc(a.department) + ") — " + esc(a.date) + " " + esc(a.time) +
        ' — <span class="badge ' + (a.status === "onaylı" ? "done" : "pending") + '">' +
        esc(a.status) + "</span></p>" +
        '<div class="btn-row">' + actions + "</div></div>";
    });
  });
  if (!apptHtml)
  {
    apptHtml = '<p class="empty">Randevu yok.</p>';
  }
  el.innerHTML =
    roleHint(["RG"], session) +
    '<div class="panel"><h2>Randevu oluştur</h2>' +
    '<form class="form-grid two-col" data-form="appointment" id="appt-form">' +
    '<label class="field"><span>Hasta</span><select name="patientId" required' +
    (canBook ? "" : " disabled") + ">" + patientOptions + "</select></label>" +
    '<label class="field"><span>Doktor</span><select name="doctorId" id="appt-doctor" required' +
    (canBook ? "" : " disabled") + ">" + doctorOptions + "</select></label>" +
    '<label class="field"><span>Müsait slot</span><select name="slot" id="appt-slot" required' +
    (canBook ? "" : " disabled") + ">" +
    (slotOptions || '<option value="">— Slot yok —</option>') +
    "</select></label>" +
    '<div class="btn-row" style="align-self:end">' +
    '<button type="submit" class="btn btn-primary"' +
    (canBook && slotOptions ? "" : " disabled") + ">Randevu al</button>" +
    '<button type="button" class="btn btn-secondary" data-action="pick-doctor"' +
    (canBook ? "" : " disabled") + ">Doktoru kontrol et</button>" +
    "</div></form></div>" +
    (showAlt ? buildAltPanel(store, pickDoctor, canBook) : "") +
    '<div class="panel"><h2>Randevular</h2>' + apptHtml + "</div>";
}

function patientsWithConfirmedAppt(store)
{
  return store.patients.filter(function (p)
  {
    return (p.appointments || []).some(function (a)
    {
      return a.status === "onaylı";
    });
  });
}

function renderPatientDossier(p)
{
  let appts = "";
  (p.appointments || []).forEach(function (a)
  {
    appts += "<li>" + esc(a.date) + " " + esc(a.time) + " — " + esc(a.doctor) + " (" + esc(a.status) + ")</li>";
  });
  let recs = "";
  (p.records || []).forEach(function (r)
  {
    recs += "<li>" + esc(r.date) + " — " + esc(r.note) + "</li>";
  });
  let treats = "";
  (p.treatments || []).forEach(function (t)
  {
    treats += "<li>" + esc(t.date) + " — " + esc(t.name) + " (" + esc(t.dose) + ")</li>";
  });
  let rx = "";
  (p.prescriptions || []).forEach(function (r)
  {
    rx += "<li>" + esc(r.date) + " — " + esc(r.type) + ": " + esc(r.text) + "</li>";
  });
  return (
    '<div class="panel" style="background:var(--accent-light)">' +
    "<h3>«include» Hasta dosyası — " + esc(p.name) + "</h3>" +
    "<p><strong>TCKN:</strong> " + esc(p.tckn) + " · <strong>Tel:</strong> " + esc(p.phone) +
    " · <strong>Sigorta:</strong> " + esc(p.insurance) + " · <strong>Doğum:</strong> " + esc(p.birthDate) + "</p>" +
    "<h4>Randevular</h4><ul>" + (appts || "<li>—</li>") + "</ul>" +
    "<h4>Muayene notları</h4><ul>" + (recs || "<li>—</li>") + "</ul>" +
    "<h4>Tedaviler</h4><ul>" + (treats || "<li>—</li>") + "</ul>" +
    "<h4>Rapor / reçete</h4><ul>" + (rx || "<li>—</li>") + "</ul>" +
    "</div>"
  );
}

async function renderMuayene()
{
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  await renderUseCasePage("muayene");
  const el = document.getElementById("dummy-data");
  if (!el)
  {
    return;
  }
  const canExam = hasRole(session, ["DR"]);
  const activePid = localStorage.getItem(MUAYENE_PID_KEY) || "";
  const active = activePid ? findPatient(store, activePid) : null;
  const waiting = patientsWithConfirmedAppt(store);
  let queueHtml = "";
  waiting.forEach(function (p)
  {
    queueHtml +=
      '<div class="btn-row" style="margin-bottom:0.5rem">' +
      "<span><strong>" + esc(p.name) + "</strong> — onaylı randevu</span>" +
      '<button type="button" class="btn btn-primary btn-sm" data-action="open-muayene" data-id="' +
      esc(p.id) + '"' + (canExam ? "" : " disabled") + ">Muayeneye al</button>" +
      '<button type="button" class="btn btn-secondary btn-sm" data-action="view-dossier" data-id="' +
      esc(p.id) + '">Dosyayı gör</button>' +
      "</div>";
  });
  if (!queueHtml)
  {
    queueHtml = '<p class="empty">Onaylı randevulu hasta yok (RG randevuyu onaylamalı).</p>';
  }
  let workspace = "";
  if (active)
  {
    workspace =
      '<div class="muayene-workspace">' +
      renderPatientDossier(active) +
      '<div class="panel"><h3>Muayene notu</h3>' +
      '<form data-form="record">' +
      '<input type="hidden" name="patientId" value="' + esc(active.id) + '">' +
      '<label class="field"><span>Not</span><textarea name="note" required' +
      (canExam ? "" : " disabled") + '></textarea></label>' +
      '<button type="submit" class="btn btn-primary"' +
      (canExam ? "" : " disabled") + ">Not ekle</button></form></div>" +
      '<div class="panel"><h3>«extend» Tedavi ekle</h3>' +
      '<form data-form="treatment" class="form-grid two-col">' +
      '<input type="hidden" name="patientId" value="' + esc(active.id) + '">' +
      '<label class="field"><span>İlaç / tedavi</span><input name="name" required' +
      (canExam ? "" : " disabled") + '></label>' +
      '<label class="field"><span>Doz</span><input name="dose" required' +
      (canExam ? "" : " disabled") + '></label>' +
      '<div class="btn-row"><button type="submit" class="btn btn-secondary"' +
      (canExam ? "" : " disabled") + ">Tedavi ekle</button></div></form></div>" +
      '<div class="panel"><h3>«extend» Rapor / reçete</h3>' +
      '<form data-form="prescription">' +
      '<input type="hidden" name="patientId" value="' + esc(active.id) + '">' +
      '<label class="field"><span>Tür</span><select name="type"' +
      (canExam ? "" : " disabled") + '><option value="rapor">Rapor</option><option value="reçete">Reçete</option></select></label>' +
      '<label class="field"><span>Metin</span><textarea name="text" required' +
      (canExam ? "" : " disabled") + '></textarea></label>' +
      '<button type="submit" class="btn btn-secondary"' +
      (canExam ? "" : " disabled") + ">Yaz</button></form></div>" +
      '<div class="panel"><h3>«extend» Hasta bilgilerini güncelle</h3>' +
      '<form data-form="update-patient" class="form-grid two-col">' +
      '<input type="hidden" name="patientId" value="' + esc(active.id) + '">' +
      '<label class="field"><span>Telefon</span><input name="phone" value="' + esc(active.phone) + '"' +
      (canExam ? "" : " disabled") + '></label>' +
      '<label class="field"><span>Sigorta</span><select name="insurance"' +
      (canExam ? "" : " disabled") + '>' +
      '<option value="SGK"' + (active.insurance === "SGK" ? " selected" : "") + '>SGK</option>' +
      '<option value="özel"' + (active.insurance === "özel" ? " selected" : "") + '>Özel</option></select></label>' +
      '<div class="btn-row"><button type="submit" class="btn btn-secondary"' +
      (canExam ? "" : " disabled") + ">Güncelle</button></div></form></div>" +
      '<div class="btn-row">' +
      '<button type="button" class="btn btn-primary" data-action="finish-muayene" data-id="' +
      esc(active.id) + '"' + (canExam ? "" : " disabled") + ">Muayeneyi tamamla</button>" +
      '<button type="button" class="btn btn-ghost" data-action="close-muayene">Kapat</button>' +
      "</div></div>";
  }
  el.innerHTML =
    roleHint(["DR"], session) +
    '<div class="panel"><h2>Randevulu hastalar</h2>' + queueHtml + "</div>" +
    workspace;
}

async function renderOdeme()
{
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  await renderUseCasePage("odeme");
  const el = document.getElementById("dummy-data");
  if (!el)
  {
    return;
  }
  const canCashier = hasRole(session, ["VZ"]);
  let rows = "";
  store.payments.forEach(function (pay)
  {
    if (session && session.kind === "patient" && pay.patientId !== session.patientId)
    {
      return;
    }
    const patient = findPatient(store, pay.patientId);
    const name = patient ? patient.name : pay.patientId;
    const tckn = patient ? patient.tckn : "—";
    const finalAmt = Math.round(pay.amount * (1 - pay.discount));
    const isOwn = session && session.kind === "patient" && session.patientId === pay.patientId;
    const canPay = !pay.paid && (canCashier || isOwn);
    const queried = pay.insuranceQuery && pay.insuranceQuery !== "—";
    const actions =
      '<div class="btn-row">' +
      '<button type="button" class="btn btn-secondary btn-sm" data-action="calc-fee" data-pay="' +
      esc(pay.id) + '"' + (canCashier ? "" : " disabled") + ">Ücret hesapla</button>" +
      '<button type="button" class="btn btn-secondary btn-sm" data-action="query-insurance" data-pay="' +
      esc(pay.id) + '" data-tckn="' + esc(tckn) + '"' + (canCashier ? "" : " disabled") +
      ">«include» Sigorta sorgula (TCKN)</button>" +
      '<button type="button" class="btn btn-secondary btn-sm" data-action="apply-discount" data-pay="' +
      esc(pay.id) + '"' + (canCashier && queried ? "" : " disabled") +
      ">«extend» İndirim uygula</button>" +
      '<button type="button" class="btn btn-primary btn-sm" data-action="take-payment" data-pay="' +
      esc(pay.id) + '"' + (canPay ? "" : " disabled") + ">Ödemeyi al</button>" +
      "</div>";
    rows +=
      "<tr>" +
      "<td>" + esc(pay.id) + "</td>" +
      "<td>" + esc(name) + "<br><small>TCKN " + esc(tckn) + "</small></td>" +
      "<td>" + esc(pay.amount) + " " + esc(pay.currency) +
      (pay.discount > 0 ? " → " + finalAmt + " (indirimli)" : "") + "</td>" +
      "<td>" + esc(pay.insuranceQuery) + "</td>" +
      "<td>%" + Math.round(pay.discount * 100) + "</td>" +
      "<td>" + (pay.paid ? "Ödendi" : "Bekliyor") + "</td>" +
      "<td>" + actions + "</td>" +
      "</tr>";
  });
  if (!rows)
  {
    rows = '<tr><td colspan="7" class="empty">Ödeme kaydı yok.</td></tr>';
  }
  el.innerHTML =
    roleHint(["VZ"], session) +
    '<div class="panel"><h2>Ödeme işlemleri</h2>' +
    '<p class="hint">Sıra: ücret hesapla → TCKN ile sigorta sorgula → indirim → ödeme al</p>' +
    '<table class="data-table"><thead><tr>' +
    "<th>ID</th><th>Hasta</th><th>Tutar</th><th>Sigorta</th><th>İndirim</th><th>Durum</th><th>İşlem</th>" +
    "</tr></thead><tbody>" + rows + "</tbody></table></div>";
}

function renderGiris()
{
  renderSessionBar();
  const el = document.getElementById("page-content");
  if (!el)
  {
    return;
  }
  const session = getSession();
  if (session)
  {
    el.innerHTML =
      '<div class="panel"><p>Zaten giriş yaptınız: <strong>' +
      esc(sessionLabel(session)) + "</strong></p>" +
      '<div class="btn-row">' +
      '<button type="button" class="btn btn-ghost" data-action="logout">Çıkış</button>' +
      '<a href="index.html" class="btn btn-primary">Ana sayfa</a>' +
      "</div></div>";
    return;
  }
  el.innerHTML =
    '<div class="login-tabs">' +
    '<button type="button" class="btn btn-secondary active" data-action="tab-login" data-tab="staff">Personel</button>' +
    '<button type="button" class="btn btn-secondary" data-action="tab-login" data-tab="patient">Hasta</button>' +
    "</div>" +
    '<div id="login-staff" class="panel">' +
    "<h2>Personel girişi</h2>" +
    '<p class="hint">Demo şifre: 1234</p>' +
    '<form data-form="login-staff" class="form-grid">' +
    '<label class="field"><span>Rol</span><select name="role" required>' +
    '<option value="KG">KG — Kayıt Görevlisi</option>' +
    '<option value="RG">RG — Randevu Görevlisi</option>' +
    '<option value="DR">DR — Doktor</option>' +
    '<option value="VZ">VZ — Veznedar</option>' +
    "</select></label>" +
    '<label class="field"><span>Şifre</span><input type="password" name="pin" required placeholder="1234"></label>' +
    '<div class="btn-row"><button type="submit" class="btn btn-primary">Giriş yap</button></div>' +
    "</form></div>" +
    '<div id="login-patient" class="panel" hidden>' +
    "<h2>Hasta girişi</h2>" +
    '<p class="hint">Örnek TCKN: 12345678901, 98765432109, 11223344556</p>' +
    '<form data-form="login-patient" class="form-grid">' +
    '<label class="field"><span>TCKN</span><input name="tckn" required maxlength="11" placeholder="11 hane"></label>' +
    '<div class="btn-row"><button type="submit" class="btn btn-primary">Giriş yap</button></div>' +
    "</form></div>" +
    '<div class="panel"><h2>Demo</h2>' +
    '<div class="btn-row">' +
    '<button type="button" class="btn btn-ghost btn-sm" data-action="reset-store">Veriyi sıfırla</button>' +
    "</div></div>";
}

function refreshPage()
{
  if (document.getElementById("use-case-checklist"))
  {
    renderChecklist();
  }
  const page = document.body.dataset.page;
  if (page === "index")
  {
    renderIndex().catch(console.error);
  }
  else if (page === "giris")
  {
    renderGiris();
  }
  else if (page === "kayit")
  {
    renderKayit().catch(console.error);
  }
  else if (page === "rezervasyon")
  {
    renderRezervasyon().catch(console.error);
  }
  else if (page === "muayene")
  {
    renderMuayene().catch(console.error);
  }
  else if (page === "odeme")
  {
    renderOdeme().catch(console.error);
  }
}

function showPatientDetail(id)
{
  const store = getStore();
  const p = findPatient(store, id);
  const box = document.getElementById("patient-detail");
  if (!p || !box)
  {
    return;
  }
  if (hasRole(getSession(), ["DR"]))
  {
    markProgress("muayeneView");
  }
  box.innerHTML =
    renderPatientDossier(p) +
    '<button type="button" class="btn btn-ghost btn-sm" data-action="close-detail">Kapat</button>';
  box.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

function apptFormPatientDoctor()
{
  const form = document.getElementById("appt-form");
  if (!form)
  {
    return { patientId: "", doctorId: "" };
  }
  return {
    patientId: form.querySelector("[name=patientId]").value,
    doctorId: form.querySelector("[name=doctorId]").value
  };
}

function bookSlot(patientId, doctorId, slot, fromAlt)
{
  const store = getStore();
  const doctor = store.doctors.find(function (d)
  {
    return d.id === doctorId;
  });
  const patient = findPatient(store, patientId);
  if (!doctor || !patient)
  {
    return false;
  }
  const slotParts = slot.split(" ");
  const appt = {
    id: nextApptId(store),
    doctor: doctor.name,
    department: doctor.department,
    date: slotParts[0],
    time: slotParts[1] || "",
    status: "beklemede"
  };
  if (!patient.appointments)
  {
    patient.appointments = [];
  }
  patient.appointments.push(appt);
  if (fromAlt)
  {
    doctor.alternativeSlots = doctor.alternativeSlots.filter(function (s)
    {
      return s !== slot;
    });
    markProgress("rezervasyonAlt");
  }
  else
  {
    doctor.availableSlots = doctor.availableSlots.filter(function (s)
    {
      return s !== slot;
    });
  }
  markProgress("rezervasyon");
  saveStore(store);
  return true;
}

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
    markProgress("muayeneView");
    refreshPage();
    return;
  }
  if (action === "view-dossier")
  {
    markProgress("muayeneView");
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
      markProgress("muayene");
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
      markProgress("odemeInsurance");
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
      markProgress("odemeDiscount");
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
    const store = getStore();
    const pay = store.payments.find(function (x)
    {
      return x.id === btn.dataset.pay;
    });
    if (pay && !pay.paid)
    {
      pay.paid = true;
      saveStore(store);
      markProgress("odeme");
      toast("Ödeme alındı.");
      refreshPage();
    }
    return;
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
    window.location.href = "index.html";
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
    markProgress("kayit");
    toast("Hoş geldiniz, " + p.name);
    window.location.href = "kayit.html";
    return;
  }
  if (kind === "register")
  {
    if (!hasRole(getSession(), ["KG"]))
    {
      toast("Kayıt için KG olarak giriş yapın.", true);
      return;
    }
    const store = getStore();
    const tckn = String(fd.get("tckn")).trim();
    if (store.patients.some(function (x)
    {
      return x.tckn === tckn;
    }))
    {
      toast("Bu TCKN zaten kayıtlı.", true);
      return;
    }
    const id = nextPatientId(store);
    store.patients.push({
      id: id,
      tckn: tckn,
      name: String(fd.get("name")).trim(),
      birthDate: fd.get("birthDate"),
      phone: String(fd.get("phone")).trim(),
      insurance: fd.get("insurance"),
      registeredAt: todayStr(),
      appointments: [],
      records: [],
      treatments: [],
      prescriptions: []
    });
    store.payments.push({
      id: nextPayId(store),
      patientId: id,
      amount: 350,
      currency: "TRY",
      insuranceQuery: "—",
      discount: 0,
      paid: false
    });
    saveStore(store);
    markProgress("kayit");
    form.reset();
    toast("Hasta kaydedildi: " + id);
    refreshPage();
    return;
  }
  if (kind === "appointment")
  {
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
    if (!hasRole(getSession(), ["DR"]))
    {
      toast("Muayene için DR olarak giriş yapın.", true);
      return;
    }
    const store = getStore();
    const patient = findPatient(store, fd.get("patientId"));
    if (!patient)
    {
      return;
    }
    if (!patient.records)
    {
      patient.records = [];
    }
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
    if (!patient.treatments)
    {
      patient.treatments = [];
    }
    patient.treatments.push({
      date: todayStr(),
      name: String(fd.get("name")).trim(),
      dose: String(fd.get("dose")).trim()
    });
    saveStore(store);
    markProgress("muayeneTreatment");
    form.reset();
    toast("Tedavi eklendi.");
    refreshPage();
    return;
  }
  if (kind === "prescription")
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
    if (!patient.prescriptions)
    {
      patient.prescriptions = [];
    }
    patient.prescriptions.push({
      date: todayStr(),
      type: fd.get("type"),
      text: String(fd.get("text")).trim()
    });
    saveStore(store);
    markProgress("muayeneRx");
    form.reset();
    toast("Rapor/reçete kaydedildi.");
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
    markProgress("muayeneManage");
    toast("Hasta bilgileri güncellendi.");
    refreshPage();
  }
});

document.addEventListener("DOMContentLoaded", function ()
{
  const shell = document.querySelector(".shell");
  if (shell && !document.getElementById("session-bar"))
  {
    const bar = document.createElement("div");
    bar.id = "session-bar";
    const header = shell.querySelector("header.site-header");
    if (header && header.nextSibling)
    {
      shell.insertBefore(bar, header.nextSibling);
    }
    else
    {
      shell.insertBefore(bar, shell.firstChild.nextSibling);
    }
  }
  refreshPage();
});

//--------------------------------------------------------------------------------------------------------------------------------
