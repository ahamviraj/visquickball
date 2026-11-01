(() => {
  "use strict";
  const reduce = () => matchMedia("(prefers-reduced-motion: reduce)").matches;
  const $$ = (s, r = document) => Array.from(r.querySelectorAll(s));

  /* Download link comes from site-config.js (window.VI_QUICK_BALL_SITE.downloadUrl) */
  const cfg = window.VI_QUICK_BALL_SITE || {};
  const safeHttps = v => { try { const u = new URL(v); return u.protocol === "https:" ? u.href : ""; } catch (_) { return ""; } };
  const dl = safeHttps(cfg.downloadUrl);
  if (dl) {
    $$("[data-download-link]").forEach(a => { a.href = dl; a.hidden = false; a.rel = "noopener noreferrer"; });
    $$("[data-download-note]").forEach(n => { n.hidden = true; });
  }
  $$("[data-year]").forEach(n => { n.textContent = String(new Date().getFullYear()); });

  /* Mobile menu */
  const toggle = document.querySelector(".menu-toggle");
  const nav = document.getElementById("site-nav");
  if (toggle && nav) {
    const set = open => { nav.classList.toggle("is-open", open); toggle.setAttribute("aria-expanded", String(open)); };
    toggle.addEventListener("click", () => set(!nav.classList.contains("is-open")));
    nav.addEventListener("click", e => { if (e.target.closest("a")) set(false); });
    document.addEventListener("keydown", e => { if (e.key === "Escape") set(false); });
  }

  /* Hero: feature tabs <-> phone screenshots */
  const sel = document.querySelector("[data-feature-selector]");
  if (sel) {
    const tabs = $$(".feature-tab", sel), shots = $$(".phone-shot");
    const cap = document.getElementById("hero-caption");
    const activate = i => {
      tabs.forEach((t, n) => {
        const on = n === i;
        t.classList.toggle("is-active", on);
        t.setAttribute("aria-selected", String(on));
        t.tabIndex = on ? 0 : -1;
      });
      shots.forEach((s, n) => s.classList.toggle("is-active", n === i));
      if (cap) cap.textContent = tabs[i].querySelector(".tab-desc").textContent;
    };
    tabs.forEach((t, i) => {
      t.addEventListener("click", () => activate(i));
      t.addEventListener("keydown", e => {
        const k = { ArrowDown: 1, ArrowRight: 1, ArrowUp: -1, ArrowLeft: -1 }[e.key];
        let n = k ? (i + k + tabs.length) % tabs.length : e.key === "Home" ? 0 : e.key === "End" ? tabs.length - 1 : -1;
        if (n < 0) return;
        e.preventDefault(); activate(n); tabs[n].focus();
      });
    });
    activate(0);

    /* Autoplay: advance every 1.5s. Pauses on hover/focus and while the tab is hidden;
       disabled entirely when the visitor prefers reduced motion. */
    const INTERVAL = 1500;
    if (!reduce()) {
      const hero = document.querySelector(".hero");
      let timer = null;
      const current = () => tabs.findIndex(t => t.classList.contains("is-active"));
      const stop = () => { clearInterval(timer); timer = null; };
      const start = () => {
        if (timer || document.hidden) return;
        timer = setInterval(() => activate((current() + 1) % tabs.length), INTERVAL);
      };
      hero.addEventListener("mouseenter", stop);
      hero.addEventListener("mouseleave", start);
      hero.addEventListener("focusin", stop);
      hero.addEventListener("focusout", start);
      // Restart the 1.5s countdown after a manual pick so it doesn't skip immediately
      sel.addEventListener("click", () => { stop(); if (!hero.matches(":hover")) start(); });
      document.addEventListener("visibilitychange", () => (document.hidden ? stop() : start()));
      start();
    }
  }

  /* Scrollers: prev/next buttons that follow scroll position */
  $$("[data-scroller]").forEach(sc => {
    const track = sc.querySelector(".scroller-track");
    const prev = sc.querySelector("[data-prev]"), next = sc.querySelector("[data-next]");
    const sync = () => {
      prev.disabled = track.scrollLeft < 4;
      next.disabled = track.scrollLeft + track.clientWidth >= track.scrollWidth - 4;
    };
    const go = d => track.scrollBy({ left: d * track.clientWidth * .8, behavior: reduce() ? "auto" : "smooth" });
    prev.addEventListener("click", () => go(-1));
    next.addEventListener("click", () => go(1));
    track.addEventListener("scroll", sync, { passive: true });
    track.addEventListener("keydown", e => {
      if (e.key === "ArrowRight") { e.preventDefault(); go(1); }
      if (e.key === "ArrowLeft") { e.preventDefault(); go(-1); }
    });
    addEventListener("resize", sync);
    sync();

  });

  /* Marquees (screenshots + promo slides): duplicate the set once so the CSS animation
     loops seamlessly. Pure CSS motion, no timer. With reduced motion the row stays a
     normal scroller with arrows. */
  $$("[data-marquee]").forEach(box => {
    const track = box.querySelector(".scroller-track");
    if (!track) return;
    if (reduce()) { box.querySelector(".scroller-btns").style.display = "flex"; return; }
    const inner = document.createElement("div");
    inner.className = "marquee-inner";
    while (track.firstChild) inner.appendChild(track.firstChild);
    $$(".shot, .slide", inner).forEach(f => {
      const c = f.cloneNode(true);
      c.setAttribute("aria-hidden", "true");
      $$("a", c).forEach(a => { a.tabIndex = -1; });
      inner.appendChild(c);
    });
    track.appendChild(inner);
    ["tabindex", "role", "aria-label"].forEach(n => track.removeAttribute(n));
    // constant speed (~50px per second) regardless of screen size
    const setSpeed = () => inner.style.setProperty("--marquee-duration", (inner.scrollWidth / 2 / 50) + "s");
    setSpeed();
    addEventListener("load", setSpeed);
    addEventListener("resize", setSpeed);
  });

  /* Lightbox */
  const lb = document.querySelector(".lightbox");
  if (lb && typeof lb.showModal === "function") {
    const img = lb.querySelector("img"), cap = lb.querySelector("figcaption"), close = lb.querySelector(".lightbox-close");
    let last = null;
    $$("[data-lightbox]").forEach(a => a.addEventListener("click", e => {
      e.preventDefault();
      last = document.activeElement;
      const t = a.querySelector("img");
      img.src = a.href; img.alt = t ? t.alt : "";
      cap.textContent = a.closest("figure")?.querySelector("figcaption")?.textContent || "";
      lb.showModal(); close.focus();
    }));
    const shut = () => lb.close();
    close.addEventListener("click", shut);
    lb.addEventListener("click", e => { if (e.target === lb) shut(); });
    lb.addEventListener("close", () => { if (last) last.focus(); });
  }

  /* Reveal on scroll */
  if (!reduce() && "IntersectionObserver" in window) {
    const io = new IntersectionObserver(es => es.forEach(en => {
      if (en.isIntersecting) { en.target.classList.add("is-visible"); io.unobserve(en.target); }
    }), { rootMargin: "0px 0px -8% 0px", threshold: .08 });
    $$(".section-head,.feature-row,.steps li,.perm,.download-card").forEach(el => { el.setAttribute("data-reveal", ""); io.observe(el); });
  }
})();