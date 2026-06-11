# Architecture Rules

Keep the codebase easy to reason about across Kotlin Multiplatform targets.

## Source Sets

- Put shared models, validation, formatting, navigation route definitions, and presentation logic in `commonMain`.
- Put platform APIs behind `expect`/`actual` declarations or interfaces.
- Implement all required actuals for active targets, or provide explicit placeholders and call them out.
- Do not put Android, UIKit, DOM, or JVM-only objects into shared UI state.

## Feature Boundaries

- Route-level code wires navigation, dependencies, state collection, and effects.
- Screen-level code renders state and exposes callbacks.
- Leaf UI renders focused pieces with narrow props.
- Repositories coordinate data sources and map external data.
- DTOs should not leak deeply into UI unless the existing project intentionally follows that style.

## Dependency Policy

- Add dependencies to the narrowest source set that needs them.
- Prefer version catalog entries in `gradle/libs.versions.toml`.
- Verify target support before adding AndroidX, Jetpack, networking, persistence, or image-loading libraries to `commonMain`.
- Ask before changing Kotlin, Compose, AGP, or major dependency versions.
