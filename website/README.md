# Vi's Quick Ball website

Static HTML/CSS/vanilla JavaScript site. No backend, no build step.

## Redesign summary (2026)

The site was fully redesigned from a light pink theme into a dark, cinematic product showcase.

- Single unified stylesheet (`styles.css`) replaced four overlapping style systems.
- New hero: CSS-only smartphone mockup (Pixel-style frame, punch-hole camera, side-button hit zone, reflection overlay) with a synchronized feature selector. Selecting a feature crossfades the phone's screenshot and updates the caption.
- Screenshot gallery rebuilt as a stable carousel with prev/next, thumbnails, keyboard arrows, and lightbox.
- Promotional slides now use native horizontal scroll-snap with sync dots — no vertical scroll hijacking.
- Privacy policy text preserved verbatim; inline `<style>` removed and replaced by `.policy` rules in `styles.css`.
- 404 page re-themed to match.
- `prefers-reduced-motion` respected across phone animation, crossfades, slide motion, and reveal-on-scroll.

## Verified project details

- App: Vi's Quick Ball - An Assistive Floating Touch Ball
- Website release label: `1.0.0`
- Developer display name: Aham Viraj; name: Viraj
- Email: [ahamviraj@gmail.com](mailto:ahamviraj@gmail.com)
- GitHub: [ahamviraj](https://github.com/ahamviraj)
- Instagram: [@ahamveeraj](https://www.instagram.com/ahamveeraj/)
- Repository: [ahamviraj/visquickball](https://github.com/ahamviraj/visquickball)

The Android Gradle configuration currently says `versionName = "1.0"` (`versionCode = 1`). The website's `v1.0.0` label does not mean a public APK download is available.

## Feature → screenshot mapping used in the hero

| Feature tab         | Screenshot file                                  |
|---------------------|--------------------------------------------------|
| Tap — Back          | `assets/screenshots/back.png`                    |
| Double-tap — Home   | `assets/screenshots/main.png`                    |
| Press — Recent Apps | `assets/screenshots/recent.png`                  |
| Drag & dock         | `assets/screenshots/down.png`                    |
| Quick Settings tile | `assets/screenshots/shortcut_in_Quick_Tiles_main.png` |
| Home-screen widget  | `assets/screenshots/widget_on_home.png`          |
| Works over any app  | `assets/screenshots/dog.png`                     |

If any of these files are renamed or removed, update the `data-hero-shot` indexes and `src` attributes in `index.html` accordingly.

## Preview locally

From the repository root:

`python -m http.server 8000 --directory website`

Then open http://localhost:8000.

## Download link

`downloadUrl` in `site-config.js` remains empty. The primary download CTA is hidden until a verified public HTTPS listing exists. Add it there only after publication; do not use a preview or placeholder URL.

## Deploy

Same as before — Vercel root = `website/`, or GitHub Pages workflow in `.github/workflows/pages.yml`. No new files or build steps were added.