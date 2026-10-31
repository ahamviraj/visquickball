# Vi's Quick Ball Privacy Policy

**Effective date: 31 October 2026**

This policy describes Vi's Quick Ball - An Assistive Floating Touch Ball (package `com.viraj.viquickball`). The app is a navigation utility that displays a floating ball and requests Android navigation actions when you use its gestures.

## 1. Information the app handles

Based on the project implementation reviewed for this policy, the app does not ask you to provide an account, name, contact details, or other profile information. It has no Internet permission, app-initiated network requests, analytics integration, advertising integration, crash-reporting SDK, or remote API integration. The inspected app code does not transmit personal or accessibility data to a developer-controlled server or a third party.

The Accessibility Service receives Android window-state events. The app reads event package and class names locally to identify likely system notification or control panels and adjust overlay visibility. It does not retrieve accessibility nodes, window content, or on-screen text. Gesture actions are passed to Android through its global-action API. General operational failures may be written to Android's local log; the app does not log screen text or accessibility content.

## 2. Accessibility Service

You enable or disable this service yourself in Android Accessibility settings. It is used to request Back, Home, and Recent Apps actions when you tap, double-tap, or long-press the ball. It also observes window-state metadata (package and class names) to detect certain system panels. Its configuration sets window-content retrieval off, and the implementation does not inspect on-screen text or accessibility nodes. Android or a device manufacturer may reject or vary a global action, especially Recent Apps.

## 3. Display over other apps

The Android “Display over other apps” permission allows the Quick Ball overlay to appear above ordinary apps. The app does not use this permission to read other apps' content. You can revoke it in Android Settings.

## 4. Information stored on your device

The app stores a small set of preferences in app-private SharedPreferences: whether Quick Ball is enabled, its selected screen edge and normalized position, and onboarding progress. These values support restoring the ball and the setup experience. They are stored on the device rather than sent by the app to a server.

The Android manifest allows app backup and the project has no rules excluding these preferences. Depending on your Android backup and device-transfer settings, Android may include app data in a system-managed backup or transfer. That is handled by Android and your configured backup provider, not by an app-operated server. You can clear the app's storage in Android Settings or uninstall the app to remove its local data; a system backup may restore backed-up data later.

## 5. Internet access and third parties

The installed Android app does not declare the Internet permission, and the reviewed source contains no network client or remote service integration. Its AndroidX and Material dependencies provide interface and platform support; the inspected dependency declarations do not include advertising, analytics, or crash-reporting SDKs. Google Play Services or other system components may independently process information under their own policies if present on your device; this app does not integrate them to send its data.

## 6. Advertisements

The reviewed project contains no ad placements or advertising SDK, and the app does not display advertisements.

## 7. Sharing

The app does not sell or share app-collected personal information with the developer or third parties because the reviewed implementation does not transmit such information. Android system backup or device-transfer behavior is described above.

## 8. Retention and deletion

Preferences remain in app-private storage while the app is installed, unless cleared or removed by Android. Clear the app's storage or uninstall it to remove those local preferences. Android may retain a backup according to the system settings you use; the developer does not operate a remote data store for this app and cannot delete an Android-managed backup on your behalf.

## 9. Children's privacy

Vi's Quick Ball is a general-purpose Android utility and is not designed to collect personal information from children. The app does not provide an account or data submission feature. A parent or guardian can manage the app and its permissions through Android settings.

## 10. Security

Preferences are kept in the app's private storage and the app does not send them over a network. No method of storage or device backup can be guaranteed completely secure. Keep your device and its backup settings protected.

## 11. Changes to this policy

This policy may be updated when the app or its data handling changes. The current version will be available at the public Privacy Policy page once its hosting URL is configured. The effective date above identifies this version.

## 12. Contact

Developer display name: Aham Viraj. Name: Viraj.

Email: [ahamviraj@gmail.com](mailto:ahamviraj@gmail.com). GitHub: [github.com/ahamviraj](https://github.com/ahamviraj). Instagram: [@ahamveeraj](https://www.instagram.com/ahamveeraj/). Project repository: [github.com/ahamviraj/visquickball](https://github.com/ahamviraj/visquickball).

**Publication detail to complete:** the final hosted Privacy Policy URL is not yet configured in the Android app. No app-store listing or public download destination is currently configured.

## 13. Scope and confirmation

This policy reflects the source code, Android manifest, and declared app dependencies inspected on the effective date. Android's system backup provider behavior depends on your device configuration. If the app's code or dependencies change, review and update this policy before distributing that version.
