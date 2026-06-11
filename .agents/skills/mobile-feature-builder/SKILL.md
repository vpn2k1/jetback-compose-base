---
name: mobile-feature-builder
description: Use when creating or expanding a mobile app feature in this Kotlin Multiplatform project, including screens, routes, state models, events/actions, controllers/viewmodels, repositories, platform hooks, and tests. Do not use for simple one-line fixes or unrelated Gradle-only work.
---

# Mobile Feature Builder

Use this skill to create production-shaped features without scattering code across the app.

## Feature Checklist

When adding a feature or screen, check whether the request needs:

- route/navigation entry
- immutable screen state
- user events or callbacks
- one-shot effects for navigation, snackbar, share, permissions, or platform commands
- stateless screen composable
- leaf composables with narrow props
- controller/viewmodel/state holder
- repository or platform bridge
- tests for state transitions and pure logic

## Workflow

1. Read the closest existing feature and match its naming, package, route, and state pattern.
2. Read `.agents/rules/naming.md`, `.agents/rules/state-management.md`, and `.agents/rules/architecture.md`.
3. Put platform-neutral UI, models, state, and presentation logic in `commonMain`.
4. Add platform-specific behavior through `expect`/`actual` or injected interfaces.
5. Keep the first implementation complete for normal, loading, error, and empty states when applicable.
6. Wire navigation in the navigation layer; avoid hardcoded navigation decisions in leaf UI.
7. Add targeted tests for validators, reducers, mapping, or controller behavior when logic changes.

## State And Events

- Prefer immutable state objects with clear defaults.
- Keep raw editable input separate from parsed or validated values.
- Keep one-shot effects out of persistent state.
- Avoid storing lambdas, mutable state objects, or platform objects inside screen state.
- Guard no-op updates when repeated events would emit the same state.

## UI Boundaries

- Route composables wire dependencies, collect state, and execute effects.
- Screen composables render state and expose callbacks.
- Leaf composables render small pieces and emit precise events.
- Business logic, parsing, validation, and repository calls should not live in composables.

## Done Criteria

- The feature is discoverable through the appropriate route or list.
- All active targets either compile or have explicit placeholder implementations.
- User-visible states are handled.
- Validation command was run or the blocker is reported.
