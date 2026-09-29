# 555 Assist — Local Android AI Assistant MVP

Minimal Android + AI Assistant concept inspired by the technological language of Kamen Rider Faiz without turning the app into a literal Rider UI.

## Compatibility
- Android Gradle Plugin: **8.11.1**
- Gradle: **8.13**
- Kotlin: **2.3.21**
- JDK: **17**
- compileSdk: **35**
- targetSdk: **35**
- minSdk: **26**
- Jetpack Compose BOM: **2025.05.00**
- Activity Compose: **1.10.1**
- App version: **1.4.0**

## Run
1. Extract the ZIP to a fresh folder.
2. Open the **Signal555Android** folder in Android Studio.
3. Set **Gradle JDK = 17**.
4. Let Gradle Sync finish.
5. Install Android SDK Platform **35** if requested.
6. Run the `app` configuration.

## Navigation
Top-level destinations keep persistent bottom navigation:
- Home
- Check
- Ask
- History
- Profile

Detail/sub-flow screens hide bottom navigation and use back navigation:
- Screenshot Check
- Link Check
- Camera Assist
- Document AI
- Next Action

## Real local features
### V5 — Screenshot Check
- Gallery image picker
- Bundled ML Kit OCR
- Explainable local risk analysis
- Results saved to local History

### V6 — Link Check + History
- Local URL normalization and heuristic inspection
- HTTPS, IP URL, Punycode, shortener, TLD, subdomain, port and lure-word checks
- Persistent local History using SharedPreferences
- No network reputation lookup

### V7 — Camera Assist
- CameraX live preview
- Runtime camera permission
- Rear-camera photo capture
- Torch toggle
- Bundled ML Kit OCR
- Local risk analysis
- Camera results saved to History

### V8 — Local Document AI
Document AI is now functional for PDF files without a server:
- Android system PDF picker
- PDF pages rendered locally with `PdfRenderer`
- Bundled ML Kit OCR for digital or scanned PDF pages
- Up to **12 pages** analyzed per run to keep mobile processing reasonable
- Extractive local summary (does not invent new facts)
- Local keyword extraction
- Local in-document search
- Heuristic action-item extraction
- Document analysis saved to History
- No INTERNET permission is required for this flow

## Still prototype/mock
- Ask Assistant / local LLM
- Rich Next Action execution
- Online reputation/malware lookups

## Important notes
The current "AI" layer is intentionally mixed:
- ML Kit OCR is an on-device machine-learning component.
- Risk analysis, URL inspection, document summarization, keyword extraction and action-item extraction are deterministic local heuristics/algorithms.
- The app does **not** yet include an on-device generative LLM.
- Risk scores are indicators, not definitive fraud/malware verdicts.
