# F-Droid metadata draft

`com.viraj.viquickball.yml` is a staging draft, not a submitted or maintainer-approved `fdroiddata` metadata file.

## Before copying it into `fdroiddata`

1. The local `v1.0.0` tag points to `58c9b50f82689ebd6043435d8e0fe428ac36c3e6`; that commit exists on the public GitHub repository and the tagged Gradle configuration declares version `1.0` / code `1`. The hash `58c9b50f82689ebd6043435d8e0fe428ac36ce36` supplied in one release brief is not a commit found by GitHub. The public release page and its assets could not be retrieved in this environment, so independently confirm the published APK and release status before relying on them.
2. Publish the reviewed source changes and create a version tag only after the release is finalized. Replace the build's `TODO_RELEASE_TAG_FULL_COMMIT` with that tag's full immutable commit hash. F-Droid's metadata requires a commit hash; a tag label or branch name is not sufficient.
3. `1.0.1` / code `2` is the appropriate next local version if `v1.0.0` / code `1` is indeed the latest public release. Code 2 advances the verified tagged code 1, but check the public Releases page for later version codes before finalizing.
4. Ask F-Droid maintainers to verify the recipe against the then-current build server, Android SDK 37, and Gradle 9.6. The recipe has not been run through `fdroid lint` or an F-Droid build server here. AGP 9.4.1 requires Gradle 9.6 and JDK 17 or newer; F-Droid's current reproducible-build notes list JDK 17, 21, and 25 support.
5. Build and publish the developer-signed release only after the unsigned source build reproduces it. Add a versioned `Binaries:` URL and the verified `AllowedAPKSigningKeys:` only after that release exists and its APK signature is confirmed. Do not add binary-verification metadata based only on a local file.
6. Request F-Droid's upstream-signature distribution path and provide the signature material through the process its maintainers approve. Do not accept F-Droid's independent signing key as a seamless upgrade path for developer-signed installs: a different signer cannot update them under Android's normal package signing rules.

F-Droid documents signature copying/reproducibility as the route for using an upstream developer signature. Its current reproducible-build notes report that `apksigcopier` cannot verify signatures produced by `apksigner` from Build Tools 35.0.0-rc1 and later. The documented cause is backwards-incompatible APK alignment handling: newer `apksigner` forcibly replaces existing alignment padding and changes default page alignment behavior. The existing developer-signed APK was signed using Build Tools 37, so exact upstream-signature publication is **not yet established**. Do not infer that the app build itself is irreproducible from this signature-tooling issue; test the same signed release and F-Droid unsigned output with the current supported copy/verification tools, or obtain maintainer guidance first.

### Validation still required

The local validation host used for this draft has no Docker executable, `fdroid`/`fdroidserver`, or `apksigcopier`. WSL is installed but its distro enumeration fails with `E_ACCESSDENIED`, so no supported Linux/F-Droid build environment was available. The only local developer-signed APK is version `1.0` / code `1`; the candidate unsigned APK is `1.0.1` / code `2`, so they are not corresponding artifacts and must not be compared as a reproducibility pair.

After publishing the exact reviewed source commit and its developer-signed `1.0.1` / code `2` APK, validate in a supported Linux F-Droid environment:

```sh
fdroid lint com.viraj.viquickball
fdroid build com.viraj.viquickball --server
apksigcopier compare path/to/upstream-1.0.1.apk path/to/fdroid-unsigned-1.0.1.apk
```

Run `apksigner verify --verbose --print-certs` on the developer APK and confirm its certificate fingerprint is the existing developer signing identity. The official `fdroid build --server`/publish workflow and successful signature comparison are still prerequisites; YAML parsing alone is not official metadata validation.

No keystore, password, signing config, release APK, tag, GitHub release, or F-Droid submission is stored or created by this draft.
