# Milestone Four Rubric Crosswalk

## Performs the planned enhancement

The Module One plan called for separate user and weight tables, numeric identifiers, numeric weight
values, consistent dates, parameterized operations, stronger password storage, user-linked history, and
progress reporting. Those items are implemented in the enhanced Project2 folder. The exact file and line
locations are listed in ENHANCEMENT_MAP.md.

## Uses database skills and techniques to accomplish the project goal

The enhanced project uses a normalized one-to-many schema, primary and foreign keys, constraints,
foreign-key enforcement, a compound index, parameterized queries, repository classes, progress queries,
and a versioned migration with an audit table for records that cannot be converted safely.

## Stores, manipulates, and accesses data

WeightEntryRepository implements create, owned single-record read, ordered history, latest-entry read,
owned update, owned delete, and progress-summary behavior. UserRepository implements registration,
authentication, and account deletion. Every weight operation uses the authenticated numeric user ID.

## Addresses design, logical, structural, and security flaws

The enhancement removes plaintext password storage, adds record ownership, closes the registration
bypass, enables foreign-key enforcement, rejects invalid states, protects local database backup, removes an
unused sensitive permission, and migrates or audits legacy data instead of dropping the tables.

## Clearly explains the work through a reviewable product

The submission contains the unchanged original project, the complete enhanced project, Android and
standalone tests, 46 passing SQLite checks, 30 passing Java checks, exact file and line locations, a full
before-and-after diff, technical documentation, five references, and a detailed Word narrative.
