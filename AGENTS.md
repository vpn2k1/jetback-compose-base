# AGENTS.md

## Project Overview

- This is a Kotlin Multiplatform project targeting Android, iOS, Desktop JVM, JavaScript, and Wasm.
- Shared Compose Multiplatform UI and business logic live under `shared/src/commonMain/kotlin/com/namvu/myapplication`.
- Platform-specific implementations live in the matching source sets, for example `androidMain`, `iosMain`, `jvmMain`, `jsMain`, and `wasmJsMain`.
- Android app entry code lives in `androidApp`; iOS entry code lives in `iosApp`; desktop entry code lives in `desktopApp`; web entry code lives in `webApp`.
- MediaPipe model assets are stored in `shared/src/androidMain/assets` and are also managed by the `downloadMediaPipeModels` Gradle task.

## Working Rules

- Prefer shared code in `commonMain` when behavior is platform-neutral.
- Add or update `expect`/`actual` declarations only when a platform needs native behavior.
- Keep Compose UI state explicit and stable; prefer small state holder/controller changes over large screen rewrites.
- Use existing package names, naming style, and feature structure before introducing new folders.
- Do not remove generated app icons, model assets, Gradle wrapper files, or Xcode project files unless the user asks for that cleanup.
- Ask before adding new production dependencies or downloading large model files.

## Validation

- For shared logic changes, run `./gradlew :shared:jvmTest` when feasible.
- For Android-impacting changes, run `./gradlew :androidApp:assembleDebug` when feasible.
- For broad shared API or source-set changes, prefer `./gradlew :shared:jvmTest :androidApp:assembleDebug`.
- For web-only work, run the relevant Wasm or JS Gradle task from the README when feasible.
- If a command cannot run because of sandboxing, network access, missing SDKs, or time, report that clearly.

## Codex Skills

- Use `$kmp-compose-mediapipe` for tasks that modify shared Compose UI, platform source sets, MediaPipe feature screens, model assets, or Gradle validation in this project.
