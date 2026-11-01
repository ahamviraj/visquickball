# Vi's Quick Ball - An Assistive Floating Touch Ball
<table>
  <tr>
    <td rowspan="3" width="100" align="center" valign="middle">
      <img src="https://raw.githubusercontent.com/ahamviraj/visquickball/refs/heads/main/website/assets/app-icon.webp" width="80" alt="Vi's Quick Ball Logo">
    </td>
    <td>
      <h1>Vi's Quick Ball</h1>
    </td>
  </tr>
  <tr>
    <td>
      <strong>An Assistive Floating Touch Ball</strong>
    </td>
  </tr>
  <tr>
    <td>
      <sub>Compatibility: Android 8.1+ (API 27+)</sub>
    </td>
  </tr>
</table>
<img src="https://raw.githubusercontent.com/ahamviraj/visquickball/refs/heads/main/slides/1.jpg" alt="Vi's Quick Ball - Back Gesture" width="100%" style="height: auto; border-radius: 12px;">

Vi's Quick Ball is a native Android assistive navigation utility. Its floating concentric-circle ball requests Back on tap, Home on double-tap, and Recent Apps on long-press, and can be dragged to dock at a screen edge. Android or a device manufacturer may restrict Recent Apps or background behavior.

🌐 **Official Website:** https://ahamviraj.github.io/visquickball/

🔒 **Privacy Policy:** https://ahamviraj.github.io/visquickball/privacy-policy.html

**Prepared next release:** 1.0.1 (version code 2; not published)

**Package ID:** `com.viraj.viquickball`

## Developer

