# AGENTS.md

## Project Overview

- This is a Kotlin Multiplatform project targeting Android, iOS, Desktop JVM, JavaScript, and Wasm.
- Shared Compose Multiplatform UI and business logic live under `shared/src/commonMain/kotlin/com/namvu/myapplication`.
- Platform-specific implementations live in the matching source sets, for example `androidMain`, `iosMain`, `jvmMain`, `jsMain`, and `wasmJsMain`.
- Android app entry code lives in `androidApp`; iOS entry code lives in `iosApp`; desktop entry code lives in `desktopApp`; web entry code lives in `webApp`.
- Reusable base UI components, theme tokens, state models, and localized strings live under `shared/src/commonMain`.

## Working Rules

- Prefer shared code in `commonMain` when behavior is platform-neutral.
- Add or update `expect`/`actual` declarations only when a platform needs native behavior.
- Keep Compose UI state explicit and stable; prefer small state holder/controller changes over large screen rewrites.
- Use existing package names, naming style, and feature structure before introducing new folders.
- Do not remove generated app icons, model assets, Gradle wrapper files, or Xcode project files unless the user asks for that cleanup.
- Ask before adding new production dependencies or downloading large model files.
- Apply the coding rules under `.agents/rules` when naming, modeling state, designing UI, and organizing architecture.
- Use `.agents/knowledge` as repo-local reference material for Kotlin and Jetpack Compose guidance.

## Mobile App Standards

- Architecture must follow unidirectional data flow: UI renders state, user actions call callbacks, and state holders update immutable state.
- Composables should render UI and emit events; parsing, validation, networking, persistence, and business rules belong outside composable bodies.
- Every user-facing screen should handle loading, error, empty, and content states when those states are possible.
- Keep navigation semantic and typed where feasible; avoid leaking platform navigation APIs into shared business or presentation logic.
- Use Material 3 conventions consistently, including dark mode compatibility, accessible contrast, and minimum practical touch targets.
- Lists should use stable keys when rendering domain items.
- Do not store platform objects such as Android `Context`, UIKit views, DOM nodes, file handles, or permission launchers in shared screen state.
- Do not hardcode secrets, tokens, private URLs, or personally identifiable information in source files.
- Token handling, secure storage, permissions, logging, and analytics must be explicit and reviewed when touched.
- Prefer repository/domain mapping for external data: DTOs should not leak deeply into UI unless the project already uses that pattern deliberately.
- Add tests for pure logic, state transitions, validation, mapping, and failure paths when behavior changes.

## Validation

- For shared logic changes, run `./gradlew :shared:jvmTest` when feasible.
- For Android-impacting changes, run `./gradlew :androidApp:assembleDebug` when feasible.
- For broad shared API or source-set changes, prefer `./gradlew :shared:jvmTest :androidApp:assembleDebug`.
- For web-only work, run the relevant Wasm or JS Gradle task from the README when feasible.
- If a command cannot run because of sandboxing, network access, missing SDKs, or time, report that clearly.

## Codex Skills

- Use `$base-kotlin-multiplatform` for tasks that modify Kotlin Multiplatform structure, shared Compose UI, platform source sets, Gradle configuration, app shells, or validation in this project.
- Use `$mobile-feature-builder` when creating or expanding app features, screens, routes, state holders, and tests.
- Use `$mobile-ui-quality` when designing, implementing, or reviewing mobile UI quality, accessibility, responsive layout, and production states.
- Use `$mobile-data-networking` when adding API, repository, persistence, caching, auth token, or offline behavior.
- Use `$mobile-testing-release` when preparing tests, CI checks, release readiness, versioning, signing, crash reporting, or deployment validation.

## Agent Reference Layout

- `.agents/skills`: Codex skills and task workflows.
- `.agents/rules`: repository coding rules such as naming, state management, architecture, UI, testing, and security.
- `.agents/knowledge`: Kotlin and Jetpack Compose reference notes to load only when needed.
- `.codex/rules`: Codex command execution policy, not coding style rules.
