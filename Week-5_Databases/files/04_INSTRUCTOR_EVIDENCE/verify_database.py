#!/usr/bin/env python3
"""Standalone verification for the Milestone Four SQLite design.

The script mirrors the submitted schema, parameterized queries, ownership rules,
progress queries, index, and version-one migration with Python's standard sqlite3
module. Android instrumentation tests are also included in the enhanced project.
"""
from __future__ import annotations

import base64
import hashlib
import os
import secrets
import sqlite3
import tempfile
from datetime import UTC, datetime
from pathlib import Path

ITERATIONS = 600_000
ALGORITHM = "PBKDF2WithHmacSHA256"

CREATE_USERS = """
CREATE TABLE users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL COLLATE NOCASE UNIQUE,
    password_hash TEXT NOT NULL,
    password_salt TEXT NOT NULL,
    password_algorithm TEXT NOT NULL,
    password_iterations INTEGER NOT NULL CHECK(password_iterations > 0),
    created_at TEXT NOT NULL
)
"""

CREATE_ENTRIES = """
CREATE TABLE weight_entries (
    entry_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    weight REAL NOT NULL CHECK(weight > 0 AND weight <= 1500),
    entry_date TEXT NOT NULL CHECK(length(entry_date) = 10 AND date(entry_date) = entry_date),
    note TEXT NOT NULL DEFAULT '' CHECK(length(note) <= 250),
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
)
"""

CREATE_ISSUES = """
CREATE TABLE migration_issues (
    issue_id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_table TEXT NOT NULL,
    source_identifier TEXT,
    reason TEXT NOT NULL,
    raw_weight TEXT,
    raw_date TEXT,
    created_at TEXT NOT NULL
)
"""

CREATE_INDEX = """
CREATE INDEX idx_weight_entries_user_date
ON weight_entries(user_id, entry_date DESC, entry_id DESC)
"""

checks = 0
lines: list[str] = []


def record(condition: bool, description: str) -> None:
    global checks
    if not condition:
        raise AssertionError(description)
    checks += 1
    line = f"PASS {checks:02d}: {description}"
    lines.append(line)
    print(line)


def now() -> str:
    return datetime.now(UTC).replace(microsecond=0).isoformat().replace("+00:00", "Z")


def connect(path: Path | str = ":memory:") -> sqlite3.Connection:
    connection = sqlite3.connect(path)
    connection.row_factory = sqlite3.Row
    connection.execute("PRAGMA foreign_keys = ON")
    return connection


def create_v2(connection: sqlite3.Connection) -> None:
    connection.execute(CREATE_USERS)
    connection.execute(CREATE_ENTRIES)
    connection.execute(CREATE_ISSUES)
    connection.execute(CREATE_INDEX)


def hash_password(password: str) -> tuple[str, str]:
    salt = secrets.token_bytes(16)
    derived = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, ITERATIONS, dklen=32)
    return base64.b64encode(derived).decode(), base64.b64encode(salt).decode()


def verify_password(password: str, encoded_hash: str, encoded_salt: str) -> bool:
    salt = base64.b64decode(encoded_salt)
    actual = hashlib.pbkdf2_hmac("sha256", password.encode(), salt, ITERATIONS, dklen=32)
    return secrets.compare_digest(actual, base64.b64decode(encoded_hash))


def register(connection: sqlite3.Connection, username: str, password: str) -> int:
    username = username.strip()
    encoded_hash, encoded_salt = hash_password(password)
    cursor = connection.execute(
        """
        INSERT INTO users(
            username, password_hash, password_salt,
            password_algorithm, password_iterations, created_at
        ) VALUES (?, ?, ?, ?, ?, ?)
        """,
        (username, encoded_hash, encoded_salt, ALGORITHM, ITERATIONS, now()),
    )
    return int(cursor.lastrowid)


