# Testing And Release Rules

Use the smallest validation that proves the change, and broaden checks when shared contracts change.

## Testing

- Test pure logic, validators, mapping, state transitions, and failure paths.
- Use platform tests for platform-specific behavior.
- Avoid live network calls in unit tests.
- Add regression tests near changed behavior.
- Name tests by observable behavior.

## Validation Commands

- Shared logic: `./gradlew :shared:jvmTest`
- Android impact: `./gradlew :androidApp:assembleDebug`
- Broad shared/API/source-set changes: `./gradlew :shared:jvmTest :androidApp:assembleDebug`
- Web, desktop, and iOS work should use the relevant README or platform toolchain command.

## Release Readiness

- Check permissions, privacy strings, versioning, signing, crash reporting, analytics, and feature flags when release behavior changes.
- Report commands run, failures, skipped validation, and residual platform risk.
