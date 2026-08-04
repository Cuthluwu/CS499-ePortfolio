package com.example.project.data;

/** Central names and SQL statements for the version-two database. */
public final class DatabaseContract {
    private DatabaseContract() {
    }

    public static final String DATABASE_NAME = "Login.db";
    public static final int DATABASE_VERSION = 2;

    public static final class Users {
        public static final String TABLE = "users";
        public static final String ID = "user_id";
        public static final String USERNAME = "username";
        public static final String PASSWORD_HASH = "password_hash";
        public static final String PASSWORD_SALT = "password_salt";
        public static final String PASSWORD_ALGORITHM = "password_algorithm";
        public static final String PASSWORD_ITERATIONS = "password_iterations";
        public static final String CREATED_AT = "created_at";

        private Users() {
        }
    }

    public static final class WeightEntries {
        public static final String TABLE = "weight_entries";
        public static final String ID = "entry_id";
        public static final String USER_ID = "user_id";
        public static final String WEIGHT = "weight";
        public static final String ENTRY_DATE = "entry_date";
        public static final String NOTE = "note";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";

        private WeightEntries() {
        }
    }

    public static final class MigrationIssues {
        public static final String TABLE = "migration_issues";
        public static final String ID = "issue_id";
        public static final String SOURCE_TABLE = "source_table";
        public static final String SOURCE_IDENTIFIER = "source_identifier";
        public static final String REASON = "reason";
        public static final String RAW_WEIGHT = "raw_weight";
        public static final String RAW_DATE = "raw_date";
        public static final String CREATED_AT = "created_at";

        private MigrationIssues() {
        }
    }

    public static final String CREATE_USERS =
            "CREATE TABLE " + Users.TABLE + " (" +
                    Users.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Users.USERNAME + " TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                    Users.PASSWORD_HASH + " TEXT NOT NULL, " +
                    Users.PASSWORD_SALT + " TEXT NOT NULL, " +
                    Users.PASSWORD_ALGORITHM + " TEXT NOT NULL, " +
                    Users.PASSWORD_ITERATIONS + " INTEGER NOT NULL CHECK(" +
                    Users.PASSWORD_ITERATIONS + " > 0), " +
                    Users.CREATED_AT + " TEXT NOT NULL" +
                    ")";

    public static final String CREATE_WEIGHT_ENTRIES =
            "CREATE TABLE " + WeightEntries.TABLE + " (" +
                    WeightEntries.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WeightEntries.USER_ID + " INTEGER NOT NULL, " +
                    WeightEntries.WEIGHT + " REAL NOT NULL CHECK(" +
                    WeightEntries.WEIGHT + " > 0 AND " + WeightEntries.WEIGHT + " <= 1500), " +
                    WeightEntries.ENTRY_DATE + " TEXT NOT NULL CHECK(length(" +
                    WeightEntries.ENTRY_DATE + ") = 10 AND date(" +
                    WeightEntries.ENTRY_DATE + ") = " + WeightEntries.ENTRY_DATE + "), " +
                    WeightEntries.NOTE + " TEXT NOT NULL DEFAULT '' CHECK(length(" +
                    WeightEntries.NOTE + ") <= 250), " +
                    WeightEntries.CREATED_AT + " TEXT NOT NULL, " +
                    WeightEntries.UPDATED_AT + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + WeightEntries.USER_ID + ") REFERENCES " +
                    Users.TABLE + "(" + Users.ID + ") ON DELETE CASCADE" +
                    ")";

    public static final String CREATE_MIGRATION_ISSUES =
            "CREATE TABLE " + MigrationIssues.TABLE + " (" +
                    MigrationIssues.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MigrationIssues.SOURCE_TABLE + " TEXT NOT NULL, " +
                    MigrationIssues.SOURCE_IDENTIFIER + " TEXT, " +
                    MigrationIssues.REASON + " TEXT NOT NULL, " +
                    MigrationIssues.RAW_WEIGHT + " TEXT, " +
                    MigrationIssues.RAW_DATE + " TEXT, " +
                    MigrationIssues.CREATED_AT + " TEXT NOT NULL" +
                    ")";

    public static final String CREATE_HISTORY_INDEX =
            "CREATE INDEX idx_weight_entries_user_date ON " +
                    WeightEntries.TABLE + "(" + WeightEntries.USER_ID + ", " +
                    WeightEntries.ENTRY_DATE + " DESC, " + WeightEntries.ID + " DESC)";
}
