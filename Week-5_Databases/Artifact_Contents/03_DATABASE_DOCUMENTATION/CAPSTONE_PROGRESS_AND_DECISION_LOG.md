# Capstone Progress and Decision Log

This file shows how the database artifact developed from the Week One plan into the Milestone Four
submission. It is not a claim that every decision was correct on the first attempt. It records what I
planned, what the original code showed me, and what I changed after testing.

## Module One: Artifact selection and plan

I selected the CS 360 Weight Tracker for the database category. My initial plan was to separate users
from weight records, add numeric identifiers, connect records to users, use numeric weight values and
consistent dates, improve password storage, and add progress queries.

## Original-code review

When I opened the exact project again, I found additional problems that made the plan more specific:

- the history query returned every record;
- update and delete used only the name field;
- registration could open the dashboard without authentication;
- onUpgrade() dropped both tables;
- the old weight table did not contain enough information to prove record ownership;
- the original tests were templates rather than database tests.

These findings changed the enhancement from a simple table rewrite into a user-owned data model with
a documented migration rule and verification for cross-user access.

## Schema and repository decisions

I kept SQLiteOpenHelper because it was part of the original project and allowed a direct before-and-after
comparison. I added separate users and weight_entries tables, numeric primary keys, a foreign key, a
compound history index, database constraints, and repository classes. I did not add indexes to every
column because the repeated query is user history ordered by date.

## Security decisions

I replaced plaintext password storage with salted PBKDF2 verifiers. I separated authentication from
record authorization, required user_id in every weight query, disabled app backup for the local database,
and removed the unused SEND_SMS permission from the enhanced manifest. I did not describe the app as
fully secure because it still lacks account recovery, login throttling, and a production identity service.

## Migration decisions

The original weight table did not store a user ID. I chose exact name-to-username matching only when the
values were available. Unmatched readable records are placed under a separate legacy import account, and
unreadable rows are recorded in migration_issues. This avoids inventing ownership or silently deleting data.

## Testing decisions

Testing changed several details of the implementation. User-isolation tests required both entry_id and
user_id in update and delete conditions. Same-date records required entry_id as a final ordering key. An
impossible legacy date showed that date parsing needed ResolverStyle.STRICT. The final package includes
Android tests as well as standalone database and Java checks.

## Final packaging

Because an earlier ZIP did not open correctly, I treated packaging as part of the submission. The final ZIP
contains the untouched original and complete enhanced project as separate folders, a root read-first file,
exact enhancement locations, verification results, and no nested ZIP.
