
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
    const isRoot = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/');
    const loginUrl = isRoot ? 'pages/giris.html' : 'giris.html';
    el.innerHTML =
      '<span>Giriş yapılmadı — işlemler için oturum açın.</span>' +
      '<a href="' + loginUrl + '" class="btn btn-primary btn-sm">Giriş yap</a>';
    return;
  }
  el.innerHTML =
    "<span>Oturum: <strong>" + esc(sessionLabel(session)) + "</strong></span>" +
    '<div class="btn-row" style="margin:0">' +
    '<button type="button" class="btn btn-ghost btn-sm" data-action="logout">Çıkış</button>' +
    "</div>";
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

async function renderIndex()
{
  renderSessionBar();
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
    '<label class="field"><span>İsim</span><input name="isim" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Soyisim</span><input name="soyisim" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>E-posta</span><input type="email" name="email" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Şifre</span><input type="password" name="password" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>TCKN</span><input name="tckn" required maxlength="11"' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Doğum tarihi</span><input type="date" name="birthDate" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Telefon</span><input name="phone" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Adres</span><input name="adres" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Boy (cm)</span><input type="number" name="boy" value="170" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field"><span>Kilo (kg)</span><input type="number" name="kilo" value="70" required' + (canRegister ? "" : " disabled") + '></label>' +
    '<label class="field" style="grid-column: span 2"><span>Sigorta</span><select name="insurance"' + (canRegister ? "" : " disabled") + '>' +
    '<option value="SGK">SGK</option><option value="özel">Özel</option></select></label>' +
    '<div class="btn-row" style="grid-column: span 2; justify-content: flex-end;">' +
    '<button type="submit" class="btn btn-primary"' + (canRegister ? "" : " disabled") + ">Kaydet</button>" +
    "</div></form></div>" +
    '<div class="panel"><h2>Kayıtlı hastalar</h2>' +
    '<div id="patient-detail"></div>' +
    renderPatientsTableRows(list, session) +
    "</div>";
}

function buildSlotOptions(doctors, doctorId)
{
  let html = "";
  doctors.forEach(function (d)
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
    "<h3>Alternatif Tarih Önerilmesi</h3>" +
    '<p class="hint">' + esc(doctor.name) + " için müsait slot yok. Alternatif tarihler:</p>" +
    '<div class="btn-row">' + btns + "</div></div>"
  );
}

