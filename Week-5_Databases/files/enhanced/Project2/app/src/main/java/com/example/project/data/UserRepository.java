package com.example.project.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import com.example.project.model.UserSession;
import com.example.project.security.PasswordHasher;
import com.example.project.validation.InputValidator;
import java.time.Instant;

/** Account persistence and authentication. Plaintext passwords never enter SQLite. */
public final class UserRepository {
    private final DBHelper helper;

    public UserRepository(DBHelper helper) {
        this.helper = helper;
    }

    public long registerUser(String rawUsername, char[] password) {
        String username = InputValidator.username(rawUsername);
        InputValidator.password(password);
        PasswordHasher.HashResult result = PasswordHasher.hash(password);

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.USERNAME, username);
        values.put(DatabaseContract.Users.PASSWORD_HASH, result.getHash());
        values.put(DatabaseContract.Users.PASSWORD_SALT, result.getSalt());
        values.put(DatabaseContract.Users.PASSWORD_ALGORITHM, result.getAlgorithm());
        values.put(DatabaseContract.Users.PASSWORD_ITERATIONS, result.getIterations());
        values.put(DatabaseContract.Users.CREATED_AT, Instant.now().toString());
        try {
            return helper.getWritableDatabase().insertOrThrow(
                    DatabaseContract.Users.TABLE,
                    null,
                    values);
        } catch (SQLiteConstraintException exception) {
            throw new IllegalArgumentException("That username already exists.", exception);
        }
    }

    public UserSession authenticate(String rawUsername, char[] password) {
        if (rawUsername == null || rawUsername.trim().isEmpty() || password == null) {
            return null;
        }
        SQLiteDatabase database = helper.getReadableDatabase();
        String sql = "SELECT " + DatabaseContract.Users.ID + ", " +
                DatabaseContract.Users.USERNAME + ", " +
                DatabaseContract.Users.PASSWORD_HASH + ", " +
                DatabaseContract.Users.PASSWORD_SALT + ", " +
                DatabaseContract.Users.PASSWORD_ALGORITHM + ", " +
                DatabaseContract.Users.PASSWORD_ITERATIONS +
                " FROM " + DatabaseContract.Users.TABLE +
                " WHERE " + DatabaseContract.Users.USERNAME + " = ? COLLATE NOCASE LIMIT 1";
        try (Cursor cursor = database.rawQuery(sql, new String[]{rawUsername.trim()})) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            boolean valid = PasswordHasher.verify(
                    password,
                    cursor.getString(3),
                    cursor.getString(2),
                    cursor.getString(4),
                    cursor.getInt(5));
            return valid ? new UserSession(cursor.getLong(0), cursor.getString(1)) : null;
        }
    }

    public boolean deleteUser(long userId) {
        InputValidator.positiveId(userId, "User ID");
        return helper.getWritableDatabase().delete(
                DatabaseContract.Users.TABLE,
                DatabaseContract.Users.ID + " = ?",
                new String[]{String.valueOf(userId)}) == 1;
    }
}
