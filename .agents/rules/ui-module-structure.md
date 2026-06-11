# UI Module Structure Rules

Use this structure when creating or refactoring Compose screens and screen-level UI modules.

## Folder Layout

Each screen should live in a feature folder under `ui/<feature>`. Prefer the
Home-style layout: keep the main screen file directly in the feature folder and
place screen-owned child UI in sibling `items/` and `modules/` folders.

Example:

```text
ui/home/
  HomeScreen.kt
  items/
    HomeScreenCategoryIconItem.kt
    HomeScreenExpenseItem.kt
    HomeScreenSyncStatusItem.kt
  modules/
    HomeScreenBodyModule.kt
    HomeScreenDashboardModule.kt
    HomeScreenExpenseFormModule.kt
    HomeScreenGoogleSyncModule.kt
    HomeScreenQuickEntryModule.kt
```

Rules:

- The main screen file is named after the screen composable, for example `ui/home/HomeScreen.kt`.
- Do not create an extra `HomeScreen/`, `ReportScreen/`, or similar grouping folder for new screen refactors unless an existing local structure already requires it.
- `items/` contains leaf UI pieces, repeated rows, cards, chips, cells, and list item renderers.
- `modules/` contains larger screen sections, panels, flows, and grouped UI blocks used by the main screen.
- Do not place unrelated screen components in another screen folder.
- Shared UI used by multiple screens belongs under `ui/base/component` or another intentional shared package, not inside a single screen folder.

## Packages

Use feature-based packages that match the Home structure:

- Main screen: `com.namvu.myapplication.ui.<feature>`
- Items: `com.namvu.myapplication.ui.<feature>.items`
- Modules: `com.namvu.myapplication.ui.<feature>.modules`

Avoid adding the screen name as an extra package segment, such as `ui.home.HomeScreen.modules`.

## Naming

Child item and module names must include the parent screen/module name.

Examples:

- `HomeScreenExpenseItem`
- `HomeScreenRecentExpenseItem`
- `HomeScreenBodyModule`
- `HomeScreenQuickEntryModule`
- `ReportScreenInsightItem`
- `BudgetScreenProgressItem`

Avoid:

- `ExpenseItem`
- `BudgetCard`
- `QuickAdd`
- `Header`

This keeps imports searchable and prevents name collisions as screens grow.

## Compose Boundaries

- The main `*Screen.kt` file owns route-level wiring, high-level loading/error states, and route-level dialogs.
- `modules/` files render meaningful screen sections and receive state/callbacks from the main screen or parent module.
- `items/` files render focused leaf components and receive the narrowest useful props.
- Business logic, parsing, validation, repositories, and persistence must stay outside composables.

## Migration

When touching an older screen that still has many private composables in one file:

- Move only the components related to your change unless a broader refactor is requested.
- Preserve behavior first; do not mix large visual redesigns with structural-only moves.
- Keep file moves small enough that review remains easy.
- When splitting a large screen, keep the route-level `*Screen.kt` as the shell and move larger content sections to `modules/` and repeated leaf UI to `items/`.
