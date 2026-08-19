CS 499 MILESTONE FOUR - DATABASES
Madison Parker

This folder contains my original CS 360 Weight Tracker and a separate copy with the database changes I made for the capstone.

01_ORIGINAL_ARTIFACT_UNMODIFIED
    My original Android Studio project, screenshots, and CS 360 reflection.

02_ENHANCED_DATABASE_ARTIFACT/Project2
    The enhanced Android Studio project. Open the Project2 folder in Android Studio.

03_DATABASE_DOCUMENTATION
    Notes about the original database, new schema, migration, password handling, queries, testing, references, and the Week 6 future-feature idea.

04_INSTRUCTOR_EVIDENCE
    Saved test output, comparison files, review notes, and scripts used while checking the database changes.

The main database changes are:
- separate numeric IDs for users and weight entries
- a user ID on each weight entry
- numeric weight values and consistent dates
- password hashing with PBKDF2 and a random salt
- user-specific history, updates, and deletes
- migration of usable old data instead of dropping the tables

The Bluetooth scale and progress-feedback material is future planning only. It is not implemented in the current Android project.

The Android project and test source are included. I did not complete a full APK build in the final preparation environment.
