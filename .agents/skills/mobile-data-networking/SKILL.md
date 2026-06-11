---
name: mobile-data-networking
description: Use when adding or changing mobile data flow in this Kotlin Multiplatform app, including API clients, repositories, DTO/domain mapping, persistence, cache, offline behavior, authentication tokens, secure storage, retries, and error handling.
---

# Mobile Data And Networking

Use this skill for production-safe data flow across shared and platform code.

Before changing data flow, read `.agents/rules/architecture.md`, `.agents/rules/security.md`, and `.agents/knowledge/kotlin/kotlin-guidelines.md`.

## Boundaries

- UI consumes presentation state, not raw transport concerns.
- Repositories own data source coordination and mapping.
- DTOs model API payloads; domain or UI models should be explicit when behavior depends on them.
- Platform storage, permissions, and secure APIs stay behind `expect`/`actual` or interfaces.

## Networking Rules

- Verify dependency coordinates and multiplatform target support before adding a networking library.
- Configure timeouts deliberately.
- Represent failures with explicit error types or result wrappers.
- Avoid swallowing exceptions silently.
- Add retry only where the operation is safe and the UX supports it.
- Do not log tokens, secrets, request bodies with private data, or personally identifiable information.

## Persistence And Cache

- Define whether data is memory-only, cached, persisted, or offline-first.
- Keep cache invalidation rules understandable.
- Prefer key-value storage for simple settings and structured storage for relational/query-heavy data.
- Keep migrations explicit when changing persisted schemas.
- Avoid blocking UI threads with file, database, or network work.

## Auth And Security

- Never hardcode secrets or production tokens.
- Store sensitive tokens in platform secure storage.
- Refresh token behavior should be explicit and testable.
- Keep authorization failure handling visible to the user when it affects app flow.

## Testing

- Test mapping, validation, error handling, and repository fallback behavior.
- Use fakes or mock engines instead of live network calls in unit tests.
- Cover success, empty, timeout, unauthorized, and malformed data paths when relevant.
