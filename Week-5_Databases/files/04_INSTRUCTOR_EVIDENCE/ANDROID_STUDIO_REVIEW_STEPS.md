# Android Studio Review Steps

1. Extract the technical ZIP before opening the project.
2. In Android Studio, select Open and choose enhanced/Project2.
3. Allow the included Gradle wrapper to synchronize. The project keeps Android SDK 35, minimum SDK 28,
   and Java 21 because those settings were already present in the original artifact.
4. Run the local tests under app/src/test.
5. Use an API 28 or newer emulator/device to run DatabaseIntegrationTest under app/src/androidTest.
6. Run the app and follow the manual flow in README_MILESTONE4.txt.

## Fast source review

- Schema, constraints, and index: DatabaseContract.java
- Foreign-key setup and migration: DBHelper.java
- Registration and authentication: UserRepository.java
- User-owned CRUD and progress reporting: WeightEntryRepository.java
- Password verifier: PasswordHasher.java
- Validation and legacy conversion: InputValidator.java and LegacyDataNormalizer.java
- Exact locations for every claim: 04_INSTRUCTOR_EVIDENCE/ENHANCEMENT_MAP.md

## Verification scope

I performed the final file review and standalone checks from a Chromebook without the Android SDK. The
artifact therefore includes the full Android Studio project and Android tests but does not claim an APK
build that I could not execute. DATABASE_VERIFICATION_RESULTS.txt and
JAVA_SECURITY_VALIDATION_RESULTS.txt record the checks that were run successfully.
