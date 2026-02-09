# Nexa Clinical Transcription App

An on-device Android demo app for recording clinical voice notes, organizing them into sessions, and (when running on supported Qualcomm hardware) performing local AI transcription and summarization via **NexaSDK** on the Hexagon NPU.

## What This App Does

- **Record** an audio note (mic permission), show a live waveform, and save it as a session.
- **Browse sessions** in a searchable list.
- **Open a session** to see playback + waveform scrub, and two tabs:
  - **Transcription** (ASR)
  - **Summary / Session Notes** (LLM), with **Copy** and **Export** actions.
- **Export** session notes to a `.txt` file using Android Sharesheet.
- **Model Center** screen to manage downloadable model artifacts (optional; depends on network availability and your environment).
- **Bounty Bench** screen that surfaces “ready/not ready” status for demo requirements (runtime + models).

If Nexa runtime is unavailable (e.g., emulator / non-Qualcomm device), the app still runs and shows UX, but AI actions are disabled with a clear message.

## APK

- Local build output (not intended to be committed to git): `artifacts/app-bounty-debug.apk`
- SHA256 (current local build):
  - `0d0904ffb9b36c3cf5576c8a0c51c69edb768b63dce829c064754dada7dff09a`

For sharing, upload the APK as a **GitHub Release asset** (APK is often >100MB, which many git hosts reject in normal commits). Then link it here:

```text
https://github.com/DarkStarStrix/nexa-clinical-transcription-app/releases/latest
```

## Build And Run (Reproducible)

### Prereqs

- Android Studio (or CLI tools) with an Android SDK installed
- JDK 17
- A device or test farm image capable of running the app (AI features require Qualcomm + Nexa compatible runtime)

### Configure SDK Path

Create `local.properties` in the repo root:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

### Build (CLI)

This project uses product flavors:

- `dev` (developer/test utilities)
- `bounty` (bounty submission configuration)

Build the bounty debug APK:

```bash
./gradlew :app:assembleBountyDebug
```

The APK will be at:

```text
app/build/outputs/apk/bounty/debug/app-bounty-debug.apk
```

### Install / Run

With a connected device:

```bash
adb install -r app/build/outputs/apk/bounty/debug/app-bounty-debug.apk
```

Or, from Gradle:

```bash
./gradlew :app:installBountyDebug
```

## Where And Why NexaSDK Is Used

NexaSDK is used to run **on-device inference** (ASR + LLM) locally, targeting Qualcomm acceleration when available.

Key integration points:

- Dependency wiring:
  - `app/build.gradle.kts` includes NexaSDK (`ai.nexa:core`) and related native backends.
- Runtime initialization and health:
  - `app/src/main/java/demo/nexa/clinical_transcription_demo/PlauApp.kt`
  - `app/src/main/java/demo/nexa/clinical_transcription_demo/nexa/NexaRuntime.kt`
  - These centralize “runtime ready / unavailable” status so the UI can degrade gracefully on non-supported devices.
- ASR + LLM wrappers:
  - `app/src/main/java/demo/nexa/clinical_transcription_demo/llm/NexaLlmEngine.kt`
  - (and the corresponding ASR engine in the `asr/` package)
  - These are the boundary where Nexa APIs are called, and where model selection is applied.
- Feature-level usage:
  - `app/src/main/java/demo/nexa/clinical_transcription_demo/presentation/NotePlaybackViewModel.kt`
  - Starts transcription/summarization and reports progress/errors back to Compose UI.

## Notes / Known Limitations

- Model files are **not bundled** in this repository to keep it lightweight and to avoid large-file hosting issues. Use the in-app **Model Center** (or your own internal artifact hosting) to provide models.
- Some cloud test environments block DNS/network. Model downloads will fail there. The app is designed so that core UX still works and download actions show errors instead of crashing.
- Lint may warn about **16KB alignment** in a native library from NexaSDK; this is dependency-provided and not something the app can fix without an upstream release.
