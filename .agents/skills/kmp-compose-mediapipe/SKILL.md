---
name: kmp-compose-mediapipe
description: Use for this repository's Kotlin Multiplatform, Compose Multiplatform, MediaPipe demo screens, source-set wiring, Gradle validation, and platform actual implementations. Do not use for unrelated generic Kotlin questions outside this project.
---

# Kotlin Multiplatform Compose MediaPipe Workflow

Follow this workflow when changing this repository.

## Scope

- Android, iOS, Desktop JVM, JavaScript, and Wasm targets.
- Shared UI and feature logic in `shared/src/commonMain/kotlin/com/namvu/myapplication`.
- Platform implementations in `shared/src/androidMain`, `shared/src/iosMain`, `shared/src/jvmMain`, `shared/src/jsMain`, and `shared/src/wasmJsMain`.
- App shells in `androidApp`, `iosApp`, `desktopApp`, and `webApp`.
- MediaPipe task demos, controllers, routes, model assets, and Gradle wiring.

## First Checks

1. Read `README.md` for current run and test commands.
2. Inspect `settings.gradle.kts`, root `build.gradle.kts`, and the affected module build file before editing Gradle configuration.
3. Locate the existing feature pattern before adding new code:
   - common API: `shared/src/commonMain/kotlin/com/namvu/myapplication/<Feature>.kt`
   - Android implementation: `shared/src/androidMain/kotlin/com/namvu/myapplication/<Feature>.android.kt`
   - iOS implementation: `shared/src/iosMain/kotlin/com/namvu/myapplication/<Feature>.ios.kt`
   - JVM implementation: `shared/src/jvmMain/kotlin/com/namvu/myapplication/<Feature>.jvm.kt`
   - web implementation: `shared/src/jsMain` or `shared/src/wasmJsMain`
   - demo UI: `shared/src/commonMain/kotlin/com/namvu/myapplication/ui/demo`
   - navigation: `shared/src/commonMain/kotlin/com/namvu/myapplication/navigation`

## Implementation Rules

- Prefer `commonMain` for shared models, controllers, routes, and Compose UI.
- Keep platform-specific APIs behind existing `expect`/`actual` boundaries.
- Match existing Compose Material 3 conventions and avoid introducing a second UI architecture.
- Keep MediaPipe model file names and task names consistent with `shared/build.gradle.kts`.
- When adding a new MediaPipe feature, update the feature model/list, route, detail/demo screen, common API, and required platform implementations together.
- Keep large assets out of code changes unless the user explicitly asks for asset updates.

## Validation

Run the narrowest useful Gradle checks:

```bash
./gradlew :shared:jvmTest
./gradlew :androidApp:assembleDebug
```

Use broader checks when touching source-set wiring, dependencies, or shared APIs:

```bash
./gradlew :shared:jvmTest :androidApp:assembleDebug
```

If validation cannot run, capture the reason and any partial command output in the final response.

## Output Expectations

- Summarize changed files and behavior.
- Mention validation commands run or why they were skipped.
- Call out platform-specific follow-up work when a target still uses a placeholder implementation.
