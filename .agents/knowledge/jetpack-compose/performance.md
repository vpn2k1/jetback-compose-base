# Compose Performance Notes

Use this reference when screens recompose too often, scroll poorly, or show layout instability.

## State Shape

- Minimize broad state reads high in the tree.
- Pass narrow props to leaf composables.
- Reuse unchanged nested state when possible.
- Avoid deriving expensive values inside composable bodies without memoization.

## Lists

- Provide stable keys for domain-backed lazy list items.
- Keep item content stable and avoid recreating expensive objects per item.
- Move heavy transformations out of item composables.

## Layout

- Avoid layouts whose size changes unexpectedly between loading and content states.
- Keep image and media containers dimensioned before content loads.
- Prefer predictable constraints over repeated measurement-heavy composition.

## Diagnostics

- First inspect state ownership and read boundaries.
- Then consider `remember`, `derivedStateOf`, stable parameter types, or compiler metrics when the issue persists.
