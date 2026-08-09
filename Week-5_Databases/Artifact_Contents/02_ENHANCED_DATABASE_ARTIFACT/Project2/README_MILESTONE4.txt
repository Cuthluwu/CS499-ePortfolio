CS 499 MILESTONE FOUR - DATABASE ENHANCEMENT
Madison Parker

PROJECT PURPOSE
This folder is the complete enhanced copy of my original CS 360 Android Weight Tracker. It keeps
the same basic application and visual style while replacing the original unrelated text tables with
a normalized SQLite design that connects each weight entry to the authenticated user.

OPEN IN ANDROID STUDIO
1. Extract the technical ZIP first.
2. Open this Project2 folder.
3. Allow Gradle to synchronize with Android SDK 35 and the included Gradle wrapper.
4. Run the app on an API 28 or newer emulator or device.
5. Run the local tests under app/src/test.
6. Run DatabaseIntegrationTest under app/src/androidTest on an emulator or device.

MANUAL REVIEW
1. Register an account.
2. Sign in with that account.
3. Save at least two weight entries using YYYY-MM-DD dates.
4. View history and confirm the newest date appears first.
5. Update or delete an entry using its numeric entry ID.
6. Register a second account and confirm that it cannot see or change the first account's records.

PRIMARY DATABASE FILES
- app/src/main/java/com/example/project/data/DatabaseContract.java
- app/src/main/java/com/example/project/data/DBHelper.java
- app/src/main/java/com/example/project/data/UserRepository.java
- app/src/main/java/com/example/project/data/WeightEntryRepository.java
- app/src/main/java/com/example/project/data/LegacyDataNormalizer.java
- app/src/main/java/com/example/project/security/PasswordHasher.java
- app/src/main/java/com/example/project/validation/InputValidator.java

TEST FILES
- app/src/test/java/com/example/project/security/PasswordHasherTest.java
- app/src/test/java/com/example/project/validation/InputValidatorTest.java
- app/src/test/java/com/example/project/data/LegacyDataNormalizerTest.java
- app/src/androidTest/java/com/example/project/data/DatabaseIntegrationTest.java

SCOPE NOTE
The SMS settings screen came from the original project and remains a placeholder outside the
Milestone Four database scope. Because it does not send messages, the enhanced manifest no longer
requests the SEND_SMS permission. The main enhancement focuses on schema design, account
ownership, authentication, CRUD operations, migration, reporting, and testing.

VERIFICATION NOTE
I completed final packaging and standalone checks on a Chromebook without the Android SDK. The
full Android project and instrumentation tests are included for Android Studio. The evidence folder
contains the commands and results for the database and pure-Java checks that I was able to run.