async function renderRezervasyon()
{
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  const el = document.getElementById("dummy-data");
  if (!el)
  {
    return;
  }
  const canBook = hasRole(session, ["RG", "patient"]);
  const pickDoctor = localStorage.getItem("rg-pick-doctor") || "";
  
  let doctors = store.doctors;
  if (!isTestMode() && hasRole(session, ["RG"])) {
    try {
      const res = await fetch("http://localhost:8080/api/randevu-gorevlisi/doktorlar", { credentials: "include" });
      if (res.ok) {
        const data = await res.json();
        doctors = data.map(d => ({
          id: d.id,
          name: d.unvan + " " + d.isim + " " + d.soyisim,
          department: d.bolum,
          availableSlots: ["09:00", "09:30", "10:00", "10:30", "11:00", "13:00", "14:00"] // API does not return slots here
        }));
      }
    } catch(e) { console.error(e); }
  }

  let patientOptions = store.patients.map(function (p)
  {
    return '<option value="' + esc(p.id) + '">' + esc(p.name) + "</option>";
  }).join("");
  let doctorOptions = doctors.map(function (d)
  {
    const sel = d.id === pickDoctor ? " selected" : "";
    const free = d.availableSlots.length;
    return (
      '<option value="' + esc(d.id) + '"' + sel + ">" +
      esc(d.name) + " — " + esc(d.department) +
      " (" + free + " slot)</option>"
    );
  }).join("");
  const slotOptions = buildSlotOptions(doctors, pickDoctor || null);
  const picked = doctors.find(function (d)
  {
    return String(d.id) === String(pickDoctor);
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
    "<h3>Hasta dosyası — " + esc(p.name) + "</h3>" +
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
  const el = document.getElementById("dummy-data");
  if (!el)
  {
    return;
  }
  const canExam = hasRole(session, ["DR"]);
  const activePid = localStorage.getItem(MUAYENE_PID_KEY) || "";
  let active = null;
  if (activePid) {
    if (!isTestMode()) {
      try {
        const res = await fetch("http://localhost:8080/api/doktor/randevu/" + activePid + "/hasta", { credentials: "include" });
        if (res.ok) {
          const apiData = await res.json();
          const contact = apiData.iletisimBilgisi || {};
          active = {
            id: apiData.id,
            name: contact.isim ? contact.isim + " " + contact.soyisim : "Bilinmiyor",
            tckn: contact.tckno || "Bilinmiyor",
            phone: contact.telefon || "Bilinmiyor",
            insurance: "Bilinmiyor",
            birthDate: contact.doğumTarihi || "Bilinmiyor",
            appointments: (apiData.randevular || []).map(r => {
              const dt = r.tarih ? r.tarih.split("T") : ["", ""];
              return { date: dt[0], time: dt[1] ? dt[1].substring(0, 5) : "", doctor: r.doktor ? r.doktor.unvan + " (" + r.doktor.bolum + ")" : "Bilinmiyor", status: "onaylı" };
            }),
            treatments: (apiData.randevular || []).flatMap(r => (r.tedaviler || []).map(t => ({ date: r.tarih ? r.tarih.split("T")[0] : "", name: t.tip, dose: t.aciklama }))),
            prescriptions: [],
            records: (apiData.hastaliklar || []).map(h => ({ date: "Geçmiş", note: h.detay }))
          };
        }
      } catch (e) { console.error(e); }
    } else {
      active = findPatient(store, activePid);
    }
  }

  let queueHtml = "";
  if (!isTestMode()) {
    try {
      const res = await fetch("http://localhost:8080/api/doktor/randevular", { credentials: "include" });
      if (res.ok) {
        const data = await res.json();
        data.forEach(r => {
           queueHtml +=
            '<div class="btn-row" style="margin-bottom:0.5rem">' +
            "<span><strong>" + esc((r.hastaIsim || "Bilinmiyor") + " " + (r.hastaSoyisim || "")) + "</strong> — " + esc(r.randevuZamani ? r.randevuZamani.replace("T", " ") : "") + "</span>" +
            '<button type="button" class="btn btn-primary btn-sm" data-action="open-muayene" data-id="' + esc(r.id) + '"' + (canExam ? "" : " disabled") + '>Muayeneye al</button>' +
            "</div>";
        });
      }
    } catch(e) { console.error(e); }
  } else {
    const waiting = patientsWithConfirmedAppt(store);
    waiting.forEach(function (p) {
      queueHtml +=
        '<div class="btn-row" style="margin-bottom:0.5rem">' +
        "<span><strong>" + esc(p.name) + "</strong> — onaylı randevu</span>" +
        '<button type="button" class="btn btn-primary btn-sm" data-action="open-muayene" data-id="' + esc(p.id) + '"' + (canExam ? "" : " disabled") + ">Muayeneye al</button>" +
        "</div>";
    });
  }

  if (!queueHtml)
  {
    queueHtml = '<p class="empty">Onaylı randevulu hasta yok.</p>';
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
      '<div class="panel"><h3>Tedavi ekle</h3>' +
      '<form data-form="treatment" class="form-grid two-col">' +
      '<input type="hidden" name="patientId" value="' + esc(active.id) + '">' +
      '<label class="field"><span>İlaç / tedavi</span><input name="name" required' +
      (canExam ? "" : " disabled") + '></label>' +
      '<label class="field"><span>Doz</span><input name="dose" required' +
      (canExam ? "" : " disabled") + '></label>' +
      '<div class="btn-row"><button type="submit" class="btn btn-secondary"' +
      (canExam ? "" : " disabled") + ">Tedavi ekle</button></div></form></div>" +
      '<div class="panel"><h3>Rapor / reçete</h3>' +
      '<form data-form="prescription">' +
      '<input type="hidden" name="patientId" value="' + esc(active.id) + '">' +
      '<label class="field"><span>Tür</span><select name="type"' +
      (canExam ? "" : " disabled") + '><option value="rapor">Rapor</option><option value="reçete">Reçete</option></select></label>' +
      '<label class="field"><span>Metin</span><textarea name="text" required' +
      (canExam ? "" : " disabled") + '></textarea></label>' +
      '<button type="submit" class="btn btn-secondary"' +
      (canExam ? "" : " disabled") + ">Yaz</button></form></div>" +
      '<div class="panel"><h3>Hasta bilgilerini güncelle</h3>' +
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
      '<button type="button" class="btn btn-primary btn-sm" data-action="take-payment" data-pay="' +
      esc(pay.id) + '"' + (canPay ? "" : " disabled") + ">Ödeme Al</button>" +
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
  const hintHtml = (session && session.kind === "patient") ? "" : roleHint(["VZ"], session);
  el.innerHTML =
    hintHtml +
    '<div class="panel"><h2>Ödeme işlemleri</h2>' +
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
      '<a href="../index.html" class="btn btn-primary">Ana sayfa</a>' +
      "</div></div>";
    return;
  }

  if (isTestMode()) {
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
      "</form></div>";
  } else {
    el.innerHTML =
      '<div class="panel">' +
      "<h2>Sisteme Giriş (Gerçek API)</h2>" +
      '<p class="hint">E-posta ve şifrenizle giriş yapınız.</p>' +
      '<form data-form="login-backend" class="form-grid">' +
      '<label class="field"><span>E-posta</span><input type="email" name="email" required placeholder="ornek@hastane.com"></label>' +
      '<label class="field"><span>Şifre</span><input type="password" name="password" required></label>' +
      '<div class="btn-row"><button type="submit" class="btn btn-primary">Giriş yap</button></div>' +
      "</form></div>";
  }
}

function updateNavigationVisibility() {
  const session = getSession();
  let role = null;
  if (session) {
    role = session.kind === 'patient' ? 'HASTA' : session.role;
  }

  const checkAccess = (href) => {
    const filename = href.split('/').pop();
    if (filename === 'index.html') return true;
    if (filename === 'giris.html') return !session;
    if (session) {
      if (role === 'HASTA') return ['profil.html', 'muayene.html', 'rezervasyon.html', 'odeme.html'].includes(filename);
      if (role === 'DR') return ['profil.html', 'doktor-panel.html', 'hasta-listesi.html', 'muayene.html'].includes(filename);
      if (role === 'KG') return ['profil.html', 'kayit.html', 'hasta-listesi.html', 'personel-kayit.html'].includes(filename);
      if (role === 'RG') return ['profil.html', 'rezervasyon.html', 'hasta-listesi.html', 'doktor-panel.html'].includes(filename);
      if (role === 'VZ') return ['profil.html', 'odeme.html', 'hasta-listesi.html'].includes(filename);
    }
    return false;
  };

  const mainNav = document.querySelector('header.site-header nav.top-nav');
  if (mainNav) {
    const links = mainNav.querySelectorAll('a');
    links.forEach(link => {
      const href = link.getAttribute('href');
      const hasAccess = checkAccess(href);
      
      if (hasAccess) {
        link.style.display = 'inline-block';
        link.style.opacity = '1';
        link.style.pointerEvents = 'auto';
        link.style.cursor = 'pointer';
      } else {
        link.style.display = 'none';
      }
    });
  }
}

function refreshPage()
{
  updateNavigationVisibility();
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
  else if (page === "profil")
  {
    renderProfil().catch(console.error);
  }
  else if (page === "doktor-panel")
  {
    renderDoktorPanel().catch(console.error);
  }
  else if (page === "hasta-listesi")
  {
    renderHastaListesi().catch(console.error);
  }
  else if (page === "personel-kayit")
  {
    renderPersonelKayit().catch(console.error);
  }
  else if (page === "api-test")
  {
    renderApiTest().catch(console.error);
  }
}

async function renderProfil() {
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  const el = document.getElementById("dummy-data");
  if (!el) return;
  
  if (!session) {
    el.innerHTML = '<div class="panel"><p class="empty">Bu sayfayı görüntülemek için giriş yapmalısınız.</p></div>';
    return;
  }

  if (session.kind === "staff") {
    el.innerHTML = 
      '<div class="panel"><h2>Personel Profili</h2>' +
      '<div class="panel" style="background:var(--accent-light)">' +
      '<h3>' + esc(session.roleLabel) + '</h3>' +
      '<p><strong>Rol Kodu:</strong> ' + esc(session.role) + '</p>' +
      '<p><em>Sistem Notu: Personel detaylı profil verileri backend API entegrasyonundan sonra aktif olacaktır.</em></p>' +
      '</div></div>';
    return;
  }

  let p;
  if (!isTestMode()) {
    try {
      const res = await fetch("http://localhost:8080/api/hasta/self", { credentials: "include" });
      if (res.ok) {
        const apiData = await res.json();
        const contact = apiData.iletisimBilgisi || {};
        p = {
          name: contact.isim ? contact.isim + " " + contact.soyisim : session.email,
          tckn: contact.tckno || "Bilinmiyor",
          phone: contact.telefon || "Bilinmiyor",
          insurance: "Bilinmiyor",
          birthDate: contact.doğumTarihi || "Bilinmiyor",
          appointments: (apiData.randevular || []).map(r => {
            const dt = r.tarih ? r.tarih.split("T") : ["", ""];
            return {
              date: dt[0],
              time: dt[1] ? dt[1].substring(0, 5) : "",
              doctor: r.doktor ? r.doktor.unvan + " (" + r.doktor.bolum + ")" : "Bilinmiyor",
              status: "onaylı"
            };
          }),
          treatments: (apiData.randevular || []).flatMap(r => (r.tedaviler || []).map(t => ({
            date: r.tarih ? r.tarih.split("T")[0] : "",
            name: t.tip,
            dose: t.aciklama
          }))),
          prescriptions: [],
          records: (apiData.hastaliklar || []).map(h => ({
            date: "Geçmiş",
            note: h.detay
          }))
        };
      } else {
        el.innerHTML = '<div class="panel"><p class="empty">Profil bilgileri yüklenemedi.</p></div>';
        return;
      }
    } catch (err) {
      el.innerHTML = '<div class="panel"><p class="empty">Sunucuya bağlanılamadı.</p></div>';
      return;
    }
  } else {
    p = findPatient(store, session.patientId);
  }

  el.innerHTML =
    '<div class="panel"><h2>Hasta Profili</h2>' +
    renderPatientDossier(p) + '</div>';
}

async function renderDoktorPanel() {
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  const el = document.getElementById("dummy-data");
  if (!el) return;
  if (!hasRole(session, ["DR"])) {
    el.innerHTML = '<div class="panel"><p class="empty">Bu sayfayı görüntülemek için Doktor (DR) olarak giriş yapmalısınız.</p></div>';
    return;
  }
  
  let queueHtml = "";
  if (!isTestMode()) {
    try {
      const res = await fetch("http://localhost:8080/api/doktor/randevular", { credentials: "include" });
      if (res.ok) {
        const data = await res.json();
        data.forEach(r => {
           queueHtml +=
            '<div class="btn-row" style="margin-bottom:0.5rem">' +
            "<span><strong>" + esc((r.hastaIsim || "Bilinmiyor") + " " + (r.hastaSoyisim || "")) + "</strong> — " + esc(r.randevuZamani ? r.randevuZamani.replace("T", " ") : "") + "</span>" +
            '<button type="button" class="btn btn-primary btn-sm" data-action="open-muayene" data-id="' +
            esc(r.id) + '">Muayeneye al</button>' +
            "</div>";
        });
      }
    } catch(e) { console.error(e); }
  } else {
    const waiting = patientsWithConfirmedAppt(store);
    waiting.forEach(function (p) {
      queueHtml +=
        '<div class="btn-row" style="margin-bottom:0.5rem">' +
        "<span><strong>" + esc(p.name) + "</strong> — onaylı randevu</span>" +
        '<button type="button" class="btn btn-primary btn-sm" data-action="open-muayene" data-id="' +
        esc(p.id) + '">Muayeneye al</button>' +
        "</div>";
    });
  }

  if (!queueHtml) queueHtml = '<p class="empty">Onaylı randevulu hasta yok.</p>';
  el.innerHTML = '<div class="panel"><h2>Doktor Paneli - Bekleyen Hastalar</h2>' + queueHtml + '</div>';
}

async function renderHastaListesi() {
  const session = getSession();
  const store = getStore();
  renderSessionBar();
  const el = document.getElementById("dummy-data");
  if (!el) return;
  if (!hasRole(session, ["KG"])) {
    el.innerHTML = '<div class="panel"><p class="empty">Bu sayfayı görüntülemek için Kayıt Görevlisi (KG) olarak giriş yapmalısınız.</p></div>';
    return;
  }
  let patients = store.patients;
  if (!isTestMode()) {
    try {
      const res = await fetch("http://localhost:8080/api/kayit/hastalar", { credentials: "include" });
      if (res.ok) {
        const apiData = await res.json();
        patients = apiData.map(p => {
           const c = p.iletisimBilgisi || {};
           return {
             id: p.id,
             name: (c.isim || "") + " " + (c.soyisim || ""),
             tckn: c.tckno || "Bilinmiyor",
             insurance: "Bilinmiyor",
             appointments: (p.randevular || []).map(r => {
               const dt = r.tarih ? r.tarih.split("T") : ["", ""];
               return { date: dt[0], time: dt[1] ? dt[1].substring(0, 5) : "" };
             })
           };
        });
      }
    } catch (e) {
      console.error(e);
    }
  }
  el.innerHTML = '<div class="panel"><h2>Tüm Hastalar</h2><div id="patient-detail"></div>' + renderPatientsTableRows(patients, session) + '</div>';
}

async function renderPersonelKayit() {
  const session = getSession();
  renderSessionBar();
  const el = document.getElementById("dummy-data");
  if (!el) return;
  el.innerHTML = '<div class="panel"><h2>Personel Kayıt</h2><p class="hint">API tarafı desteklenene kadar mock data üzerinden çalışmıyor. (Sadece UI stub)</p></div>';
}

async function renderApiTest() {
  const el = document.getElementById("dummy-data");
  if (!el) return;

  const endpoints = [
    { method: "GET", name: "Hasta Self", url: "http://localhost:8080/api/hasta/self", category: "Hasta İşlemleri" },
    { method: "PUT", name: "Hasta Boy Güncelle", url: "http://localhost:8080/api/hasta/self/boy", category: "Hasta İşlemleri" },
    { method: "PUT", name: "Hasta Kilo Güncelle", url: "http://localhost:8080/api/hasta/self/kilo", category: "Hasta İşlemleri" },
    { method: "PUT", name: "Hasta İletişim Güncelle", url: "http://localhost:8080/api/hasta/self/iletisim", category: "Hasta İşlemleri" },
    
    { method: "GET", name: "Doktor Profil", url: "http://localhost:8080/api/doktor", category: "Doktor İşlemleri" },
    { method: "POST", name: "Doktor Kayıt", url: "http://localhost:8080/api/doktor/register", category: "Doktor İşlemleri" },
    { method: "GET", name: "Doktor Randevular", url: "http://localhost:8080/api/doktor/randevular", category: "Doktor İşlemleri" },
    { method: "GET", name: "Randevu Hastası Getir", url: "http://localhost:8080/api/doktor/randevu/1/hasta", category: "Doktor İşlemleri" },
    { method: "PUT", name: "Randevu Süresi Güncelle", url: "http://localhost:8080/api/doktor/randevu/1/sure", category: "Doktor İşlemleri" },
    { method: "POST", name: "Tedavi Ekle", url: "http://localhost:8080/api/doktor/randevu/1/tedavi", category: "Doktor İşlemleri" },
    { method: "DELETE", name: "Tedavi Sil", url: "http://localhost:8080/api/doktor/randevu/1/tedavi/1", category: "Doktor İşlemleri" },
    { method: "POST", name: "Reçete Ekle", url: "http://localhost:8080/api/doktor/randevu/1/tedavi/1/recete", category: "Doktor İşlemleri" },
    { method: "DELETE", name: "Reçete Sil", url: "http://localhost:8080/api/doktor/randevu/1/tedavi/1/recete/1", category: "Doktor İşlemleri" },
    { method: "POST", name: "Rapor Ekle", url: "http://localhost:8080/api/doktor/randevu/1/rapor", category: "Doktor İşlemleri" },
    { method: "DELETE", name: "Rapor Sil", url: "http://localhost:8080/api/doktor/randevu/1/rapor/1", category: "Doktor İşlemleri" },
    { method: "PUT", name: "Hasta Boy (Doktor)", url: "http://localhost:8080/api/doktor/hasta/1/boy", category: "Doktor İşlemleri" },
    { method: "PUT", name: "Hasta Kilo (Doktor)", url: "http://localhost:8080/api/doktor/hasta/1/kilo", category: "Doktor İşlemleri" },
    { method: "POST", name: "Hastalık Ekle", url: "http://localhost:8080/api/doktor/hasta/1/hastalik", category: "Doktor İşlemleri" },
    { method: "PUT", name: "Hastalık Güncelle", url: "http://localhost:8080/api/doktor/hasta/1/hastalik/1", category: "Doktor İşlemleri" },
    { method: "DELETE", name: "Hastalık Sil", url: "http://localhost:8080/api/doktor/hasta/1/hastalik/1", category: "Doktor İşlemleri" },
    
    { method: "POST", name: "KG Personel Kayıt", url: "http://localhost:8080/api/kayit/register", category: "Kayıt Görevlisi İşlemleri" },
    { method: "POST", name: "Hasta Kayıt", url: "http://localhost:8080/api/kayit/hasta", category: "Kayıt Görevlisi İşlemleri" },
    { method: "GET", name: "Hastaları Listele", url: "http://localhost:8080/api/kayit/hastalar", category: "Kayıt Görevlisi İşlemleri" },
    
    { method: "POST", name: "RG Personel Kayıt", url: "http://localhost:8080/api/randevu-gorevlisi/register", category: "Randevu Görevlisi İşlemleri" },
    { method: "GET", name: "Doktorları Listele", url: "http://localhost:8080/api/randevu-gorevlisi/doktorlar", category: "Randevu Görevlisi İşlemleri" },
    { method: "POST", name: "Randevu Al", url: "http://localhost:8080/api/randevu-gorevlisi/randevu", category: "Randevu Görevlisi İşlemleri" },
    { method: "DELETE", name: "Randevu İptal", url: "http://localhost:8080/api/randevu-gorevlisi/randevu/1", category: "Randevu Görevlisi İşlemleri" },
    { method: "GET", name: "Alternatif Tarihler", url: "http://localhost:8080/api/randevu-gorevlisi/alternatif-tarihler", category: "Randevu Görevlisi İşlemleri" },
    
    { method: "POST", name: "VZ Personel Kayıt", url: "http://localhost:8080/api/veznedar/register", category: "Vezne İşlemleri" },
    { method: "POST", name: "Ödeme Al", url: "http://localhost:8080/api/veznedar/randevu/1/odeme", category: "Vezne İşlemleri" },
    
    { method: "POST", name: "Giriş Yap", url: "http://localhost:8080/api/auth/login", category: "Kimlik Doğrulama" },
    { method: "POST", name: "Çıkış Yap", url: "http://localhost:8080/api/auth/logout", category: "Kimlik Doğrulama" }
  ];

  const categories = {};
  endpoints.forEach((ep, i) => {
    if (!categories[ep.category]) categories[ep.category] = [];
    ep.id = i;
    categories[ep.category].push(ep);
  });

  let html = '<div class="panel" style="margin-bottom: 0;"><h2>Backend Tüm Endpoint Testleri</h2>';
  html += '<p class="hint">Spring Boot projenizdeki 32 adet mapping kategoriler halinde test ediliyor...</p></div>';

  for (const cat in categories) {
    html += '<div class="panel" style="max-width: 100%; overflow-x: auto; margin-top: 1rem;">';
    html += '<h3 style="margin-top: 0; color: var(--accent);">' + esc(cat) + '</h3>';
    html += '<table class="data-table"><thead><tr><th style="width: 80px;">Metot</th><th>Servis</th><th>Endpoint URL</th><th style="width: 140px;">Durum</th></tr></thead><tbody>';
    
    categories[cat].forEach(ep => {
      html += '<tr id="api-test-row-' + ep.id + '">';
      html += '<td><span class="badge" style="background: var(--surface); border: 1px solid var(--border); color: var(--text);">' + ep.method + '</span></td>';
      html += '<td><strong>' + esc(ep.name) + '</strong></td>';
      html += '<td><code style="background: #f1f5f9; padding: 0.15rem 0.35rem; border-radius: 4px; font-size: 0.85rem;">' + esc(ep.url) + '</code></td>';
      html += '<td><span class="badge" style="background: #e2e8f0; color: #475569;">Bağlanılıyor...</span></td>';
      html += '</tr>';
    });
    
    html += '</tbody></table></div>';
  }

  el.innerHTML = html;

  for (let i = 0; i < endpoints.length; i++) {
    const ep = endpoints[i];
    const rowId = 'api-test-row-' + i;
    try {
      const res = await fetch(ep.url, { method: ep.method, mode: 'cors' });
      const row = document.getElementById(rowId);
      if (row) {
        if (res.ok) {
          row.cells[3].innerHTML = '<span class="badge done">Başarılı (' + res.status + ')</span>';
        } else {
          row.cells[3].innerHTML = '<span class="badge" style="background: #dcfce7; color: #1a7f4b;">Bağlanıldı (' + res.status + ')</span>';
        }
      }
    } catch (err) {
      const row = document.getElementById(rowId);
      if (row) {
        row.cells[3].innerHTML = '<span class="badge" style="background: #fef2f2; color: #b91c1c;">Hata (Yok)</span>';
      }
    }
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
  }
  box.innerHTML =
    renderPatientDossier(p) +
    '<button type="button" class="btn btn-ghost btn-sm" data-action="close-detail">Kapat</button>';
  box.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

