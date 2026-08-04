# Migration and Data Preservation

## Why the original upgrade was replaced

The original `onUpgrade()` dropped `users` and `Userdetails`. A schema change would therefore
erase accounts and weight history. The enhanced helper raises the database version from 1 to 2
and performs a controlled migration inside the transaction provided by `SQLiteOpenHelper`.

## Version-one to version-two sequence

1. Confirm which version-one tables exist.
2. Rename `users` to `users_legacy` and `Userdetails` to `userdetails_legacy`.
3. Create the complete version-two schema and index.
4. Read every legacy account. Hash the stored legacy password with a unique salt and record the
   algorithm and work factor. New registrations follow current validation rules; migrated
   accounts are preserved even if an old password would not satisfy the new minimum length.
5. Build a case-insensitive mapping between original usernames and new numeric `user_id` values.
6. Parse legacy weights as numeric values and normalize supported date formats to `YYYY-MM-DD`.
7. Link a record to a real user only when its original `name` matches an original username.
8. Place an unmatched but otherwise valid record under a separate `legacy_import` account. This
   avoids inventing ownership and accidentally exposing a record to the wrong real user.
9. Write unconvertible rows to `migration_issues` with their original values and a reason.
10. Drop the renamed legacy tables only after the migration finishes successfully.

## Important limitation handled honestly

The original `Userdetails` table did not contain a user relationship. No migration can recover
information that was never stored. The enhanced code therefore uses a conservative rule:
matching names can be linked; unmatched records are preserved separately. It does not guess
which real user owns an ambiguous record.

## Rollback behavior

Android's `SQLiteOpenHelper.onUpgrade()` executes within a transaction. If the migration throws
an exception before completion, the changes are rolled back rather than leaving a half-created
schema. The migration does not catch and ignore structural database exceptions.
