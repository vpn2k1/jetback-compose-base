# Jetpack Compose And Compose Multiplatform Guidelines

Use this reference for Compose architecture and UI behavior.

## Composable Boundaries

- Route composables wire dependencies, state collection, navigation, and effects.
- Screen composables render state and expose callbacks.
- Leaf composables should receive narrow props and avoid business logic.
- Keep parsing, validation, networking, persistence, and permission decisions outside composable bodies.

## State

- Hoist state to the lowest owner that needs to coordinate it.
- Keep visual-only state local when it does not affect business rules.
- Use stable immutable state models for screen-level state.
- Avoid storing platform objects or mutable state containers inside screen state.

## Effects

- Use side-effect APIs deliberately and close to the UI concern.
- One-shot app commands should be modeled as effects and executed by route-level code.
- Avoid triggering long-running work repeatedly from recomposition.

## Resources

- Use Compose Multiplatform resources from shared code.
- Do not use Android `R` directly from `commonMain`.
- Keep user-facing strings ready for localization when practical.
