---
name: mobile-testing-release
description: Use when preparing mobile app validation, tests, CI checks, release readiness, versioning, signing, crash reporting, analytics readiness, build commands, or deployment checklists for this Kotlin Multiplatform project.
---

# Mobile Testing And Release

Use this skill to make changes verifiable and release-aware.

Before changing validation, CI, or release behavior, read `.agents/rules/testing-release.md` and `.agents/rules/security.md` when privacy, permissions, signing, logs, or analytics are involved.

## Test Strategy

- Prefer fast tests for pure logic, validators, mappers, reducers, and state holders.
- Use platform tests when behavior depends on platform APIs.
- Avoid live network calls in automated tests.
- Add regression tests near the changed behavior.
- Keep test names behavior-focused.

## Validation Commands

Use the narrowest command that proves the change:

```bash
./gradlew :shared:jvmTest
./gradlew :androidApp:assembleDebug
```

For broad shared or dependency changes:

```bash
./gradlew :shared:jvmTest :androidApp:assembleDebug
```

For web, desktop, or iOS-specific work, use the task documented in `README.md` or the platform toolchain.

## Release Readiness

Check when release-impacting code changes:

- app version and build number
- signing configuration
- permissions and privacy strings
- crash reporting initialization
- analytics event naming and privacy
- feature flags or rollout guards
- minSdk and targetSdk impact
- offline/error behavior
- accessibility basics

## Reporting

Final responses should include:

- tests or builds run
- failures and likely causes
- validation not run and why
- residual release risk
- platform-specific follow-up work
