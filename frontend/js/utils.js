
function isTestMode() {
  return localStorage.getItem(TEST_MODE_KEY) !== "false"; // Default to true if null
}

function loadJson(path) {
  const key = path.replace(/^\.\//, "");
  if (window.HOSPITAL_DATA && window.HOSPITAL_DATA[key]) {
    return Promise.resolve(window.HOSPITAL_DATA[key]);
  }
  return fetch(path).then(function (res) {
    if (!res.ok) {
      throw new Error("JSON yüklenemedi: " + path);
    }
    return res.json();
  });
}

function esc(s) {
  if (s === null || s === undefined) {
    return "";
  }
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function toast(msg, isError) {
  const old = document.querySelector(".toast");
  if (old) {
    old.remove();
  }
  const el = document.createElement("div");
  el.className = "toast" + (isError ? " error" : "");
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(function () {
    el.remove();
  }, 2800);
}

function todayStr() {
  const d = new Date();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return d.getFullYear() + "-" + m + "-" + day;
}
