PRAGMA foreign_keys = ON;

CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL COLLATE NOCASE UNIQUE,
    password_hash TEXT NOT NULL,
    password_salt TEXT NOT NULL,
    password_algorithm TEXT NOT NULL,
    password_iterations INTEGER NOT NULL CHECK(password_iterations > 0),
    created_at TEXT NOT NULL
);

CREATE TABLE weight_entries (
    entry_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    weight REAL NOT NULL CHECK(weight > 0 AND weight <= 1500),
    entry_date TEXT NOT NULL CHECK(length(entry_date) = 10 AND date(entry_date) = entry_date),
    note TEXT NOT NULL DEFAULT '' CHECK(length(note) <= 250),
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE migration_issues (
    issue_id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_table TEXT NOT NULL,
    source_identifier TEXT,
    reason TEXT NOT NULL,
    raw_weight TEXT,
    raw_date TEXT,
    created_at TEXT NOT NULL
);

CREATE INDEX idx_weight_entries_user_date
ON weight_entries(user_id, entry_date DESC, entry_id DESC);
