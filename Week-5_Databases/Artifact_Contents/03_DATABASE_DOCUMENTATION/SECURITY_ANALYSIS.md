# Database Security Analysis

## Password storage

The original `password TEXT` value was directly readable and directly compared in SQL. The
enhanced design uses PBKDF2-HMAC-SHA-256 with a unique 16-byte salt and a work factor of
600,000. The hash, salt, algorithm name, and work factor are stored for each account. A candidate
password is derived again and compared with `MessageDigest.isEqual`; the plaintext value is
never inserted into SQLite. Verification rejects unsupported algorithms, unreasonable iteration
counts, and decoded salts or hashes with unexpected lengths before performing key derivation.
Blank legacy credentials are replaced with independently randomized 256-bit inaccessible values,
not a username-derived default.

This is hashing, not encryption. The narrative uses those terms deliberately. Hashing supports
verification without keeping a reversible password value. The design follows the password
storage principles described by OWASP and NIST, but the classroom application is not presented
as a fully deployed identity system. A production system would also require breach-password
screening, rate limiting, secure recovery, and broader device-threat analysis.

## Authentication and authorization

Authentication answers: “Did this user supply the correct password?” Authorization answers:
“May this authenticated user read or modify this record?” The original application addressed the
first question weakly and did not address the second. The enhancement passes the numeric
`user_id` to the dashboard after authentication and includes both `entry_id` and `user_id` in
update and delete conditions.

```sql
UPDATE weight_entries
SET weight = ?, entry_date = ?, note = ?, updated_at = ?
WHERE entry_id = ? AND user_id = ?;

DELETE FROM weight_entries
WHERE entry_id = ? AND user_id = ?;
```

A different user therefore receives a zero-row result even if that user knows another entry ID.

## Parameter binding

User values are supplied through `ContentValues`, selection arguments, or `rawQuery` argument
arrays. They are not concatenated into SQL statements. Table and column names come only from
`DatabaseContract` constants.

## Structural controls

- `NOT NULL`, `UNIQUE`, `CHECK`, and `FOREIGN KEY` constraints reject invalid states at the
  database boundary.
- Foreign-key enforcement is enabled in `onConfigure()` for each connection.
- `ON DELETE CASCADE` prevents orphaned weight rows.
- The manifest disables application backup for this local database artifact.
- Cursors use try-with-resources.
- Registration no longer contains a direct route to the dashboard.

## Scope limitations

SQLite protects local structure and relationships but does not by itself provide full database-file
encryption. This enhancement does not claim that the weight data is encrypted at rest. The
project also retains synchronous database calls because changing the entire threading model was
outside the planned database milestone. Moving I/O off the main thread would be a logical next
step for a production application.
