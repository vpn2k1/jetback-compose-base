# UI Module Structure Rules

Use this structure when creating or refactoring Compose screens and screen-level UI modules.

## Folder Layout

Each screen should live in a feature folder under `ui/<feature>`. Prefer the
Home-style layout: keep the main screen file directly in the feature folder,
place screen-owned UI in `items/`, and place item-specific helper functions or
UI data mapping in `modules/`.

Example:

```text
ui/home/
  HomeScreen.kt
  items/
    HomeScreenCategoryIconItem.kt
    HomeScreenDashboardItem.kt
    HomeScreenExpenseItem.kt
    HomeScreenQuickEntryItem.kt
    HomeScreenSyncStatusItem.kt
  modules/
    HomeScreenDashboardModule.kt
    HomeScreenExpenseFormModule.kt
    HomeScreenGoogleSyncModule.kt
    HomeScreenQuickEntryModule.kt
```

Rules:

- The main screen file is named after the screen composable, for example `ui/home/HomeScreen.kt`.
- Do not create an extra `HomeScreen/`, `ReportScreen/`, or similar grouping folder for new screen refactors unless an existing local structure already requires it.
- `items/` contains screen-owned UI composables, including sections, panels, repeated rows, cards, chips, cells, and list item renderers.
- `modules/` contains non-composable helper functions, derived UI state, formatting decisions, item-specific calculations, and small data models used by `items/`.
- Do not put composable UI in `modules/` for new screen refactors.
- Business logic that belongs to domain, repositories, validation, persistence, networking, or state holders must stay outside `ui/<feature>/modules/`.
- Do not place unrelated screen components in another screen folder.
- Shared UI used by multiple screens belongs under `ui/base/component` or another intentional shared package, not inside a single screen folder.

## Packages

Use feature-based packages that match the Home structure:

- Main screen: `com.namvu.myapplication.ui.<feature>`
- Items: `com.namvu.myapplication.ui.<feature>.items`
- Modules: `com.namvu.myapplication.ui.<feature>.modules`

Avoid adding the screen name as an extra package segment, such as `ui.home.HomeScreen.modules`.

## Naming

Child item and module names must include the parent screen name.

Examples:

- `HomeScreenExpenseItem`
- `HomeScreenRecentExpenseItem`
- `HomeScreenDashboardItem`
- `HomeScreenQuickEntryModule`
- `HomeScreenGoogleSyncModule`
- `ReportScreenInsightItem`
- `BudgetScreenProgressModule`

Avoid:

- `ExpenseItem`
- `BudgetCard`
- `QuickAdd`
- `Header`

This keeps imports searchable and prevents name collisions as screens grow.

## Compose Boundaries

- The main `*Screen.kt` file owns route-level wiring, high-level loading/error states, and route-level dialogs.
- `items/` files render UI and receive state/callbacks from the main screen or parent item.
- `modules/` files should expose pure functions or lightweight UI-state models for item rendering.
- Keep parsing, validation, repositories, persistence, networking, and business rules outside composables and outside UI modules when they are domain concerns.

## Migration

When touching an older screen that still has many private composables in one file:

- Move only the components related to your change unless a broader refactor is requested.
- Preserve behavior first; do not mix large visual redesigns with structural-only moves.
- Keep file moves small enough that review remains easy.
- When splitting a large screen, keep the route-level `*Screen.kt` as the shell, move UI composables to `items/`, and move item-specific helper logic to `modules/`.
