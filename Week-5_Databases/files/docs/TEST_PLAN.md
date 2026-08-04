# Test and Verification Plan

## Android tests included in the enhanced project

`DatabaseIntegrationTest.java` contains tests for:

- hashed password storage and successful/failed authentication;
- user-scoped retrieval, update, and deletion;
- chronological history and progress summary;
- foreign-key cascade deletion; and
- migration from the exact version-one table definitions.

Local JUnit tests also cover unique salts, malformed verifier metadata, password length,
username normalization, strict ISO dates, finite weight ranges, note limits, supported legacy date
formats, and rejection of legacy values that cannot be normalized safely.

## Standalone verification performed in this submission environment

The container used to prepare this submission does not include an Android SDK, so I do not claim
that an APK was assembled here. Instead, I performed three separate checks:

1. `PasswordHasher`, `InputValidator`, and `LegacyDataNormalizer` compiled with Java 21 and
   `javac -Xlint:all`. A standalone test passed 30 checks.
2. `verify_database.py` created and exercised the submitted schema with Python's standard
   SQLite implementation. It passed 46 checks covering schema, constraints, authentication,
   account isolation, CRUD, ordering, progress, indexing, cascade deletion, and migration.
3. Every Android XML resource was parsed, every `R.id` reference in the Java source was matched
   to a declared resource ID, and the untouched original copy was compared to its source using
   SHA-256. As an additional syntax check, all submitted Java source and test files compiled
   against local Android/JUnit API stubs with `javac -Xlint:all` and no warnings. This does not
   replace a real Android SDK build, and it is labeled accordingly.

The full Android project, Gradle wrapper, unit tests, and instrumentation tests are included so the
instructor can open the enhanced `Project2` folder in Android Studio and run the Android tests.

## Evidence files

- `verify_database.py` and `run_database_verification.sh`
- `DATABASE_VERIFICATION_RESULTS.txt`
- `StandaloneSecurityValidationTest.java` and `run_java_verification.sh`
- `JAVA_SECURITY_VALIDATION_RESULTS.txt`
- `STATIC_PROJECT_VERIFICATION.txt`
- `ORIGINAL_INTEGRITY_SHA256.txt`
- `ORIGINAL_TO_ENHANCED.diff`
- `FILE_MANIFEST_SHA256.txt`
