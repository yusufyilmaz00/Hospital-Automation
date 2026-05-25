
function init() {
  if (typeof injectSharedElements === 'function') {
    injectSharedElements();
  }
  const shell = document.querySelector(".shell");
  if (shell && !document.getElementById("session-bar")) {
    const bar = document.createElement("div");
    bar.id = "session-bar";
    bar.className = "session-bar";
    const header = shell.querySelector("header.site-header");
    if (header) {
      header.appendChild(bar);
    }
  }
  
  const testModeToggle = document.getElementById("test-mode-toggle");
  if (testModeToggle) {
    testModeToggle.checked = isTestMode();
  }

  setupEvents();
  refreshPage();
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', init);
} else {
  init();
}
