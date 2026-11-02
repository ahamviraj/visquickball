# F-Droid metadata draft

`com.viraj.viquickball.yml` is a staging draft, not a submitted or maintainer-approved `fdroiddata` metadata file.

## Before copying it into `fdroiddata`

1. The local `v1.0.0` tag points to `58c9b50f82689ebd6043435d8e0fe428ac36c3e6`; the public release APK was retrieved and verified to use the developer signing certificate. The tagged Gradle configuration declares version `1.0` / code `1`.
2. The `Builds.commit` value is pinned to `2d386bff7e8f170e4463243c4e7726e87936bd06`, the source commit for the public `v1.0.1` tag and release. F-Droid metadata requires the full commit hash as the build revision; the tag and commit resolve to the same source.
3. Version `1.0.1` / code `2` advances the verified public `v1.0.0` release's code 1. Check GitHub Releases for any newer release before submitting this draft.
4. The recipe has not been run through `fdroid lint` or an F-Droid build server here. Android Gradle Plugin 9.4 supports API 37 and requires Gradle 9.6 / JDK 17; the wrapper pins Gradle 9.6 and F-Droid documents build-server JDKs 17, 21, and 25. The API 37 platform/build-tools availability and recipe compatibility still need an F-Droid build-server test.
5. The developer-signed v1.0.1 APK is published and its certificate matches v1.0.0. F-Droid's upstream-signed distribution still requires its source build to match this APK. Only after successful verification should the metadata add a versioned `Binaries:` URL and the confirmed `AllowedAPKSigningKeys:` digest.
6. Request F-Droid's upstream-signature distribution path and provide the signature material through the process its maintainers approve. Do not accept F-Droid's independent signing key as a seamless upgrade path for developer-signed installs: a different signer cannot update them under Android's normal package signing rules.

F-Droid documents signature copying/reproducibility as the route for using an upstream developer signature. Its current reproducible-build notes report that `apksigcopier` cannot verify signatures produced by `apksigner` from Build Tools 35.0.0-rc1 and later. The published APK was verified with Build Tools 37.0.0, but that does not establish which tool version produced its signature. This project does not explicitly pin `buildToolsVersion`; AGP 9.4 documents 36.0.0 as its default. Exact upstream-signature publication is therefore **not yet established**. Do not infer that the app build itself is irreproducible from this signature-tooling issue; compare this exact signed release against the F-Droid unsigned output with the supported copy/verification tools, or obtain maintainer guidance first.

### Validation still required

The current Windows validation host has no Docker, Podman, `fdroid`/`fdroidserver`, or `apksigcopier`. WSL is installed but distro enumeration fails with `E_ACCESSDENIED`, so no supported Linux/F-Droid build environment is currently available. The public v1.0.1 APK must be compared with an F-Droid build of this exact source revision.

In a supported Linux F-Droid environment, validate the published developer-signed `1.0.1` / code `2` APK against the output built from this metadata:

```sh
fdroid lint com.viraj.viquickball
fdroid build com.viraj.viquickball --server
apksigcopier compare path/to/upstream-1.0.1.apk path/to/fdroid-unsigned-1.0.1.apk
```

Run `apksigner verify --verbose --print-certs` on the developer APK and confirm its certificate fingerprint is the existing developer signing identity. The official `fdroid build --server`/publish workflow and successful signature comparison are still prerequisites; YAML parsing alone is not official metadata validation.

This draft contains no keystore, password, signing configuration, or APK, and has not been submitted to F-Droid. The upstream v1.0.1 tag and GitHub release exist separately.
