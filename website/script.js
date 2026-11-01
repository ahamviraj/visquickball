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

  const presentation = document.querySelector("[data-presentation]");
  if (presentation) {
    const viewport = presentation.querySelector(".presentation-viewport");
    const slides = Array.from(presentation.querySelectorAll(".promo-slide"));
    const indicators = Array.from(presentation.querySelectorAll("[data-slide-to]"));
    let activeIndex = 0;

    const updateActive = index => {
      if (index < 0 || index >= slides.length) return;
      activeIndex = index;
      slides.forEach((slide, slideIndex) => {
        const active = slideIndex === activeIndex;
        slide.classList.toggle("is-active", active);
        slide.setAttribute("aria-hidden", "false");
        slide.inert = false;
      });
      indicators.forEach((button, buttonIndex) => {
        const active = buttonIndex === activeIndex;
        button.classList.toggle("is-active", active);
        if (active) button.setAttribute("aria-current", "true");
        else button.removeAttribute("aria-current");
      });
    };

    // The page keeps its normal scroll behavior. These indicators simply jump
    // to a slide, while the observer updates the active dot as each card enters view.
    indicators.forEach(button => {
      button.addEventListener("click", () => {
        const target = slides[Number(button.dataset.slideTo)];
        if (target) target.scrollIntoView({
          behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth",
          block: "start"
        });
      });
    });

    const observer = new IntersectionObserver(entries => {
      const visible = entries
        .filter(entry => entry.isIntersecting)
        .sort((a, b) => b.intersectionRatio - a.intersectionRatio);
      if (visible.length) updateActive(slides.indexOf(visible[0].target));
    }, {
      root: null,
      rootMargin: "-18% 0px -18% 0px",
      threshold: [0.15, 0.3, 0.5, 0.7]
    });
    slides.forEach(slide => observer.observe(slide));

    viewport.addEventListener("keydown", event => {
      if (event.altKey || event.ctrlKey || event.metaKey) return;
      if (event.key !== "ArrowDown" && event.key !== "ArrowUp") return;
      const nextIndex = activeIndex + (event.key === "ArrowDown" ? 1 : -1);
      if (nextIndex < 0 || nextIndex >= slides.length) return;
      event.preventDefault();
      slides[nextIndex].scrollIntoView({
        behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth",
        block: "start"
      });
    });

    updateActive(0);
  }

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
