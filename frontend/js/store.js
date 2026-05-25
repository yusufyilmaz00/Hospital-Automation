
function normalizeStore(store) {
  store.patients.forEach(function (p) {
    if (!p.appointments) {
      p.appointments = [];
    }
    if (!p.records) {
      p.records = [];
    }
    if (!p.treatments) {
      p.treatments = [];
    }
    if (!p.prescriptions) {
      p.prescriptions = [];
    }
  });
  store.doctors.forEach(function (d) {
    if (!d.availableSlots) {
      d.availableSlots = [];
    }
    if (!d.alternativeSlots) {
      d.alternativeSlots = [];
    }
  });
  return store;
}

function getStore() {
  if (!isTestMode()) {
    console.warn("[API Mode] API Fetch is not fully implemented yet. Returning empty store.");
    return normalizeStore({ patients: [], doctors: [], payments: [] });
  }

  const raw = localStorage.getItem(STORE_KEY);
  if (raw) {
    return normalizeStore(JSON.parse(raw));
  }
  const base = normalizeStore(JSON.parse(
    JSON.stringify(window.HOSPITAL_DATA["data/patients.json"])
  ));
  saveStore(base);
  return base;
}

function saveStore(store) {
  localStorage.setItem(STORE_KEY, JSON.stringify(store));
}

function resetStore() {
  localStorage.removeItem(STORE_KEY);
  localStorage.removeItem(MUAYENE_PID_KEY);
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

