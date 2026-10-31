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

  const lightbox = document.querySelector(".lightbox");
  if (lightbox && typeof lightbox.showModal === "function") {
    const image = lightbox.querySelector("img");
    const caption = lightbox.querySelector("figcaption");
    const close = lightbox.querySelector(".lightbox-close");

    document.querySelectorAll("[data-lightbox]").forEach(link => {
      link.addEventListener("click", event => {
        event.preventDefault();
        const thumbnail = link.querySelector("img");
        image.src = link.href;
        image.alt = thumbnail.alt;
        caption.textContent = link.closest("figure")?.querySelector("figcaption")?.textContent || thumbnail.alt;
        lightbox.showModal();
        close.focus();
      });
    });

    close.addEventListener("click", () => lightbox.close());
    lightbox.addEventListener("click", event => {
      if (event.target === lightbox) lightbox.close();
    });
  }
})();
