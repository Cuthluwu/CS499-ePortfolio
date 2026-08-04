# Exact Enhancement Map

This map identifies the submitted path, method, and line range for each major improvement.

| Enhancement | Exact path | Lines | What can be checked |
|---|---|---:|---|
| Version-two schema and history index | enhanced/Project2/app/src/main/java/com/example/project/data/DatabaseContract.java | 53-96 | Users, weight entries, migration audit table, constraints, foreign key, and compound history index. |
| Foreign-key enforcement | enhanced/Project2/app/src/main/java/com/example/project/data/DBHelper.java | 31-34 | Enables foreign-key checks whenever the application opens the database. |
| Version-one to version-two migration | enhanced/Project2/app/src/main/java/com/example/project/data/DBHelper.java | 55-80 | Renames the old tables, creates version two, migrates records, and removes old tables only after conversion. |
| Legacy user conversion | enhanced/Project2/app/src/main/java/com/example/project/data/DBHelper.java | 82-118 | Converts original plaintext passwords to salted PBKDF2 verifier records. |
| Legacy entry conversion and ownership handling | enhanced/Project2/app/src/main/java/com/example/project/data/DBHelper.java | 120-170 | Normalizes readable rows, links exact matches, isolates unmatched rows, and logs unreadable values. |
| Strict legacy date and weight parsing | enhanced/Project2/app/src/main/java/com/example/project/data/LegacyDataNormalizer.java | 12-36 | Uses strict date resolution and rejects impossible dates or invalid weights. |
| Password hashing | enhanced/Project2/app/src/main/java/com/example/project/security/PasswordHasher.java | 22-34 | Uses a new random salt and stores PBKDF2-HMAC-SHA-256 verifier metadata. |
| Password verification | enhanced/Project2/app/src/main/java/com/example/project/security/PasswordHasher.java | 36-56 | Rejects malformed metadata and uses a constant-time comparison helper. |
| Shared validation | enhanced/Project2/app/src/main/java/com/example/project/validation/InputValidator.java | 17-65 | Validates usernames, password length, identifiers, finite weight, ISO date, and note length. |
| Account registration | enhanced/Project2/app/src/main/java/com/example/project/data/UserRepository.java | 20-40 | Stores the verifier and returns a stable numeric user ID. |
| Authentication | enhanced/Project2/app/src/main/java/com/example/project/data/UserRepository.java | 42-67 | Uses a parameterized username lookup and returns a session only after password verification. |
| User-owned insert | enhanced/Project2/app/src/main/java/com/example/project/data/WeightEntryRepository.java | 30-48 | Writes every measurement with the authenticated user ID. |
| Ownership-scoped update | enhanced/Project2/app/src/main/java/com/example/project/data/WeightEntryRepository.java | 50-69 | Requires both entry ID and user ID in the SQL condition. |
| Ownership-scoped delete | enhanced/Project2/app/src/main/java/com/example/project/data/WeightEntryRepository.java | 71-79 | Requires both entry ID and user ID in the SQL condition. |
| Owned single-record read | enhanced/Project2/app/src/main/java/com/example/project/data/WeightEntryRepository.java | 81-96 | Prevents record access by entry ID alone. |
| Ordered account history | enhanced/Project2/app/src/main/java/com/example/project/data/WeightEntryRepository.java | 98-115 | Filters by user and orders by date and entry ID. |
| Latest measurement | enhanced/Project2/app/src/main/java/com/example/project/data/WeightEntryRepository.java | 117-131 | Returns only the current user’s newest entry. |
| Progress summary | enhanced/Project2/app/src/main/java/com/example/project/data/WeightEntryRepository.java | 133-177 | Returns count, first measurement, latest measurement, and change for one account. |
| Authenticated session handoff | enhanced/Project2/app/src/main/java/com/example/project/MainActivity.java | 38-55 | Passes the numeric user ID and username only after authentication succeeds. |
| Dashboard session boundary | enhanced/Project2/app/src/main/java/com/example/project/MainActivity2.java | 32-38 | Returns to login when the dashboard does not receive a valid session. |
| Dashboard CRUD uses the authenticated user | enhanced/Project2/app/src/main/java/com/example/project/MainActivity2.java | 62-87 | Create, update, and delete all pass the current user ID. |
| User-only history and progress display | enhanced/Project2/app/src/main/java/com/example/project/MainActivity2.java | 105-130 | Displays only the current account’s records and summary. |
| Registration bypass removed | enhanced/Project2/app/src/main/java/com/example/project/MainActivity4.java | 52-55 | Returns to the login screen instead of opening the dashboard directly. |
| Backup protection and least privilege | enhanced/Project2/app/src/main/AndroidManifest.xml | 1-33 | Disables application backup and does not request the unused SEND_SMS permission. |
| Legacy backup rules | enhanced/Project2/app/src/main/res/xml/backup_rules.xml | 1-6 | Excludes the local database and preferences from full backup. |
| Android 12+ extraction rules | enhanced/Project2/app/src/main/res/xml/data_extraction_rules.xml | 1-12 | Excludes database and preferences from cloud backup and device transfer. |
| Android database integration tests | enhanced/Project2/app/src/androidTest/java/com/example/project/data/DatabaseIntegrationTest.java | 1-118 | Covers password storage, authentication, user isolation, CRUD, progress, cascade deletion, and migration. |

The untouched starting point is under original/Project2.
The complete source and resource comparison is ORIGINAL_TO_ENHANCED.diff.
