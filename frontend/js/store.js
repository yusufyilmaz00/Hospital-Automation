//
// Utkan Başurgan
//
//--------------------------------------------------------------------------------------------------------------------------------

function normalizeStore(store)
{
  if (!store.staffProfiles || Object.keys(store.staffProfiles).length === 0)
  {
    const base = window.HOSPITAL_DATA && window.HOSPITAL_DATA["data/patients.json"];
    store.staffProfiles = base && base.staffProfiles
      ? JSON.parse(JSON.stringify(base.staffProfiles))
      : {};
  }
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

function getBaseStore()
{
  return normalizeStore(JSON.parse(
    JSON.stringify(window.HOSPITAL_DATA["data/patients.json"])
  ));
}

function shouldRefreshStore(store, base)
{
  const meta = localStorage.getItem(STORE_META_KEY);

  if (meta !== STORE_SEED_VERSION)
  {
    return true;
  }

  if (!store || !Array.isArray(store.patients) || !Array.isArray(store.doctors) || !Array.isArray(store.payments))
  {
    return true;
  }

  return store.patients.length < base.patients.length ||
    store.doctors.length < base.doctors.length ||
    store.payments.length < base.payments.length;
}

function getStore()
{
  if (!isTestMode())
  {
    console.warn("[API Mode] API Fetch is not fully implemented yet. Returning empty store.");
    return normalizeStore({ patients: [], doctors: [], payments: [] });
  }

  const base = getBaseStore();
  const raw = localStorage.getItem(STORE_KEY);
  if (raw)
  {
    const store = normalizeStore(JSON.parse(raw));
    if (!shouldRefreshStore(store, base))
    {
      return store;
    }
  }

  saveStore(base);
  return base;
}

function saveStore(store)
{
  localStorage.setItem(STORE_META_KEY, STORE_SEED_VERSION);
  localStorage.setItem(STORE_KEY, JSON.stringify(store));
}

function resetStore()
{
  localStorage.removeItem(STORE_KEY);
  localStorage.removeItem(STORE_META_KEY);
  localStorage.removeItem(MUAYENE_PID_KEY);
  localStorage.removeItem(MUAYENE_AID_KEY);
  localStorage.removeItem(MUAYENE_DONE_APPTS_KEY);
  getStore();
}

function nextPatientId(store) {
  let max = 1000;
  store.patients.forEach(function (p) {
    const n = parseInt(p.id.replace("P-", ""), 10);
    if (!isNaN(n) && n > max) {
      max = n;
    }
  });
  return "P-" + (max + 1);
}

function nextApptId(store) {
  let max = 500;
  store.patients.forEach(function (p) {
    (p.appointments || []).forEach(function (a) {
      const n = parseInt(String(a.id).replace("R-", ""), 10);
      if (!isNaN(n) && n > max) {
        max = n;
      }
    });
  });
  return "R-" + (max + 1);
}

function nextPayId(store) {
  let max = 800;
  store.payments.forEach(function (pay) {
    const n = parseInt(String(pay.id).replace("PY-", ""), 10);
    if (!isNaN(n) && n > max) {
      max = n;
    }
  });
  return "PY-" + (max + 1);
}

function findPatient(store, id) {
  return store.patients.find(function (p) {
    return p.id === id;
  });
}

function compareAppointmentsByTime(left, right)
{
  const leftValue = String((left.date || "") + " " + (left.time || "")).trim();
  const rightValue = String((right.date || "") + " " + (right.time || "")).trim();

  return leftValue.localeCompare(rightValue, "tr");
}

function confirmedAppointmentRows(store)
{
  const rows = [];

  store.patients.forEach(function (patient)
  {
    (patient.appointments || []).forEach(function (appointment)
    {
      if (appointment.status === "onaylı")
      {
        rows.push({
          patient: patient,
          appointment: appointment
        });
      }
    });
  });

  rows.sort(function (left, right)
  {
    return compareAppointmentsByTime(left.appointment, right.appointment);
  });

  return rows;
}

function findPatientAppointment(patient, appointmentId)
{
  if (!patient || !appointmentId)
  {
    return null;
  }

  return (patient.appointments || []).find(function (appointment)
  {
    return String(appointment.id) === String(appointmentId);
  }) || null;
}

function updateAppointmentStatus(store, patientId, appointmentId, nextStatus, fallbackStatus)
{
  const patient = findPatient(store, patientId);
  let appointment = findPatientAppointment(patient, appointmentId);

  if (!appointment && patient && fallbackStatus)
  {
    appointment = (patient.appointments || []).filter(function (item)
    {
      return item.status === fallbackStatus;
    }).sort(compareAppointmentsByTime)[0] || null;
  }

  if (!appointment && patient)
  {
    appointment = (patient.appointments || []).filter(function (item)
    {
      return item.status === "onaylı";
    }).sort(compareAppointmentsByTime)[0] || null;
  }

  if (!appointment)
  {
    return "";
  }

  appointment.status = nextStatus;
  saveStore(store);

  return appointment.id;
}

function getFinishedAppointmentIds()
{
  const raw = localStorage.getItem(MUAYENE_DONE_APPTS_KEY);

  if (!raw)
  {
    return [];
  }

  try
  {
    const ids = JSON.parse(raw);

    if (Array.isArray(ids))
    {
      return ids.map(String);
    }
  }
  catch(e)
  {
  }

  return [];
}

function rememberFinishedAppointment(appointmentId)
{
  if (!appointmentId)
  {
    return;
  }

  const ids = getFinishedAppointmentIds();
  const value = String(appointmentId);

  if (ids.indexOf(value) < 0)
  {
    ids.push(value);
    localStorage.setItem(MUAYENE_DONE_APPTS_KEY, JSON.stringify(ids));
  }
}

function patientsWithConfirmedAppt(store) {
  return store.patients.filter(function (p) {
    return (p.appointments || []).some(function (a) {
      return a.status === "onaylı";
    });
  });
}

function bookSlot(patientId, doctorId, slot, fromAlt) {
  const store = getStore();
  const doctor = store.doctors.find(function (d) {
    return d.id === doctorId;
  });
  const patient = findPatient(store, patientId);
  if (!doctor || !patient) {
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
  if (!patient.appointments) {
    patient.appointments = [];
  }
  patient.appointments.push(appt);
  if (fromAlt) {
    doctor.alternativeSlots = doctor.alternativeSlots.filter(function (s) {
      return s !== slot;
    });
  } else {
    doctor.availableSlots = doctor.availableSlots.filter(function (s) {
      return s !== slot;
    });
  }
  saveStore(store);
  return true;
}

//--------------------------------------------------------------------------------------------------------------------------------