- Developer: **Viraj**
- Display name: **Aham Viraj**
- Email: [ahamviraj@gmail.com](mailto:ahamviraj@gmail.com)
- GitHub: [ahamviraj](https://github.com/ahamviraj)
- Official website: [Vi's Quick Ball](https://ahamviraj.github.io/visquickball/)
- Privacy policy: [Privacy Policy](https://ahamviraj.github.io/visquickball/privacy-policy.html)
- Instagram: [@ahamveeraj](https://www.instagram.com/ahamveeraj/)
- Project repository: [ahamviraj/visquickball](https://github.com/ahamviraj/visquickball)

## Version

The repository contains a local `v1.0.0` tag, while the source at that tag and a local signed APK report `versionName = "1.0"` and `versionCode = 1`. The next prepared release is **1.0.1** with **version code 2** so it can advance installed code-1 packages. I could not verify whether the tag or APK was publicly distributed; no new release or tag has been created, and no public download is claimed.

## Features

- Four-layer gray and white floating ball with a small press animation.
- Drag to the nearest screen edge; dock position is stored locally.
- Optional Android Quick Settings tile and home-screen widget.
- Setup dashboard for overlay and Accessibility Service permissions.
- Privacy policy available in the app offline and in `website/privacy-policy.html`.
- No ads or advertising SDK in the inspected project.

## Promotional Slides

<p align="center"><a href="slides/1.jpg"><img src="slides/1.jpg" alt="Promotional slide with a phone home screen and the headline Tired of old navigation? Try Quick Ball." width="760"></a></p>
<p align="center"><a href="slides/2.jpg"><img src="slides/2.jpg" alt="Vi's Quick Ball promotional graphic with a phone preview and product overview panels." width="760"></a></p>
<p align="center"><a href="slides/3.jpg"><img src="slides/3.jpg" alt="Promotional collage of Android home screens with Quick Ball; large text reads Place it anywhere." width="760"></a></p>
<p align="center"><a href="slides/4.jpg"><img src="slides/4.jpg" alt="Two angled Android home screens show Quick Ball on opposite sides, with the words Right and Left." width="760"></a></p>
<p align="center"><a href="slides/5.jpg"><img src="slides/5.jpg" alt="Promotional illustration showing one tap for Back and a double tap for Home." width="760"></a></p>
<p align="center"><a href="slides/6.jpg"><img src="slides/6.jpg" alt="Promotional illustration of a long press on Quick Ball and the Recent Apps overview." width="760"></a></p>
<p align="center"><a href="slides/7.jpg"><img src="slides/7.jpg" alt="Promotional graphic showing a Quick Ball tile in Android Quick Settings and a Quick Ball home-screen widget." width="760"></a></p>

## Screenshots

<p align="center"><a href="screenshots/back.png"><img src="screenshots/back.png" alt="Android home screen with the concentric Quick Ball overlay docked at the right edge." width="180"></a> <a href="screenshots/dog.png"><img src="screenshots/dog.png" alt="Shopping app page with a dog photo and the Quick Ball overlay at the right edge." width="180"></a> <a href="screenshots/down.png"><img src="screenshots/down.png" alt="Android home screen with Quick Ball positioned near the bottom edge." width="180"></a> <a href="screenshots/files.png"><img src="screenshots/files.png" alt="Android file manager showing internal storage folders with the Quick Ball overlay on the right." width="180"></a></p>
<p align="center"><a href="screenshots/gallery.png"><img src="screenshots/gallery.png" alt="Android photo gallery grid with the Quick Ball overlay at the right edge." width="180"></a> <a href="screenshots/left.png"><img src="screenshots/left.png" alt="Android home screen with Quick Ball docked along the left edge." width="180"></a> <a href="screenshots/main.png"><img src="screenshots/main.png" alt="Android home screen with its wallpaper and the Quick Ball floating above the lower app icons." width="180"></a> <a href="screenshots/recent.png"><img src="screenshots/recent.png" alt="Android Recent Apps overview with Quick Ball visible at the right edge." width="180"></a></p>
<p align="center"><a href="screenshots/right.png"><img src="screenshots/right.png" alt="Android home screen with Quick Ball docked along the right edge." width="180"></a> <a href="screenshots/shortcut_in_Quick_Tiles_main.png"><img src="screenshots/shortcut_in_Quick_Tiles_main.png" alt="Expanded Android Quick Settings panel with the Quick Ball tile switched on." width="180"></a> <a href="screenshots/shortcut_in_Quick_Tiles.png"><img src="screenshots/shortcut_in_Quick_Tiles.png" alt="Android Quick Settings tile editor showing Quick Ball among the available tiles." width="180"></a> <a href="screenshots/up.png"><img src="screenshots/up.png" alt="Android home screen with Quick Ball positioned near the top edge." width="180"></a></p>
<p align="center"><a href="screenshots/widget_on_home.png"><img src="screenshots/widget_on_home.png" alt="Android home screen displaying the Vi's Quick Ball widget with its ON state and setup and turn-off actions." width="180"></a> <a href="screenshots/widget.png"><img src="screenshots/widget.png" alt="Android widget picker preview for Vi's Quick Ball, with an Add widget button." width="180"></a></p>

## Permissions

- **Display over other apps:** shows the floating ball above ordinary apps.
- **Accessibility Service:** requests Back, Home, and Recent Apps actions and observes window package/class metadata to detect some system panels. It does not retrieve accessibility nodes or window text.

Both permissions are enabled manually in Android Settings. The Android app currently declares no Internet permission and has no app-initiated network requests. See [Privacy Policy](PRIVACY_POLICY.md) for the verified data-handling details.

## Build the Android app

**Build requirements:** JDK 17 or newer must be installed and available to Gradle. On Windows, configure `JAVA_HOME` or select the Gradle JDK in Android Studio under **Settings → Build, Execution, Deployment → Build Tools → Gradle**. This project uses the supplied Gradle wrapper: use `gradlew.bat` on Windows or `./gradlew` on Linux, from the repository root.

For example, build the debug APK with:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is normally at `app/build/outputs/apk/debug/app-debug.apk`. A release build does not read signing credentials and is unsigned by default:

```powershell
.\gradlew.bat test assembleRelease
```

To explicitly sign a release with the existing developer key, request it with Gradle's long-form project property (works in PowerShell):

```powershell
.\gradlew.bat assembleRelease --project-prop=viquickball.developerSigning=true
```

That opt-in requires the ignored root `keystore.properties` and its configured keystore. Keep both out of source control. A normal F-Droid source build must omit this property; it should produce an unsigned APK for F-Droid's signing/verification process.

The release changelog draft is at `fastlane/metadata/android/en-US/changelogs/2.txt`. The app has not been submitted to or listed by F-Droid.

## Preview the website

From the repository root:

```powershell
python -m http.server 8000 --directory website
```

Then open `http://localhost:8000`. Website details and hosting instructions are in [website/README.md](website/README.md).

## Publish the static website

The website is static HTML/CSS/JavaScript and needs no backend. GitHub Pages is prepared through `.github/workflows/pages.yml`; configure **Settings → Pages → Source → GitHub Actions** after publishing the repository. Vercel can serve the site with `website/` as its project root, no build command, and `.` as output directory. See [website/README.md](website/README.md) for steps.

The website is prepared for Vercel at `website/` as its project root. No website has been deployed, and no public app download or store listing is configured yet.

## F-Droid preparation

The F-Droid metadata is a **draft** in [`fdroid-metadata-draft/`](fdroid-metadata-draft/README.md); it is not a submission. Before submitting, publish the reviewed source commit with a matching immutable version tag, confirm the release version/code and update history, and have F-Droid maintainers validate the recipe. The Gradle default build does not require or use the private signing key. To preserve updates from developer-signed installs, F-Droid must verify a source build and publish with the upstream signing identity; that has not yet been established. An APK signed only with F-Droid's independent key is not a seamless update for developer-signed installations. See the draft notes for requirements and current signature-tool limitations.

## Configure the canonical policy URL

After you publish and verify the policy page, set the real HTTPS URL in `app/src/main/java/com/viraj/viquickball/PrivacyPolicyConfig.kt` (`CANONICAL_URL`). Rebuild and sign the app release. Before publishing the website, fill verified download, support email, and repository destinations in `website/site-config.js`; empty configuration values intentionally render explanatory text instead of fake links.

## Privacy and publication

- The developer contact and repository links are now configured with the supplied details.
- Add a verified app listing/download URL only after publication.
- After hosting, copy the final canonical HTTPS Privacy Policy URL into the Android app configuration and the Indus Appstore dashboard.
- Recheck the policy if dependencies, network access, or data handling change.

To report a privacy concern, email [ahamviraj@gmail.com](mailto:ahamviraj@gmail.com). The published policy is available at [ahamviraj.github.io/visquickball/privacy-policy.html](https://ahamviraj.github.io/visquickball/privacy-policy.html).

## License

Vi's Quick Ball is distributed under the **GNU General Public License v3.0 only** (`GPL-3.0-only`). See the repository's [LICENSE](LICENSE) file for the complete license terms. This is version 3 only; it does not grant use under later GPL versions.

Third-party libraries and platform components remain subject to their respective licenses. The project license does not change those terms.
