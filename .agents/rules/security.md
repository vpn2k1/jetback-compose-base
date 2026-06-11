# Security And Privacy Rules

Treat mobile data, logs, permissions, and storage as production-sensitive.

## Secrets

- Never hardcode secrets, production tokens, private keys, or credentials.
- Do not commit generated local config containing secrets.
- Ask before adding a new secret-handling mechanism.

## Storage

- Store sensitive tokens through platform secure storage.
- Keep token refresh and logout behavior explicit.
- Avoid persisting personal data unless the feature requires it.

## Logging And Analytics

- Do not log tokens, passwords, private request bodies, or personally identifiable information.
- Analytics event names and properties must avoid sensitive data.
- Crash reporting and analytics setup should be reviewed when touched.

## Permissions

- Request only permissions required by the feature.
- Handle denied, permanently denied, and unavailable states.
- Keep platform permission strings user-understandable.
