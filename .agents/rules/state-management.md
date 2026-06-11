# State Management Rules

Use unidirectional data flow for mobile app screens.

## State

- Screen state should be immutable and equality-friendly.
- Keep raw editable input separate from parsed, validated, or persisted values.
- Derive values from existing state when derivation is cheap and deterministic.
- Avoid storing `MutableState`, lambdas, coroutine scopes, platform objects, controllers, or navigation objects in screen state.
- Keep UI-only visual state local to composables when it does not affect business behavior.

## Events And Effects

- User actions should flow through callbacks, events, or named state-holder functions.
- State holders own business decisions and state transitions.
- One-shot commands such as navigation, snackbar, share, permission prompts, and external intents should use effects or route-level callbacks.
- Do not model one-shot events as persistent `Boolean` flags that must be consumed.

## Async Work

- Preserve existing content during refresh when possible.
- Represent loading, refreshing, success, empty, and error separately when the UX needs them.
- Cancel or ignore stale requests when newer user intent supersedes older work.
- Report recoverable errors through explicit state and retry actions.