def authenticate(connection: sqlite3.Connection, username: str, password: str) -> int | None:
    row = connection.execute(
        """
        SELECT user_id, password_hash, password_salt, password_algorithm, password_iterations
        FROM users WHERE username = ? COLLATE NOCASE LIMIT 1
        """,
        (username.strip(),),
    ).fetchone()
    if not row or row["password_algorithm"] != ALGORITHM or row["password_iterations"] <= 0:
        return None
    return int(row["user_id"]) if verify_password(password, row["password_hash"], row["password_salt"]) else None


def add_entry(connection: sqlite3.Connection, user_id: int, weight: float, date: str, note: str = "") -> int:
    stamp = now()
    cursor = connection.execute(
        """
        INSERT INTO weight_entries(user_id, weight, entry_date, note, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        (user_id, weight, date, note.strip(), stamp, stamp),
    )
    return int(cursor.lastrowid)


def history(connection: sqlite3.Connection, user_id: int):
    return connection.execute(
        """
        SELECT entry_id, user_id, weight, entry_date, note
        FROM weight_entries
        WHERE user_id = ?
        ORDER BY entry_date DESC, entry_id DESC
        """,
        (user_id,),
    ).fetchall()


def update_owned(connection: sqlite3.Connection, user_id: int, entry_id: int, weight: float, date: str, note: str) -> bool:
    result = connection.execute(
        """
        UPDATE weight_entries
        SET weight = ?, entry_date = ?, note = ?, updated_at = ?
        WHERE entry_id = ? AND user_id = ?
        """,
        (weight, date, note, now(), entry_id, user_id),
    )
    return result.rowcount == 1


def delete_owned(connection: sqlite3.Connection, user_id: int, entry_id: int) -> bool:
    result = connection.execute(
        "DELETE FROM weight_entries WHERE entry_id = ? AND user_id = ?",
        (entry_id, user_id),
    )
    return result.rowcount == 1


def progress(connection: sqlite3.Connection, user_id: int):
    return connection.execute(
        """
        SELECT
            COUNT(*) AS entry_count,
            (SELECT weight FROM weight_entries WHERE user_id = ?
             ORDER BY entry_date ASC, entry_id ASC LIMIT 1) AS first_weight,
            (SELECT entry_date FROM weight_entries WHERE user_id = ?
             ORDER BY entry_date ASC, entry_id ASC LIMIT 1) AS first_date,
            (SELECT weight FROM weight_entries WHERE user_id = ?
             ORDER BY entry_date DESC, entry_id DESC LIMIT 1) AS latest_weight,
            (SELECT entry_date FROM weight_entries WHERE user_id = ?
             ORDER BY entry_date DESC, entry_id DESC LIMIT 1) AS latest_date
        FROM weight_entries WHERE user_id = ?
        """,
        (user_id, user_id, user_id, user_id, user_id),
    ).fetchone()


def normalize_date(raw: str) -> str | None:
    for fmt in ("%Y-%m-%d", "%B %d, %Y", "%m/%d/%Y"):
        try:
            return datetime.strptime(raw.strip(), fmt).date().isoformat()
        except (ValueError, AttributeError):
            pass
    return None


def normalize_weight(raw: str) -> float | None:
    try:
        value = float(raw.strip())
        return value if 0 < value <= 1500 else None
    except (ValueError, AttributeError):
        return None


def migrate_v1_to_v2(connection: sqlite3.Connection) -> None:
    connection.execute("ALTER TABLE users RENAME TO users_legacy")
    connection.execute("ALTER TABLE Userdetails RENAME TO userdetails_legacy")
    create_v2(connection)
    user_ids: dict[str, int] = {}

    for sequence, row in enumerate(connection.execute("SELECT username, password FROM users_legacy"), 1):
        raw_username, raw_password = row
        base = (raw_username or "").strip() or f"legacy_user_{sequence}"
        candidate = base
        suffix = 1
        while connection.execute(
            "SELECT 1 FROM users WHERE username = ? COLLATE NOCASE", (candidate,)
        ).fetchone():
            candidate = f"{base}_legacy_{suffix}"
            suffix += 1
        user_id = register(connection, candidate, raw_password or f"legacy-unavailable-{candidate}")
        if raw_username and raw_username.strip():
            user_ids.setdefault(raw_username.strip().lower(), user_id)

    fallback_id: int | None = None
    for row in connection.execute("SELECT name, weight, date FROM userdetails_legacy"):
        name, raw_weight, raw_date = row
        weight = normalize_weight(raw_weight)
        date = normalize_date(raw_date)
        if weight is None or date is None:
            if weight is None and date is None:
                reason = "Weight and date could not be normalized."
            elif weight is None:
                reason = "Weight could not be normalized."
            else:
                reason = "Date could not be normalized."
            connection.execute(
                """
                INSERT INTO migration_issues(
                    source_table, source_identifier, reason,
                    raw_weight, raw_date, created_at
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                ("Userdetails", name, reason, raw_weight, raw_date, now()),
            )
            continue
        owner_id = user_ids.get((name or "").strip().lower())
        if owner_id is None:
            if fallback_id is None:
                fallback_id = register(connection, "legacy_import", secrets.token_urlsafe(24))
            owner_id = fallback_id
        add_entry(connection, owner_id, weight, date, f"Imported from original record labeled: {(name or 'unnamed').strip()}")

    connection.execute("DROP TABLE userdetails_legacy")
    connection.execute("DROP TABLE users_legacy")


def run() -> Path:
    with tempfile.TemporaryDirectory() as temporary:
        path = Path(temporary) / "weight_tracker_verified.db"
        connection = connect(path)
        create_v2(connection)

        # Schema and structural checks.
        record(connection.execute("PRAGMA foreign_keys").fetchone()[0] == 1, "foreign-key enforcement is enabled")
        tables = {row[0] for row in connection.execute("SELECT name FROM sqlite_master WHERE type='table'")}
        record({"users", "weight_entries", "migration_issues"}.issubset(tables), "all version-two tables are created")
        user_columns = {row[1]: row[2] for row in connection.execute("PRAGMA table_info(users)")}
        record(user_columns["user_id"] == "INTEGER", "users has an integer user_id")
        record("password_hash" in user_columns and "password_salt" in user_columns, "users stores a hash and salt instead of a password column")
        entry_columns = {row[1]: row[2] for row in connection.execute("PRAGMA table_info(weight_entries)")}
        record(entry_columns["entry_id"] == "INTEGER", "weight_entries has an integer entry_id")
        record(entry_columns["user_id"] == "INTEGER", "weight_entries has an integer user_id")
        record(entry_columns["weight"] == "REAL", "weight is stored with REAL affinity")
        foreign_keys = connection.execute("PRAGMA foreign_key_list(weight_entries)").fetchall()
        record(len(foreign_keys) == 1 and foreign_keys[0][2] == "users", "weight_entries references users")
        record(foreign_keys[0][6].upper() == "CASCADE", "the user relationship uses ON DELETE CASCADE")
        indexes = {row[1] for row in connection.execute("PRAGMA index_list(weight_entries)")}
        record("idx_weight_entries_user_date" in indexes, "the user/date history index exists")

        # Registration and password storage.
        madison = register(connection, "Madison", "correct-horse-battery-staple")
        william = register(connection, "William", "another-correct-password")
        record(madison > 0 and william > 0 and madison != william, "registration returns stable, distinct user IDs")
        stored = connection.execute(
            "SELECT password_hash, password_salt, password_algorithm, password_iterations FROM users WHERE user_id = ?",
            (madison,),
        ).fetchone()
        record(stored[0] != "correct-horse-battery-staple", "plaintext password is not stored")
        record(len(base64.b64decode(stored[1])) == 16, "each account uses a 128-bit random salt")
        record(stored[2] == ALGORITHM and stored[3] == ITERATIONS, "algorithm and work factor are stored with the verifier")
        record(authenticate(connection, "madison", "correct-horse-battery-staple") == madison, "authentication is case-insensitive for username")
        record(authenticate(connection, "Madison", "wrong-password") is None, "incorrect password is rejected")
        try:
            register(connection, "MADISON", "third-password")
            duplicate_rejected = False
        except sqlite3.IntegrityError:
            duplicate_rejected = True
        record(duplicate_rejected, "duplicate usernames are rejected without case sensitivity")
        second_hash, second_salt = hash_password("correct-horse-battery-staple")
        record(second_salt != stored[1] and second_hash != stored[0], "identical passwords receive different salts and hashes")

        # User-scoped CRUD.
        first = add_entry(connection, madison, 140.0, "2026-06-01", "starting point")
        second = add_entry(connection, madison, 138.5, "2026-07-01", "monthly check")
        tie = add_entry(connection, madison, 138.2, "2026-07-01", "same date, later entry ID")
        other = add_entry(connection, william, 200.0, "2026-07-15", "separate account")
        record(all(value > 0 for value in (first, second, tie, other)), "new entries receive numeric primary keys")
        record(len(history(connection, madison)) == 3, "history returns only the requested user's records")
        record(len(history(connection, william)) == 1, "a second user's history remains separate")
        ordered = history(connection, madison)
        record([row["entry_id"] for row in ordered[:2]] == [tie, second], "same-date records use entry_id as a deterministic tie-breaker")
        record(not update_owned(connection, william, first, 99.0, "2026-07-02", "blocked"), "cross-user update is rejected by the SQL WHERE clause")
        record(not delete_owned(connection, william, first), "cross-user delete is rejected by the SQL WHERE clause")
        record(update_owned(connection, madison, first, 139.5, "2026-06-02", "corrected"), "owner can update the record")
        record(connection.execute("SELECT weight FROM weight_entries WHERE entry_id = ?", (first,)).fetchone()[0] == 139.5, "owner update persists the new value")
        removable = add_entry(connection, madison, 138.0, "2026-08-01", "temporary")
        record(delete_owned(connection, madison, removable), "owner can delete the record")
        record(connection.execute("SELECT 1 FROM weight_entries WHERE entry_id = ?", (removable,)).fetchone() is None, "deleted record is absent")

        # Constraints.
        invalid_weight_rejected = False
        try:
            add_entry(connection, madison, 0, "2026-08-01", "invalid")
        except sqlite3.IntegrityError:
            invalid_weight_rejected = True
        record(invalid_weight_rejected, "database CHECK constraint rejects zero weight")
        invalid_user_rejected = False
        try:
            add_entry(connection, 999999, 130, "2026-08-01", "orphan")
        except sqlite3.IntegrityError:
            invalid_user_rejected = True
        record(invalid_user_rejected, "foreign key rejects an entry for a missing user")
        long_note_rejected = False
        try:
            add_entry(connection, madison, 130, "2026-08-01", "x" * 251)
        except sqlite3.IntegrityError:
            long_note_rejected = True
        record(long_note_rejected, "database CHECK constraint rejects notes over 250 characters")
        date_length_rejected = False
        try:
            add_entry(connection, madison, 130, "August 1, 2026", "bad date")
        except sqlite3.IntegrityError:
            date_length_rejected = True
        record(date_length_rejected, "database CHECK constraint rejects non-ISO date length")
        impossible_date_rejected = False
        try:
            add_entry(connection, madison, 130, "2026-02-30", "impossible date")
        except sqlite3.IntegrityError:
            impossible_date_rejected = True
        record(impossible_date_rejected, "database CHECK constraint rejects an impossible ISO date")
        iteration_constraint_rejected = False
        try:
            connection.execute(
                """
                INSERT INTO users(
                    username, password_hash, password_salt,
                    password_algorithm, password_iterations, created_at
                ) VALUES (?, ?, ?, ?, ?, ?)
                """,
                ("bad_iterations", "hash", "salt", ALGORITHM, 0, now()),
            )
        except sqlite3.IntegrityError:
            iteration_constraint_rejected = True
        record(iteration_constraint_rejected, "database CHECK constraint rejects a nonpositive password work factor")

        # Reporting and query plan.
        summary = progress(connection, madison)
        record(summary["entry_count"] == 3, "progress query returns the correct entry count")
        record(summary["first_date"] == "2026-06-02" and summary["latest_date"] == "2026-07-01", "progress query finds chronological endpoints")
        record(abs((summary["latest_weight"] - summary["first_weight"]) - (-1.3)) < 0.0001, "progress change is calculated from first to latest weight")
        empty_user = register(connection, "empty_user", "password-for-empty-user")
        empty = progress(connection, empty_user)
        record(empty["entry_count"] == 0 and empty["first_weight"] is None, "progress query handles an empty history")
        plan = " ".join(str(value) for row in connection.execute(
            "EXPLAIN QUERY PLAN SELECT entry_id FROM weight_entries WHERE user_id = ? ORDER BY entry_date DESC, entry_id DESC",
            (madison,),
        ) for value in row)
        record("idx_weight_entries_user_date" in plan, "SQLite query plan uses the history index")

        # Cascade deletion.
        cascade_user = register(connection, "cascade_user", "password-for-cascade")
        add_entry(connection, cascade_user, 150, "2026-08-01", "cascade test")
        connection.execute("DELETE FROM users WHERE user_id = ?", (cascade_user,))
        remaining = connection.execute("SELECT COUNT(*) FROM weight_entries WHERE user_id = ?", (cascade_user,)).fetchone()[0]
        record(remaining == 0, "deleting a user removes dependent weight entries")

        connection.commit()
        connection.close()

        # Migration checks use a separate legacy database.
        migration_path = Path(temporary) / "migration.db"
        legacy = connect(migration_path)
        legacy.execute("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)")
        legacy.execute("CREATE TABLE Userdetails(name TEXT PRIMARY KEY, weight TEXT, date TEXT)")
        legacy.execute("INSERT INTO users VALUES('Madison', 'legacy-password')")
        legacy.execute("INSERT INTO users VALUES('William', 'legacy-password-two')")
        legacy.execute("INSERT INTO Userdetails VALUES('Madison', '138.5', 'June 22, 2026')")
        legacy.execute("INSERT INTO Userdetails VALUES('Unmatched Name', '175', '07/01/2026')")
        legacy.execute("INSERT INTO Userdetails VALUES('Broken Row', 'not-a-number', 'not-a-date')")
        with legacy:
            migrate_v1_to_v2(legacy)
        record(authenticate(legacy, "Madison", "legacy-password") is not None, "migration hashes legacy plaintext credentials while preserving login")
        madison_id = authenticate(legacy, "Madison", "legacy-password")
        record(len(history(legacy, madison_id)) == 1, "legacy record with matching name is linked to that user")
        record(history(legacy, madison_id)[0]["entry_date"] == "2026-06-22", "legacy long-form date is normalized to ISO")
        legacy_import_id = legacy.execute("SELECT user_id FROM users WHERE username = 'legacy_import'").fetchone()[0]
        record(len(history(legacy, legacy_import_id)) == 1, "unmatched legacy record is preserved under a separate import account")
        record(legacy.execute("SELECT COUNT(*) FROM migration_issues").fetchone()[0] == 1, "unreadable legacy record is logged instead of silently discarded")
        record(not {"users_legacy", "userdetails_legacy"}.intersection(
            {row[0] for row in legacy.execute("SELECT name FROM sqlite_master WHERE type='table'")}
        ), "legacy tables are removed only after the migration completes")
        legacy.close()


    final = f"PASS: {checks} database verification checks completed successfully."
    print(final)
    lines.append(final)
    Path(__file__).with_name("DATABASE_VERIFICATION_RESULTS.txt").write_text("\n".join(lines) + "\n", encoding="utf-8")
    return Path(__file__).with_name("DATABASE_VERIFICATION_RESULTS.txt")


if __name__ == "__main__":
    run()
