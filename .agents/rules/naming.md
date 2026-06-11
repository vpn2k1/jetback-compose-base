# Naming Rules

Use these conventions unless nearby code clearly uses a different local pattern.

## Packages And Files

- Use lowercase package names.
- Keep files close to the feature or platform source set they belong to.
- Name files after the main public type or screen they contain.
- Use suffixes consistently:
  - `Screen` for stateless screen composables.
  - `Route` for navigation/dependency wiring composables.
  - `State` for immutable UI state.
  - `Event` or `Action` for user input events, matching existing code.
  - `Effect` for one-shot UI commands.
  - `Controller`, `ViewModel`, or existing project term for state holders.
  - `.android.kt`, `.ios.kt`, `.jvm.kt`, `.wasmJs.kt`, `.js.kt` for platform implementations.

## Kotlin Types

- Classes, interfaces, objects, composables, and enum entries use idiomatic Kotlin naming.
- Boolean state should read naturally: `isLoading`, `hasError`, `canRetry`.
- Avoid vague names like `Manager`, `Helper`, `Util`, `Data`, or `Info` unless the project already uses them deliberately.
- Prefer domain names over technical names in UI state: `selectedTask` is better than `selectedItemData`.

## Compose Names

- Public composables use PascalCase and describe the UI they render.
- Callback names should describe user intent: `onRetryClick`, `onTaskSelected`, `onDismissRequest`.
- Modifier parameters should be named `modifier` and appear first among optional parameters.
- Preview functions should be named after the component and state when previews are added.
