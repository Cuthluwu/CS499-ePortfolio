# Version-Two Schema and Data Dictionary

## Relationship diagram

```text
users (one)                                      weight_entries (many)
+----------------------+                         +----------------------+
| PK user_id           |<------------------------| FK user_id           |
| UQ username NOCASE   |   ON DELETE CASCADE     | PK entry_id          |
| password_hash        |                         | weight REAL          |
| password_salt        |                         | entry_date YYYY-MM-DD|
| password_algorithm   |                         | note                 |
| password_iterations  |                         | created_at           |
| created_at           |                         | updated_at           |
+----------------------+                         +----------------------+

migration_issues is an internal audit table used only when an old text record cannot be
converted safely during the version-one to version-two migration.
```

## `users`

| Column | Type and rule | Reason |
|---|---|---|
| `user_id` | `INTEGER PRIMARY KEY AUTOINCREMENT` | Stable internal identity used by relationships and session state. |
| `username` | `TEXT NOT NULL COLLATE NOCASE UNIQUE` | Prevents missing or case-variant duplicate usernames. |
| `password_hash` | `TEXT NOT NULL` | Stores the Base64 PBKDF2 output, never the supplied password. |
| `password_salt` | `TEXT NOT NULL` | Stores the unique random salt required to verify the password. |
| `password_algorithm` | `TEXT NOT NULL` | Records how the verifier was created so a later version can migrate algorithms. |
| `password_iterations` | positive integer | Records the work factor instead of hiding it in application code. |
| `created_at` | ISO timestamp | Supports auditing and future account management. |

## `weight_entries`

| Column | Type and rule | Reason |
|---|---|---|
| `entry_id` | `INTEGER PRIMARY KEY AUTOINCREMENT` | Identifies one measurement; permits several records for one user. |
| `user_id` | required foreign key | Establishes ownership and prevents an entry for a nonexistent account. |
| `weight` | `REAL`, greater than 0 and at most 1500 | Makes the value numeric and rejects clearly invalid states. |
| `entry_date` | ten-character ISO date validated by SQLite `date()` | Provides consistent lexical and chronological ordering and rejects impossible dates. |
| `note` | required text defaulting to empty; max 250 | Supports context without allowing unbounded text. |
| `created_at` | ISO timestamp | Records when the row was inserted. |
| `updated_at` | ISO timestamp | Records when the row was last changed. |

## `migration_issues`

This table prevents silent data loss. If an original weight or date cannot be converted, the raw
values and a precise reason are retained for review. The application does not treat an invalid
legacy value as a valid weight entry.

## Index

`idx_weight_entries_user_date(user_id, entry_date DESC, entry_id DESC)` matches the main
history query. The leading `user_id` supports account filtering and the child side of the foreign
key. The remaining columns match the requested order, including a stable tie-breaker when two
records use the same date.
