package com.example.project.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.project.security.PasswordHasher;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Creates the normalized schema and migrates the original version-one database. */
public final class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "WeightTrackerDatabase";
    private static final String LEGACY_USERS = "users_legacy";
    private static final String LEGACY_ENTRIES = "userdetails_legacy";
    private static final String LEGACY_IMPORT_USERNAME = "legacy_import";

    public DBHelper(Context context) {
        super(
                context,
                DatabaseContract.DATABASE_NAME,
                null,
                DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase database) {
        super.onConfigure(database);
        database.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createVersionTwoSchema(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            migrateVersionOneToTwo(database);
        }
    }

    private void createVersionTwoSchema(SQLiteDatabase database) {
        database.execSQL(DatabaseContract.CREATE_USERS);
        database.execSQL(DatabaseContract.CREATE_WEIGHT_ENTRIES);
        database.execSQL(DatabaseContract.CREATE_MIGRATION_ISSUES);
        database.execSQL(DatabaseContract.CREATE_HISTORY_INDEX);
    }

    private void migrateVersionOneToTwo(SQLiteDatabase database) {
        boolean hasUsers = tableExists(database, "users");
        boolean hasEntries = tableExists(database, "Userdetails");

        if (hasUsers) {
            database.execSQL("ALTER TABLE users RENAME TO " + LEGACY_USERS);
        }
        if (hasEntries) {
            database.execSQL("ALTER TABLE Userdetails RENAME TO " + LEGACY_ENTRIES);
        }

        createVersionTwoSchema(database);
        Map<String, Long> userIds = hasUsers
                ? migrateLegacyUsers(database)
                : new HashMap<>();
        if (hasEntries) {
            migrateLegacyEntries(database, userIds);
        }

        if (hasEntries) {
            database.execSQL("DROP TABLE " + LEGACY_ENTRIES);
        }
        if (hasUsers) {
            database.execSQL("DROP TABLE " + LEGACY_USERS);
        }
    }

    private Map<String, Long> migrateLegacyUsers(SQLiteDatabase database) {
        Map<String, Long> migrated = new HashMap<>();
        try (Cursor cursor = database.query(
                LEGACY_USERS,
                new String[]{"username", "password"},
                null,
                null,
                null,
                null,
                null)) {
            int sequence = 1;
            while (cursor.moveToNext()) {
                String rawUsername = cursor.getString(0);
                String rawPassword = cursor.getString(1);
                String username = uniqueLegacyUsername(database, rawUsername, sequence++);
                char[] password = rawPassword == null ? new char[0] : rawPassword.toCharArray();
                if (password.length == 0) {
                    password = ("legacy-unavailable-" + username).toCharArray();
                }
                PasswordHasher.HashResult hash = PasswordHasher.hash(password);
                java.util.Arrays.fill(password, '\0');

                ContentValues values = new ContentValues();
                values.put(DatabaseContract.Users.USERNAME, username);
                values.put(DatabaseContract.Users.PASSWORD_HASH, hash.getHash());
                values.put(DatabaseContract.Users.PASSWORD_SALT, hash.getSalt());
                values.put(DatabaseContract.Users.PASSWORD_ALGORITHM, hash.getAlgorithm());
                values.put(DatabaseContract.Users.PASSWORD_ITERATIONS, hash.getIterations());
                values.put(DatabaseContract.Users.CREATED_AT, Instant.now().toString());
                long userId = database.insertOrThrow(DatabaseContract.Users.TABLE, null, values);
                if (rawUsername != null && !rawUsername.trim().isEmpty()) {
                    migrated.putIfAbsent(rawUsername.trim().toLowerCase(Locale.US), userId);
                }
            }
        }
        return migrated;
    }

    private void migrateLegacyEntries(SQLiteDatabase database, Map<String, Long> userIds) {
        Long fallbackUserId = null;
        try (Cursor cursor = database.query(
                LEGACY_ENTRIES,
                new String[]{"name", "weight", "date"},
                null,
                null,
                null,
                null,
                null)) {
            while (cursor.moveToNext()) {
                String legacyName = cursor.getString(0);
                String rawWeight = cursor.getString(1);
                String rawDate = cursor.getString(2);
                Double weight = LegacyDataNormalizer.parseWeight(rawWeight);
                String entryDate = LegacyDataNormalizer.normalizeDate(rawDate);

                if (weight == null || entryDate == null) {
                    String reason = weight == null && entryDate == null
                            ? "Weight and date could not be normalized."
                            : weight == null
                            ? "Weight could not be normalized."
                            : "Date could not be normalized.";
                    logMigrationIssue(database, legacyName, reason, rawWeight, rawDate);
                    continue;
                }

                Long ownerId = legacyName == null
                        ? null
                        : userIds.get(legacyName.trim().toLowerCase(Locale.US));
                if (ownerId == null) {
                    if (fallbackUserId == null) {
                        fallbackUserId = createLegacyImportAccount(database);
                    }
                    ownerId = fallbackUserId;
                }

                String timestamp = Instant.now().toString();
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.WeightEntries.USER_ID, ownerId);
                values.put(DatabaseContract.WeightEntries.WEIGHT, weight);
                values.put(DatabaseContract.WeightEntries.ENTRY_DATE, entryDate);
                values.put(
                        DatabaseContract.WeightEntries.NOTE,
                        legacyImportNote(legacyName));
                values.put(DatabaseContract.WeightEntries.CREATED_AT, timestamp);
                values.put(DatabaseContract.WeightEntries.UPDATED_AT, timestamp);
                database.insertOrThrow(DatabaseContract.WeightEntries.TABLE, null, values);
            }
        }
    }

    private long createLegacyImportAccount(SQLiteDatabase database) {
        String username = uniqueLegacyUsername(database, LEGACY_IMPORT_USERNAME, 1);
        char[] unavailablePassword = ("unavailable-" + System.nanoTime()).toCharArray();
        try {
            PasswordHasher.HashResult hash = PasswordHasher.hash(unavailablePassword);
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Users.USERNAME, username);
            values.put(DatabaseContract.Users.PASSWORD_HASH, hash.getHash());
            values.put(DatabaseContract.Users.PASSWORD_SALT, hash.getSalt());
            values.put(DatabaseContract.Users.PASSWORD_ALGORITHM, hash.getAlgorithm());
            values.put(DatabaseContract.Users.PASSWORD_ITERATIONS, hash.getIterations());
            values.put(DatabaseContract.Users.CREATED_AT, Instant.now().toString());
            return database.insertOrThrow(DatabaseContract.Users.TABLE, null, values);
        } finally {
            java.util.Arrays.fill(unavailablePassword, '\0');
        }
    }

    private String legacyImportNote(String legacyName) {
        String label = legacyName == null || legacyName.trim().isEmpty()
                ? "unnamed"
                : legacyName.trim();
        String prefix = "Imported from original record labeled: ";
        int available = 250 - prefix.length();
        return prefix + (label.length() <= available ? label : label.substring(0, available));
    }

    private void logMigrationIssue(
            SQLiteDatabase database,
            String identifier,
            String reason,
            String rawWeight,
            String rawDate) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MigrationIssues.SOURCE_TABLE, "Userdetails");
        values.put(DatabaseContract.MigrationIssues.SOURCE_IDENTIFIER, identifier);
        values.put(DatabaseContract.MigrationIssues.REASON, reason);
        values.put(DatabaseContract.MigrationIssues.RAW_WEIGHT, rawWeight);
        values.put(DatabaseContract.MigrationIssues.RAW_DATE, rawDate);
        values.put(DatabaseContract.MigrationIssues.CREATED_AT, Instant.now().toString());
        database.insertOrThrow(DatabaseContract.MigrationIssues.TABLE, null, values);
        Log.w(TAG, "Legacy record was preserved in migration_issues: " + reason);
    }

    private String uniqueLegacyUsername(
            SQLiteDatabase database,
            String rawUsername,
            int sequence) {
        String base = rawUsername == null ? "" : rawUsername.trim();
        if (base.isEmpty()) {
            base = "legacy_user_" + sequence;
        }
        String candidate = base;
        int suffix = 1;
        while (usernameExists(database, candidate)) {
            candidate = base + "_legacy_" + suffix++;
        }
        return candidate;
    }

    private boolean usernameExists(SQLiteDatabase database, String username) {
        try (Cursor cursor = database.rawQuery(
                "SELECT 1 FROM " + DatabaseContract.Users.TABLE +
                        " WHERE " + DatabaseContract.Users.USERNAME + " = ? COLLATE NOCASE LIMIT 1",
                new String[]{username})) {
            return cursor.moveToFirst();
        }
    }

    private boolean tableExists(SQLiteDatabase database, String tableName) {
        try (Cursor cursor = database.rawQuery(
                "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?",
                new String[]{tableName})) {
            return cursor.moveToFirst();
        }
    }
}
