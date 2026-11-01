(() => {
  "use strict";

  const reduceMotion = () =>
    window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  /* ---------------------------------------------------------------
     Config: download URL (unchanged behavior)
     --------------------------------------------------------------- */
  const config = window.VI_QUICK_BALL_SITE || {};
  const safeHttps = value => {
    try {
      const url = new URL(value);
      return url.protocol === "https:" ? url.href : "";
    } catch (_) {
      return "";
    }
  };
  const download = safeHttps(config.downloadUrl);
  if (download) {
    document.querySelectorAll("[data-download-link]").forEach(link => {
      link.href = download;
      link.hidden = false;
      link.setAttribute("rel", "noopener noreferrer");
    });
    document.querySelectorAll("[data-download-note]").forEach(note => {
      note.hidden = true;
    });
  }

  /* ---------------------------------------------------------------
     Footer year
     --------------------------------------------------------------- */
  document.querySelectorAll("[data-year]").forEach(node => {
    node.textContent = String(new Date().getFullYear());
  });

  /* ---------------------------------------------------------------
     Hero feature selector <-> phone screenshots
     --------------------------------------------------------------- */
  const heroSelector = document.querySelector("[data-feature-selector]");
  const heroScreens = document.querySelector("[data-hero-screens]");
  const heroCaption = document.querySelector("#hero-caption");

  if (heroSelector && heroScreens) {
    const tabs = Array.from(heroSelector.querySelectorAll(".feature-tab"));
    const shots = Array.from(heroScreens.querySelectorAll(".phone-shot"));

    const activate = index => {
      if (index < 0 || index >= tabs.length) return;
      tabs.forEach((tab, i) => {
        const on = i === index;
        tab.classList.toggle("is-active", on);
        tab.setAttribute("aria-selected", String(on));
        tab.tabIndex = on ? 0 : -1;
      });
      shots.forEach((shot, i) => {
        shot.classList.toggle("is-active", i === index);
      });
      if (heroCaption) {
        const desc = tabs[index].querySelector(".tab-desc");
        if (desc) heroCaption.textContent = desc.textContent;
      }
    };

    tabs.forEach((tab, i) => {
      tab.addEventListener("click", () => activate(i));
      tab.addEventListener("keydown", event => {
        const keys = ["ArrowDown", "ArrowRight", "ArrowUp", "ArrowLeft", "Home", "End"];
        if (!keys.includes(event.key)) return;
        event.preventDefault();
        let next = i;
        if (event.key === "ArrowDown" || event.key === "ArrowRight") next = (i + 1) % tabs.length;
        else if (event.key === "ArrowUp" || event.key === "ArrowLeft") next = (i - 1 + tabs.length) % tabs.length;
        else if (event.key === "Home") next = 0;
        else if (event.key === "End") next = tabs.length - 1;
        activate(next);
        tabs[next].focus();
      });
    });

    activate(0);
  }

  /* ---------------------------------------------------------------
     Gallery carousel + lightbox
     --------------------------------------------------------------- */
  const gallery = document.querySelector("[data-showcase-gallery]");
  if (gallery) {
    const thumbs = Array.from(gallery.querySelectorAll("[data-gallery-index]"));
    const image = gallery.querySelector("[data-gallery-image]");
    const featureLink = gallery.querySelector("[data-showcase-lightbox]");
    const caption = gallery.querySelector("[data-gallery-caption]");
    const currentEl = gallery.querySelector("[data-gallery-current]");
    const totalEl = gallery.querySelector("[data-gallery-total]");
    const prevBtn = gallery.querySelector("[data-gallery-prev]");
    const nextBtn = gallery.querySelector("[data-gallery-next]");

    const items = thumbs.map(button => {
      const img = button.querySelector("img");
      const label = button.getAttribute("aria-label") || "";
      return {
        button,
        src: img ? img.getAttribute("src") : "",
        alt: label,
        caption: label
      };
    });

    if (totalEl) totalEl.textContent = String(items.length).padStart(2, "0");

    let active = 0;
    let timer;

    const show = (index, focusThumb = false) => {
      if (!items.length) return;
      active = (index + items.length) % items.length;
      const item = items[active];

      image.classList.add("is-changing");
      window.clearTimeout(timer);
      timer = window.setTimeout(() => {
        image.src = item.src;
        image.alt = item.alt;
        if (featureLink) {
          featureLink.href = item.src;
          featureLink.setAttribute("aria-label", `Open ${item.caption} at full size`);
        }
        if (caption) caption.textContent = item.caption;
        if (currentEl) currentEl.textContent = String(active + 1).padStart(2, "0");

        items.forEach((entry, i) => {
          const on = i === active;
          entry.button.classList.toggle("is-active", on);
          entry.button.setAttribute("aria-pressed", String(on));
        });

        item.button.scrollIntoView({
          behavior: reduceMotion() ? "auto" : "smooth",
          block: "nearest",
          inline: "nearest"
        });

        requestAnimationFrame(() => image.classList.remove("is-changing"));
        if (focusThumb) item.button.focus();
      }, 140);
    };

    items.forEach((item, i) => {
      item.button.addEventListener("click", () => show(i));
    });
    if (prevBtn) prevBtn.addEventListener("click", () => show(active - 1));
    if (nextBtn) nextBtn.addEventListener("click", () => show(active + 1));

    gallery.addEventListener("keydown", event => {
      if (event.key === "ArrowLeft") {
        event.preventDefault();
        show(active - 1);
      } else if (event.key === "ArrowRight") {
        event.preventDefault();
        show(active + 1);
      }
    });

    show(0);
  }

  /* ---------------------------------------------------------------
     Promo slides: dots + scroll-sync
     --------------------------------------------------------------- */
  const presentation = document.querySelector("[data-presentation]");
  if (presentation) {
    const viewport = presentation.querySelector(".slide-viewport");
    const slides = Array.from(presentation.querySelectorAll("[data-slide]"));
    const dots = Array.from(presentation.querySelectorAll("[data-slide-to]"));

    const setActive = index => {
      if (index < 0 || index >= slides.length) return;
      slides.forEach((slide, i) => slide.classList.toggle("is-active", i === index));
      dots.forEach((dot, i) => {
        dot.classList.toggle("is-active", i === index);
        if (i === index) dot.setAttribute("aria-current", "true");
        else dot.removeAttribute("aria-current");
      });
    };

    dots.forEach(dot => {
      dot.addEventListener("click", () => {
        const target = slides[Number(dot.dataset.slideTo)];
        if (!target) return;
        target.scrollIntoView({
          behavior: reduceMotion() ? "auto" : "smooth",
          block: "nearest",
          inline: "center"
        });
      });
    });

    if (viewport) {
      const observer = new IntersectionObserver(
        entries => {
          const visible = entries
            .filter(e => e.isIntersecting)
            .sort((a, b) => b.intersectionRatio - a.intersectionRatio);
          if (visible.length) {
            const index = slides.indexOf(visible[0].target);
            if (index >= 0) setActive(index);
          }
        },
        { root: viewport, threshold: [0.5, 0.7, 0.9] }
      );
      slides.forEach(slide => observer.observe(slide));

      viewport.addEventListener("keydown", event => {
        if (event.key !== "ArrowLeft" && event.key !== "ArrowRight") return;
        event.preventDefault();
        const current = slides.findIndex(s => s.classList.contains("is-active"));
        const next = event.key === "ArrowRight"
          ? Math.min(current + 1, slides.length - 1)
          : Math.max(current - 1, 0);
        const target = slides[next];
        if (target) {
          target.scrollIntoView({
            behavior: reduceMotion() ? "auto" : "smooth",
            block: "nearest",
            inline: "center"
          });
        }
      });
    }

    setActive(0);
  }

  /* ---------------------------------------------------------------
     Lightbox (shared by gallery + promo slides)
     --------------------------------------------------------------- */
  const lightbox = document.querySelector(".lightbox");
  if (lightbox && typeof lightbox.showModal === "function") {
    const image = lightbox.querySelector("img");
    const caption = lightbox.querySelector("figcaption");
    const closeBtn = lightbox.querySelector(".lightbox-close");
    let lastFocus = null;

    const open = (href, alt, text) => {
      lastFocus = document.activeElement;
      image.src = href;
      image.alt = alt || "";
      caption.textContent = text || alt || "";
      lightbox.showModal();
      if (closeBtn) closeBtn.focus();
    };

    document.querySelectorAll("[data-lightbox]").forEach(link => {
      link.addEventListener("click", event => {
        event.preventDefault();
        const thumb = link.querySelector("img");
        const cap = link.closest("article")?.querySelector(".promo-caption")?.textContent || "";
        open(link.href, thumb ? thumb.alt : "", cap);
      });
    });

    document.querySelectorAll("[data-showcase-lightbox]").forEach(link => {
      link.addEventListener("click", event => {
        event.preventDefault();
        const img = link.querySelector("img");
        const galleryCaption = document.querySelector("[data-gallery-caption]")?.textContent || "";
        open(link.href, img ? img.alt : "", galleryCaption);
      });
    });

    if (closeBtn) {
      closeBtn.addEventListener("click", () => {
        lightbox.close();
        if (lastFocus) lastFocus.focus();
      });
    }
    lightbox.addEventListener("click", event => {
      if (event.target === lightbox) {
        lightbox.close();
        if (lastFocus) lastFocus.focus();
      }
    });
  }

  /* ---------------------------------------------------------------
     Reveal-on-scroll (progressive enhancement, no-op w/ reduced motion)
     --------------------------------------------------------------- */
  if (!reduceMotion() && "IntersectionObserver" in window) {
    const targets = document.querySelectorAll(".section-head, .feature-card, .steps li, .permission-card, .download-card");
    targets.forEach(el => el.setAttribute("data-reveal", ""));

    const io = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add("is-visible");
            io.unobserve(entry.target);
          }
        });
      },
      { rootMargin: "0px 0px -8% 0px", threshold: 0.08 }
    );
    targets.forEach(el => io.observe(el));
  }
})();