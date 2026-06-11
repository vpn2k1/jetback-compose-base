# UI Quality Rules

Build screens that feel production-ready on mobile first and still behave well on other targets.

## Required Screen States

- Handle content, loading, refreshing, empty, and error states when applicable.
- Provide retry for recoverable failures.
- Show permission denied or unavailable feature states when a platform capability is required.

## Layout

- Prefer adaptive constraints over fixed dimensions.
- Use scroll containers when content can exceed viewport height.
- Avoid clipped text, overlapping components, and layout shifts.
- Keep primary actions reachable and predictable.
- Use stable keys in lazy lists.

## Accessibility

- Icon-only actions need semantic labels.
- Do not use color as the only status signal.
- Maintain readable contrast in light and dark mode.
- Use practical touch targets around 48dp for important controls.
- Keep focus order natural.

## Visual Consistency

- Use Material 3 and existing project styling.
- Match typography scale to the surface size.
- Avoid introducing a competing design system without explicit approval.
