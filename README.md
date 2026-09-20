# 555 Assist — Android UI Prototype

Minimal Android + AI Assistant concept inspired by the technological language of Kamen Rider Faiz without turning the app into a literal Rider UI.

## Compatibility build (v1.0.1)
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

## Run
1. Extract this ZIP to a fresh folder.
2. Open the **Signal555Android** folder in Android Studio.
3. Make sure **Gradle JDK = 17** in Settings > Build, Execution, Deployment > Build Tools > Gradle.
4. Let Gradle Sync finish.
5. Install Android SDK Platform **35** if Android Studio asks for it.
6. Run the `app` configuration on an emulator or Android device.

### Important if you opened the old project before
Do **not** keep using the old `.gradle` metadata from the AGP 9.4 build. Open this fixed ZIP as a fresh folder. If Android Studio still remembers the old model, use **File > Sync Project with Gradle Files**. A full cache reset should not normally be necessary.

## Current scope
The screens and navigation are functional as a UI prototype. Screenshot analysis, camera analysis, document analysis and AI replies are currently mock/demo results. Real CameraX, OCR, PDF parsing, URL reputation checks and AI API integration are the next implementation layer.


## Build compatibility fix

This package uses AGP 8.11.1, Gradle 8.13, JDK 17 target, and the Kotlin 2.x `compilerOptions` DSL.
The deprecated `kotlinOptions { jvmTarget = "17" }` block has been removed.

## V3 compatibility fix
Removed explicit `androidx.compose.foundation.layout.weight` imports from `AskScreen.kt` and `HomeScreen.kt`.
`Modifier.weight(...)` is resolved from the implicit `RowScope` / `ColumnScope` receiver.

## V4 navigation hierarchy
Top-level destinations keep the persistent bottom navigation:
- Home
- Check
- Ask
- History
- Profile

Detail/sub-flow screens intentionally hide the bottom navigation and use a back action:
- Screenshot Check
- Link Check
- Camera Assist
- Document AI
- Next Action

The Check tab now opens a dedicated Check Hub instead of jumping directly into Screenshot Check.
