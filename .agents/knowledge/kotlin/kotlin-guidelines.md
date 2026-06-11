# Kotlin Guidelines

Use this reference when Kotlin implementation details matter.

## Style

- Prefer immutable `val` over `var`.
- Prefer data classes for immutable state and value objects.
- Use sealed interfaces or sealed classes for closed state, event, and result hierarchies.
- Keep functions small and named by intent.
- Avoid nullable chains when explicit state models would be clearer.

## Coroutines And Flow

- Keep coroutine ownership in state holders, repositories, or platform layers.
- Avoid launching coroutines directly from leaf composables.
- Represent async results explicitly.
- Prefer structured concurrency over detached work.
- Cancel stale work when a newer user action supersedes it.

## Errors

- Do not swallow exceptions silently.
- Map technical failures into domain or UI failures at boundaries.
- Keep retry behavior explicit and safe.

## Multiplatform

- Keep common code free of platform-only APIs.
- Use `expect`/`actual` only when common code truly needs a platform capability.
- Prefer interfaces when dependency injection or test doubles are useful.
- Verify library target support before adding dependencies to `commonMain`.
