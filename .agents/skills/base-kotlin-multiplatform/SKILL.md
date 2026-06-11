---
name: base-kotlin-multiplatform
description: Use for this repository's base Kotlin Multiplatform and Compose Multiplatform app work, including shared UI, state management, source-set wiring, expect/actual platform code, Gradle configuration, app shells, and validation. Do not use for unrelated generic Kotlin questions outside this project.
---

# Base Kotlin Multiplatform App Workflow

Use this skill when changing a base Kotlin Multiplatform application that targets Android, iOS, Desktop JVM, JavaScript, and Wasm with Compose Multiplatform.

## Project Shape

- Shared app code belongs in `shared/src/commonMain/kotlin`.
- Platform-specific code belongs in the matching source set: `androidMain`, `iosMain`, `jvmMain`, `jsMain`, or `wasmJsMain`.
- App entry modules are `androidApp`, `iosApp`, `desktopApp`, and `webApp`.
- Build configuration lives in `settings.gradle.kts`, root `build.gradle.kts`, module `build.gradle.kts` files, and `gradle/libs.versions.toml`.

## Workflow

1. Read the existing code first and follow local conventions.
2. Identify the concern: shared UI, state management, navigation, platform API, resources, build configuration, or tests.
3. Read the relevant `.agents/rules` file before making a structural or behavioral change.
4. Read the relevant `.agents/knowledge` file only when deeper Kotlin or Jetpack Compose guidance is needed.
5. Prefer the smallest change that fits the existing architecture.
6. Keep platform-specific APIs behind `expect`/`actual` declarations or interfaces.
7. Verify dependency coordinates and target support before adding libraries to `commonMain`.
8. Run the narrowest useful Gradle validation when feasible.

## Reference Routing

- Naming conventions: `.agents/rules/naming.md`
- State modeling and events: `.agents/rules/state-management.md`
- Architecture boundaries: `.agents/rules/architecture.md`
- UI quality and accessibility: `.agents/rules/ui-quality.md`
- Testing and release checks: `.agents/rules/testing-release.md`
- Security and privacy: `.agents/rules/security.md`
- Kotlin reference: `.agents/knowledge/kotlin/kotlin-guidelines.md`
- Jetpack Compose reference: `.agents/knowledge/jetpack-compose/compose-guidelines.md`
- Compose performance reference: `.agents/knowledge/jetpack-compose/performance.md`

## Skill Routing

- Use `$mobile-feature-builder` for new or expanded features, screens, routes, state holders, and feature tests.
- Use `$mobile-ui-quality` for UI polish, accessibility, adaptive layouts, complete screen states, and dark mode.
- Use `$mobile-data-networking` for API clients, repositories, cache, persistence, auth, and offline behavior.
- Use `$mobile-testing-release` for validation strategy, CI, release readiness, signing, crash reporting, analytics, and deployment risk.

## Architecture Defaults

- Prefer unidirectional data flow: UI renders state, user actions call callbacks, state holders update immutable state.
- Keep composables focused on rendering and user events; avoid business rules in composable bodies.
- Store screen state in immutable data classes or stable state holders.
- Use one-shot effects for navigation, snackbar, share, or platform commands instead of consume-once booleans in state.
- Keep UI-only state local to composables when it is purely visual, such as focus, scroll, expansion, or animation progress.
- Preserve the project's existing MVI, MVVM, or controller pattern when it is coherent.

## Compose Rules

- Route-level composables may collect state, call platform/navigation APIs, and wire dependencies.
- Screen-level composables should be mostly stateless renderers that receive state and callbacks.
- Leaf composables should receive the narrowest useful state.
- Use Compose Multiplatform resources from common code; do not use Android `R` from `commonMain`.
- Keep Material 3 styling consistent with the existing app.
- Avoid adding new UI frameworks or competing architecture layers unless the user asks.

## Kotlin Multiplatform Rules

- Put common models, validation, formatting, navigation routes, and presentation logic in `commonMain`.
- Add `expect` declarations only when common code needs a platform capability.
- Implement all required `actual` declarations for active targets.
- If a platform cannot support a feature yet, provide an explicit placeholder implementation and call it out.
- Do not assume AndroidX or Jetpack libraries work in `commonMain`; verify multiplatform artifacts first.
- Avoid platform objects in shared state, such as Android `Context`, UIKit views, file handles, or browser DOM objects.

## Gradle And Dependencies

- Prefer version catalog entries in `gradle/libs.versions.toml`.
- Inspect the target source sets before adding a dependency.
- Add dependencies to the narrowest source set that needs them.
- Ask before adding new production dependencies, changing Kotlin/AGP/Compose versions, or downloading large assets.
- Keep Gradle changes scoped and avoid broad cleanup unless requested.

## Validation

Use the smallest meaningful check:

```bash
./gradlew :shared:jvmTest
./gradlew :androidApp:assembleDebug
```

For wider source-set, dependency, or shared API changes:

```bash
./gradlew :shared:jvmTest :androidApp:assembleDebug
```

For web-only changes, use the relevant task from `README.md`.

If validation cannot run because of sandboxing, network, SDK setup, or time, say exactly what blocked it.

## Output Expectations

- Summarize the behavior changed, not just files edited.
- Mention validation commands run or skipped.
- Call out any platform target that still needs an implementation.
