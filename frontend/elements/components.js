function injectSharedElements() {
  const banner = document.createElement('div');
  banner.id = 'test-mode-banner';
  banner.style.cssText = 'background: #ffecb3; text-align: center; padding: 0.35rem; font-size: 0.85rem; border-bottom: 1px solid #ffd54f; display: flex; justify-content: center; gap: 1rem; align-items: center; width: 100%;';
  const isRoot = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/');
  banner.innerHTML = `
    <span style="color: #b45309;"><strong>Geliştirici Paneli</strong></span>
    <label for="test-mode-toggle" style="cursor: pointer; color: #b45309;">Test Mode</label>
    <input type="checkbox" id="test-mode-toggle" style="cursor: pointer;">
    <a href="${isRoot ? 'pages/' : ''}api-test.html" style="color: #b45309; font-weight: bold; margin-left: 1rem; text-decoration: underline;">API Test &rarr;</a>
  `;
  document.body.insertBefore(banner, document.body.firstChild);

  const header = document.querySelector('header.site-header');
  if (header) {
    const nav = document.createElement('nav');
    nav.className = 'top-nav';
    const isRoot = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/');
    const prefixPage = isRoot ? 'pages/' : '';
    const prefixIndex = isRoot ? '' : '../';
    const pages = [
      { url: prefixIndex + 'index.html', label: 'Ana sayfa' },
      { url: prefixPage + 'giris.html', label: 'Giriş' },
      { url: prefixPage + 'profil.html', label: 'Profil' },
      { url: prefixPage + 'doktor-panel.html', label: 'Doktor' },
      { url: prefixPage + 'hasta-listesi.html', label: 'Hastalar' },
      { url: prefixPage + 'kayit.html', label: 'Hasta Kayıt' },
      { url: prefixPage + 'personel-kayit.html', label: 'Personel Kayıt' },
      { url: prefixPage + 'rezervasyon.html', label: 'Rezervasyon' },
      { url: prefixPage + 'muayene.html', label: 'Muayene' },
      { url: prefixPage + 'odeme.html', label: 'Ödeme' }
    ];

    const currentPage = document.body.dataset.page;
    const pageMap = {
      'index': 'index.html',
      'giris': 'giris.html',
      'profil': 'profil.html',
      'doktor': 'doktor-panel.html',
      'hastalar': 'hasta-listesi.html',
      'kayit': 'kayit.html',
      'personel': 'personel-kayit.html',
      'rezervasyon': 'rezervasyon.html',
      'muayene': 'muayene.html',
      'odeme': 'odeme.html',
      'api-test': 'api-test.html'
    };

    pages.forEach(p => {
      const a = document.createElement('a');
      a.href = p.url;
      a.textContent = p.label;
      if (p.url.endsWith(pageMap[currentPage])) {
        a.className = 'active';
      }
      nav.appendChild(a);
    });
    header.appendChild(nav);
  }

  const shell = document.querySelector('.shell');
  if (shell) {
    const footer = document.createElement('footer');
    footer.className = 'site-footer';
    footer.style.cssText = 'margin-top: 3rem; padding-top: 1.5rem; border-top: 1px solid var(--border); text-align: center;';
    footer.innerHTML = `
      <nav class="top-nav" style="justify-content: center; margin-top: 0; padding-top: 0; border-top: none;">
        <a href="${isRoot ? '' : '../'}index.html">Ana sayfa</a>
        <a href="${isRoot ? 'pages/' : ''}giris.html">Giriş</a>
        <a href="${isRoot ? 'pages/' : ''}profil.html">Profil</a>
        <a href="${isRoot ? 'pages/' : ''}doktor-panel.html">Doktor</a>
        <a href="${isRoot ? 'pages/' : ''}hasta-listesi.html">Hastalar</a>
        <a href="${isRoot ? 'pages/' : ''}kayit.html">Hasta Kayıt</a>
        <a href="${isRoot ? 'pages/' : ''}personel-kayit.html">Personel Kayıt</a>
        <a href="${isRoot ? 'pages/' : ''}rezervasyon.html">Rezervasyon</a>
        <a href="${isRoot ? 'pages/' : ''}muayene.html">Muayene</a>
        <a href="${isRoot ? 'pages/' : ''}odeme.html">Ödeme</a>
      </nav>
      <p style="margin-top: 1rem; color: var(--muted); font-size: 0.85rem;">BLM3722 - Yazılım Mühendisliği - Grup 2</p>
    `;
    shell.appendChild(footer);
  }
}
