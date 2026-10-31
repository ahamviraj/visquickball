(() => {
  const config = window.VI_QUICK_BALL_SITE || {};
  const safeHttps = value => {
    try { const url = new URL(value); return url.protocol === "https:" ? url.href : ""; }
    catch (_) { return ""; }
  };
  const download = safeHttps(config.downloadUrl);
  document.querySelectorAll("[data-download-link]").forEach(link => {
    if (download) { link.href = download; link.hidden = false; link.setAttribute("rel", "noopener noreferrer"); }
  });
  if (download) document.querySelectorAll("[data-download-note]").forEach(note => note.hidden = true);

  document.querySelectorAll("[data-year]").forEach(node => node.textContent = String(new Date().getFullYear()));
})();
