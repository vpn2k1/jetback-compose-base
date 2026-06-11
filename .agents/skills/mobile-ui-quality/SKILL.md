---
name: mobile-ui-quality
description: Use when designing, implementing, or reviewing mobile UI quality in this project, including Compose Multiplatform screens, Material 3 styling, accessibility, adaptive layouts, loading/error/empty states, dark mode, touch targets, and visual polish.
---

# Mobile UI Quality

Use this skill to make screens feel production-ready, accessible, and maintainable.

Before changing UI, read `.agents/rules/ui-quality.md`. For deeper Compose behavior, read `.agents/knowledge/jetpack-compose/compose-guidelines.md`; for recomposition or stability issues, read `.agents/knowledge/jetpack-compose/performance.md`.

## UI Standards

- Use Material 3 components and project-local styling conventions.
- Support dark mode unless the request explicitly scopes it out.
- Preserve readable contrast for text, icons, dividers, and disabled states.
- Use practical minimum touch targets around 48dp for primary interactive controls.
- Avoid overlapping text, clipped labels, unstable row heights, and layout shifts.
- Give lists stable keys when rendering domain items.
- Keep typography proportional to the surface: compact panels need compact headings.

## Required States

For data-driven screens, account for:

- loading
- refreshing
- empty
- error with retry when retry is possible
- content
- permission denied or unavailable platform capability when applicable

## Responsive Rules

- Prefer adaptive constraints over fixed pixel layouts.
- Keep phone portrait, larger phones, tablets, desktop, and web targets in mind.
- Use scroll containers when content can exceed viewport height.
- Keep primary actions reachable and predictable.
- Do not rely on Android-only UI resources from `commonMain`.

## Accessibility

- Add semantic labels for icon-only actions and meaningful images.
- Do not use color as the only signal for status.
- Keep focus order natural.
- Avoid tiny controls, low contrast, and text embedded in decorative images.

## Review Pass

Before finishing UI work, check:

- screen states are represented
- text fits on small screens
- actions have clear affordances
- dark mode does not break contrast
- loading and error states do not trap the user
- platform-specific limitations are called out
