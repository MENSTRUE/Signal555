# 555 Assist — Local Android AI Assistant MVP

Minimal Android + AI Assistant concept inspired by the technological language of Kamen Rider Faiz without turning the app into a literal Rider UI.

## Compatibility
This revision is intentionally pinned for Android Studio installations whose latest supported Android Gradle Plugin is **AGP 8.11.1**.

- Android Gradle Plugin: **8.11.1**
- Gradle: **8.13**
- Kotlin: **2.3.21**
- JDK: **17**
- compileSdk: **35**
- targetSdk: **35**
- minSdk: **26**
- Jetpack Compose BOM: **2025.05.00**
- Activity Compose: **1.10.1**
- App version: **1.2.0**

## Run
1. Extract the ZIP to a fresh folder.
2. Open the **Signal555Android** folder in Android Studio.
3. Set **Gradle JDK = 17** in Settings > Build, Execution, Deployment > Build Tools > Gradle.
4. Let Gradle Sync finish.
5. Install Android SDK Platform **35** if Android Studio asks for it.
6. Run the `app` configuration on an emulator or Android device.

## Navigation
Top-level destinations keep persistent bottom navigation:
- Home
- Check
- Ask
- History
- Profile

Detail/sub-flow screens hide the bottom navigation and use a back action:
- Screenshot Check
- Link Check
- Camera Assist
- Document AI
- Next Action

## V5 — Local Screenshot Check
Screenshot Check is functional and offline-first:
- Image selection uses Android's gallery/document picker.
- OCR uses bundled ML Kit Text Recognition (`com.google.mlkit:text-recognition:16.0.1`).
- Extracted text is analyzed by an explainable local risk engine.
- Screenshot and OCR text are not sent to a server.
- Results are saved to local History.

## V6 — Local Link Check + Persistent History
Link Check is now functional without opening the URL:
- Normalizes pasted domains/URLs locally.
- Checks HTTP vs HTTPS.
- Detects IP-address URLs, Punycode, shorteners, suspicious TLDs, excessive subdomains/hyphens, sensitive lure words, non-standard ports, and very long URLs.
- Produces an explainable local risk indicator and safety guidance.
- Does **not** perform reputation/malware lookups and therefore does not claim a URL is definitively safe or malicious.
- No INTERNET permission is added for the local checker.

History is now persistent on-device for the MVP:
- Screenshot Check results are saved locally.
- Link Check results are saved locally.
- History can be cleared from the History screen.
- Storage currently uses SharedPreferences to keep the MVP dependency-light; it can be migrated to Room when richer history/query features are needed.

## Still prototype/mock
- Ask Assistant / local LLM
- Camera Assist
- Document AI / PDF parsing
- Network reputation lookups

## Notes
The local risk scores are explainable heuristics. They are safety indicators, not definitive fraud, malware, or trust classifications.


## V7 — Local Camera Assist (v1.3.0)
Camera Assist now uses CameraX for a real live camera preview and photo capture. Captured photos are processed on-device with the bundled ML Kit text recognizer, then passed to the same explainable local risk engine used by Screenshot Check. Camera results are saved to local History.

- CameraX live preview and rear-camera capture
- Runtime camera permission
- Torch toggle
- Bundled/offline OCR
- Local risk analysis
- Camera results in persistent History
- No INTERNET permission added

Current real local features: Screenshot Check, Link Check, Camera Assist, History. Ask and Document AI are still prototype flows.
