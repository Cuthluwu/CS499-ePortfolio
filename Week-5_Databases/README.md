# Week 5 — Databases

This milestone uses my CS 360 Android Weight Tracker. The original app could register users, check a login, and save weight records in SQLite, but the database design had several problems that became more obvious when I reviewed the project for the capstone.

## Files

- [Database narrative](Madison_Parker_CS499_Milestone4_Database_Narrative.docx)
- [`Artifact_Contents/01_ORIGINAL_ARTIFACT_UNMODIFIED/`](Artifact_Contents/01_ORIGINAL_ARTIFACT_UNMODIFIED/) — original Android project
- [`Artifact_Contents/02_ENHANCED_DATABASE_ARTIFACT/`](Artifact_Contents/02_ENHANCED_DATABASE_ARTIFACT/) — enhanced Android project
- [`Artifact_Contents/03_DATABASE_DOCUMENTATION/`](Artifact_Contents/03_DATABASE_DOCUMENTATION/) — schema, migration, security, and query notes
- [Downloadable artifact ZIP](Madison_Parker_CS499_Milestone4_Database_Artifact.zip)

## What was wrong in the original version

The original `DBHelper` stored usernames and passwords directly in the `users` table. Weight records were kept in a separate `Userdetails` table where the person's name was the primary key, weight and date were text fields, and there was no user ID connecting a weight record to an account. The history method selected every row, and `onUpgrade()` dropped both tables.

## What I changed

- Added separate numeric `user_id` and `entry_id` values.
- Connected each weight entry to its user with a foreign key.
- Stored weight as a numeric value and used a consistent date format.
- Added database constraints for invalid values.
- Replaced plain-text password storage with PBKDF2 password hashing and a random salt.
- Limited weight history, updates, and deletes to the signed-in user's ID.
- Replaced the destructive upgrade with a migration that attempts to preserve usable older data.
- Added ordered weight history and a first-to-latest progress summary.

The migration also has to deal with a limitation in my original design: old weight rows never stored a user ID. When an old row cannot be matched safely to an account, the enhanced code keeps it under a separate import account rather than guessing who owns it.

## Testing

The artifact includes database checks and Java checks for the schema, validation, password handling, ownership, queries, and migration. The Android project and test source are included, but I did not complete a full APK build in the final preparation environment.

## Week 6 idea

I later explored Bluetooth scale syncing and optional progress feedback as possible future improvements. Those are planning ideas, not completed features in this version of the Weight Tracker.