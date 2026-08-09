# Week 5 — Databases

Milestone Four enhances the CS 360 Android Weight Tracker as an end-to-end database artifact. The original application demonstrated SQLite persistence and basic CRUD. The completed version adds a versioned relational model, stable identifiers, account ownership, database constraints, repository boundaries, credential verification, migration, deterministic reporting, and repeatable evidence.

## Start Here

1. [Enhancement narrative](Madison_Parker_CS499_Milestone4_Database_Narrative.docx)
2. [`Artifact_Contents/00_READ_ME_FIRST.txt`](Artifact_Contents/00_READ_ME_FIRST.txt)
3. [`Artifact_Contents/04_INSTRUCTOR_EVIDENCE/WHAT_CHANGED_AT_A_GLANCE.md`](Artifact_Contents/04_INSTRUCTOR_EVIDENCE/WHAT_CHANGED_AT_A_GLANCE.md)
4. [`Artifact_Contents/04_INSTRUCTOR_EVIDENCE/ENHANCEMENT_MAP.md`](Artifact_Contents/04_INSTRUCTOR_EVIDENCE/ENHANCEMENT_MAP.md)
5. [`Artifact_Contents/03_DATABASE_DOCUMENTATION/SECURITY_ANALYSIS.md`](Artifact_Contents/03_DATABASE_DOCUMENTATION/SECURITY_ANALYSIS.md)
6. [Downloadable technical artifact](Madison_Parker_CS499_Milestone4_Database_Artifact.zip)
7. [Behavior-aware feedback and Bluetooth scale roadmap](Artifact_Contents/03_DATABASE_DOCUMENTATION/BEHAVIOR_DESIGN_AND_BLUETOOTH_ROADMAP.md)

## Before and After

| Database concern | Original condition | Completed enhancement |
| --- | --- | --- |
| Identity and relationships | Username/name text used as keys; no account-to-entry foreign key | Integer `user_id` and `entry_id` keys with a one-to-many relationship |
| Credential storage | Plaintext password column and direct comparison | Per-account random salt, PBKDF2-HMAC-SHA-256 verifier, algorithm and work-factor metadata |
| Ownership | History and mutations were not scoped to the authenticated account | Read, update, delete, and progress operations require authenticated `user_id` |
| Data validity | Weight and dates stored as loosely formatted text | REAL weight, ISO date, `NOT NULL`, `UNIQUE`, `CHECK`, and foreign-key constraints |
| Upgrades | `onUpgrade` dropped existing tables | Version-one to version-two transactional migration with ambiguity auditing |
| User value | Raw history rows | Ordered history, latest entry, and first-to-latest progress summary |

## Security and Data-Integrity Decisions

- `PasswordHasher` stores salted verifiers instead of reversible or plaintext credentials and records parameters needed for future work-factor changes.
- Final verification bounds accepted work factors, rejects malformed decoded verifier dimensions, and assigns randomized 256-bit inaccessible credentials to blank legacy accounts instead of deriving a predictable value.
- `UserRepository` separates registration and authentication from weight-entry behavior.
- `WeightEntryRepository` uses bound values and scopes update/delete statements by both `entry_id` and authenticated `user_id`.
- `DBHelper.onConfigure` enables foreign-key enforcement for every connection.
- The version-two schema rejects impossible or non-ISO dates, nonpositive/out-of-range weights, overlong notes, missing owners, and invalid password work factors.
- Migration links a legacy row only when the available name matches a username case-insensitively; unmatched valid rows are preserved under a separate import account, while unreadable rows are recorded in `migration_issues` rather than discarded or assigned by guesswork.
- The enhanced manifest disables application backup and removes an unused sensitive SMS permission.

The artifact does not claim to be a complete production identity platform. Account recovery, breached-password screening, server-side rate limiting, hardware-backed secrets, encrypted sensitive-data storage, and background-thread database execution remain documented future work.

## Personal Product Extension

The Week 6 refinement adds a researched roadmap for optional behavior-aware feedback and Bluetooth scale synchronization. The goal is to make the Weight Tracker more personal without turning a measurement into a judgment.

For a user who selects weight loss, a sustained downward trend can activate a pale green overview state with a check icon, signed change, and specific progress text. A sustained upward trend can activate a restrained pale red attention state with neutral language and a next action. Classification is based on a documented rolling comparison and the user's selected loss, maintenance, or gain goal. A single higher reading is not automatically labeled failure. Users can disable adaptive colors or choose a neutral interface.

The psychology section connects this decision to consistent self-monitoring and positive reinforcement research while also documenting the limits of the evidence. The accessibility specification uses text, icons, numeric differences, content descriptions, and WCAG-compliant contrast so color is never the only carrier of meaning.

The technical roadmap now distinguishes three different facts:

- Withings demonstrates that automatic Bluetooth/Wi-Fi scale-to-app synchronization is a real commercial experience.
- The Bluetooth SIG Weight Scale Service defines a standards-based data path for weight and optional timestamp, user, BMI, and height fields.
- Madison's application has not yet proved direct compatibility with a physical scale or a vendor-controlled protocol.

The proposed architecture covers pairing, Android runtime permissions, device adapters, unit normalization, source metadata, account ownership, idempotent imports, transaction boundaries, replay protection, privacy controls, accessible feedback, and a physical-device acceptance record. It is clearly labeled future work and is not presented as completed Bluetooth interoperability.

## Verification Boundary

- **46 of 46 SQLite checks passed** for schema creation, keys, constraints, credential storage, authentication, ownership-scoped CRUD, ordering, reporting, index selection, cascade behavior, and migration.
- **36 of 36 Java checks passed** after Java 17 `-Xlint:all` compilation of password, validation, and legacy-normalization logic.
- Original-artifact integrity records cover all 78 baseline files.
- The complete Gradle project and Android instrumentation tests are included for Android Studio execution; the publication environment did not assemble an APK, and the portfolio does not claim otherwise.

Run the recorded verifiers with:

```sh
sh Artifact_Contents/04_INSTRUCTOR_EVIDENCE/run_database_verification.sh
sh Artifact_Contents/04_INSTRUCTOR_EVIDENCE/run_java_verification.sh
```
