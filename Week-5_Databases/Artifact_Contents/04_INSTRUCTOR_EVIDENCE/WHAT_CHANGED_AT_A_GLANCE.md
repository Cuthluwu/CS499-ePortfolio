# What Changed at a Glance

| Area | Original CS 360 artifact | Milestone Four enhanced artifact | Main evidence |
|---|---|---|---|
| User identity | Username text was the primary key. | Numeric user_id is the primary key; username remains unique and case-insensitive. | DatabaseContract.java; UserRepository.java |
| Password storage | Password was stored and compared as plaintext. | A unique salt and PBKDF2-HMAC-SHA-256 verifier are stored with algorithm and work-factor metadata. | PasswordHasher.java; UserRepository.java |
| Weight record identity | name was the primary key, so one name could not have a realistic history. | Each measurement has a numeric entry_id. | DatabaseContract.java |
| Record ownership | Weight rows had no relationship to an account. | Each weight entry requires a user_id foreign key. | DatabaseContract.java; WeightEntryRepository.java |
| History access | SELECT * returned every record. | History is filtered by authenticated user_id and ordered by date and entry ID. | WeightEntryRepository.getHistory() |
| Update/delete security | Operations used only name. | SQL requires both entry_id and user_id. | WeightEntryRepository.updateEntry(); deleteEntry() |
| Data types | Weight and date were unrestricted text. | Weight uses REAL with a range check; dates use validated ISO text; notes have a length limit. | DatabaseContract.java; InputValidator.java |
| Upgrade behavior | onUpgrade() dropped both tables. | Version-one data is migrated, ambiguous ownership is isolated, and unreadable values are audited. | DBHelper.java; LegacyDataNormalizer.java |
| Referential integrity | No foreign key existed. | Foreign-key enforcement is enabled and dependent entries use ON DELETE CASCADE. | DBHelper.onConfigure(); DatabaseContract.java |
| Query support | No index matched account history. | A compound index supports user filtering and newest-first ordering. | idx_weight_entries_user_date |
| Application boundary | Registration could open the dashboard directly. | Successful authentication supplies the stable user ID; missing session data returns to login. | MainActivity.java; MainActivity2.java; MainActivity4.java |
| Permissions and backup | The original manifest requested SEND_SMS and allowed the original backup configuration. | Unused SMS permission is removed; application backup is disabled and rules exclude database data. | AndroidManifest.xml; backup_rules.xml; data_extraction_rules.xml |
| Testing | Template tests did not check database behavior. | Unit tests, Android integration tests, 46 SQLite checks, and 36 Java checks cover the enhancement. | app/src/test; app/src/androidTest; verification results |
