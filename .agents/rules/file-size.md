# File Size Rules

Keep files small enough to scan and review quickly.

## Line Limits

- Prefer files between 150 and 200 lines.
- If a file grows beyond 200 lines, split it into smaller focused files unless there is a strong reason not to.
- Do not add new code to an already oversized file when the change can reasonably live in a new focused file.

## Splitting Guidance

- Split by responsibility, not arbitrary line count.
- For Compose UI, follow `.agents/rules/ui-module-structure.md`:
  - main screen shell in `ui/<feature>/<ScreenName>.kt`
  - repeated leaf UI in `ui/<feature>/items/`
  - larger screen sections in `ui/<feature>/modules/`
- For domain/data code, split by main public type or cohesive behavior:
  - models in focused model files
  - repository contracts separate from implementations
  - use cases split when they represent different workflows

## Exceptions

Occasional files over 200 lines are acceptable when splitting would make the code harder to understand, for example:

- generated files
- Gradle or platform configuration files
- compact sealed hierarchies that are clearer together
- migration or schema files that must remain atomic

When making an exception, avoid expanding the file further unless the user explicitly asks.
