# Vi's Quick Ball website

This is a static HTML/CSS/vanilla JavaScript site. It has no backend or build step.

## Verified project details

- App: Vi's Quick Ball - An Assistive Floating Touch Ball
- Website release label: `1.0.0`
- Developer display name: Aham Viraj; name: Viraj
- Email: [ahamviraj@gmail.com](mailto:ahamviraj@gmail.com)
- GitHub: [ahamviraj](https://github.com/ahamviraj)
- Instagram: [@ahamveeraj](https://www.instagram.com/ahamveeraj/)
- Repository: [ahamviraj/visquickball](https://github.com/ahamviraj/visquickball)

The Android Gradle configuration currently says `versionName = "1.0"` (`versionCode = 1`). It was not changed; see the root README for this version discrepancy. The website's version label does not mean a public APK download is available.

## Preview locally

From the repository root, run `python -m http.server 8000 --directory website` and open `http://localhost:8000`. You can also open `website/index.html` directly, but a local server more closely matches static hosting.

## Download link

Developer, email, Instagram, and repository links are the verified values above and are linked directly from the Contact section and footer. The download CTA remains hidden while `downloadUrl` in `site-config.js` is empty. Add a verified public HTTPS listing or download URL there only after publication; do not use a preview or placeholder URL. The Android app's canonical Privacy Policy URL is configured separately in `app/src/main/java/com/viraj/viquickball/PrivacyPolicyConfig.kt` after the site is deployed.

## Deploy with Vercel

The website files (`index.html`, `privacy-policy.html`, styles, scripts, and assets) are directly inside `website/`. There is no framework, package manifest, `public/` subdirectory, or build output directory.

1. Publish the repository to GitHub and import `ahamviraj/visquickball` in Vercel.
2. In the Vercel project settings, set **Root Directory** to `website`.
3. Select **Other** for **Framework Preset**.
4. Leave **Build Command** blank. There is no build step.
5. Leave **Install Command** blank. The site has no package dependencies.
6. Set **Output Directory** to `.` (the `website/` project root). There is no `public/` folder, so this serves `index.html` and the other files from that root.
7. Save the settings and deploy when you are ready. Vercel will provide the actual deployment address; no address is assumed in this project.
8. Open the deployed homepage and `/privacy-policy.html` and check that both load. The site's relative links keep the policy navigation within the deployed site.

Vercel documents that static HTML, CSS, and client-side JavaScript need no build step; with the **Other** preset the output defaults to `public` if present, otherwise `.`. The exact settings above match this website's file layout. See [Configure a Build](https://vercel.com/docs/builds/configure-a-build) and [Project Settings](https://vercel.com/docs/project-configuration/project-settings).

### Configure the final canonical Privacy Policy URL

After Vercel has deployed the site, copy its real HTTPS address and append `/privacy-policy.html`. Put that exact URL in `CANONICAL_URL` in `app/src/main/java/com/viraj/viquickball/PrivacyPolicyConfig.kt`, then rebuild and sign the Android release. The constant remains empty until you set it; the app continues to show its bundled offline policy. Enter the same deployed URL in the Indus Appstore privacy-policy field. Do not use the Vercel project name or a guessed domain as a URL.

## GitHub Pages

GitHub Pages remains available as an alternate host. The repository includes `.github/workflows/pages.yml`, which uploads only the `website/` directory and deploys it through GitHub Actions. After you publish the repository:

1. Open repository **Settings → Pages** and set **Build and deployment → Source** to **GitHub Actions**.
2. The included workflow triggers on pushes to `main`. If your default branch has another name, change `branches: [main]` in `.github/workflows/pages.yml` to that branch. Push the workflow and website, or run **Actions → Deploy website to GitHub Pages → Run workflow**. The workflow publishes the site; do not run it until you choose to publish.
3. Wait for the Pages deployment to finish. GitHub shows the final site URL under **Settings → Pages**. For a project site, the path normally includes the repository name.
4. Verify both the homepage and `/privacy-policy.html` from a browser. Configure that exact HTTPS policy URL in the Android app.

The workflow is prepared but has not been run from this workspace. No hosting settings were changed and no deployment was made.

## Before publishing

- Add a real public app listing/download URL to `site-config.js` only once it exists.
- Confirm the privacy policy's effective date and project data-handling statements still match the release source.
- Deploy the site, obtain its final policy URL, and put it into `PrivacyPolicyConfig.CANONICAL_URL` before building the store release.
- Review the policy and app-store disclosures yourself; this project makes no legal-compliance or store-approval claim.
