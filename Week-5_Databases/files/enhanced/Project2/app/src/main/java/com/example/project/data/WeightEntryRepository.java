package com.example.project.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.project.model.ProgressSummary;
import com.example.project.model.WeightEntry;
import com.example.project.validation.InputValidator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** User-scoped CRUD and reporting queries for weight history. */
public final class WeightEntryRepository {
    private static final String[] PROJECTION = {
            DatabaseContract.WeightEntries.ID,
            DatabaseContract.WeightEntries.USER_ID,
            DatabaseContract.WeightEntries.WEIGHT,
            DatabaseContract.WeightEntries.ENTRY_DATE,
            DatabaseContract.WeightEntries.NOTE
    };

    private final DBHelper helper;

    public WeightEntryRepository(DBHelper helper) {
        this.helper = helper;
    }

    public long addEntry(long userId, double weight, String entryDate, String note) {
        InputValidator.positiveId(userId, "User ID");
        double validWeight = InputValidator.weight(weight);
        String validDate = InputValidator.isoDate(entryDate);
        String validNote = InputValidator.note(note);
        String timestamp = Instant.now().toString();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WeightEntries.USER_ID, userId);
        values.put(DatabaseContract.WeightEntries.WEIGHT, validWeight);
        values.put(DatabaseContract.WeightEntries.ENTRY_DATE, validDate);
        values.put(DatabaseContract.WeightEntries.NOTE, validNote);
        values.put(DatabaseContract.WeightEntries.CREATED_AT, timestamp);
        values.put(DatabaseContract.WeightEntries.UPDATED_AT, timestamp);
        return helper.getWritableDatabase().insertOrThrow(
                DatabaseContract.WeightEntries.TABLE,
                null,
                values);
    }

    public boolean updateEntry(
            long userId,
            long entryId,
            double weight,
            String entryDate,
            String note) {
        InputValidator.positiveId(userId, "User ID");
        InputValidator.positiveId(entryId, "Entry ID");
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WeightEntries.WEIGHT, InputValidator.weight(weight));
        values.put(DatabaseContract.WeightEntries.ENTRY_DATE, InputValidator.isoDate(entryDate));
        values.put(DatabaseContract.WeightEntries.NOTE, InputValidator.note(note));
        values.put(DatabaseContract.WeightEntries.UPDATED_AT, Instant.now().toString());
        return helper.getWritableDatabase().update(
                DatabaseContract.WeightEntries.TABLE,
                values,
                DatabaseContract.WeightEntries.ID + " = ? AND " +
                        DatabaseContract.WeightEntries.USER_ID + " = ?",
                new String[]{String.valueOf(entryId), String.valueOf(userId)}) == 1;
    }

    public boolean deleteEntry(long userId, long entryId) {
        InputValidator.positiveId(userId, "User ID");
        InputValidator.positiveId(entryId, "Entry ID");
        return helper.getWritableDatabase().delete(
                DatabaseContract.WeightEntries.TABLE,
                DatabaseContract.WeightEntries.ID + " = ? AND " +
                        DatabaseContract.WeightEntries.USER_ID + " = ?",
                new String[]{String.valueOf(entryId), String.valueOf(userId)}) == 1;
    }

    public WeightEntry getOwnedEntry(long userId, long entryId) {
        InputValidator.positiveId(userId, "User ID");
        InputValidator.positiveId(entryId, "Entry ID");
        try (Cursor cursor = helper.getReadableDatabase().query(
                DatabaseContract.WeightEntries.TABLE,
                PROJECTION,
                DatabaseContract.WeightEntries.ID + " = ? AND " +
                        DatabaseContract.WeightEntries.USER_ID + " = ?",
                new String[]{String.valueOf(entryId), String.valueOf(userId)},
                null,
                null,
                null,
                "1")) {
            return cursor.moveToFirst() ? map(cursor) : null;
        }
    }

    public List<WeightEntry> getHistory(long userId) {
        InputValidator.positiveId(userId, "User ID");
        List<WeightEntry> entries = new ArrayList<>();
        try (Cursor cursor = helper.getReadableDatabase().query(
                DatabaseContract.WeightEntries.TABLE,
                PROJECTION,
                DatabaseContract.WeightEntries.USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                DatabaseContract.WeightEntries.ENTRY_DATE + " DESC, " +
                        DatabaseContract.WeightEntries.ID + " DESC")) {
            while (cursor.moveToNext()) {
                entries.add(map(cursor));
            }
        }
        return Collections.unmodifiableList(entries);
    }

    public WeightEntry getLatestEntry(long userId) {
        InputValidator.positiveId(userId, "User ID");
        try (Cursor cursor = helper.getReadableDatabase().query(
                DatabaseContract.WeightEntries.TABLE,
                PROJECTION,
                DatabaseContract.WeightEntries.USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                DatabaseContract.WeightEntries.ENTRY_DATE + " DESC, " +
                        DatabaseContract.WeightEntries.ID + " DESC",
                "1")) {
            return cursor.moveToFirst() ? map(cursor) : null;
        }
    }

    public ProgressSummary getProgressSummary(long userId) {
        InputValidator.positiveId(userId, "User ID");
        String sql = "SELECT " +
                "COUNT(*) AS entry_count, " +
                "(SELECT " + DatabaseContract.WeightEntries.WEIGHT +
                " FROM " + DatabaseContract.WeightEntries.TABLE +
                " WHERE " + DatabaseContract.WeightEntries.USER_ID + " = ?" +
                " ORDER BY " + DatabaseContract.WeightEntries.ENTRY_DATE + " ASC, " +
                DatabaseContract.WeightEntries.ID + " ASC LIMIT 1) AS first_weight, " +
                "(SELECT " + DatabaseContract.WeightEntries.ENTRY_DATE +
                " FROM " + DatabaseContract.WeightEntries.TABLE +
                " WHERE " + DatabaseContract.WeightEntries.USER_ID + " = ?" +
                " ORDER BY " + DatabaseContract.WeightEntries.ENTRY_DATE + " ASC, " +
                DatabaseContract.WeightEntries.ID + " ASC LIMIT 1) AS first_date, " +
                "(SELECT " + DatabaseContract.WeightEntries.WEIGHT +
                " FROM " + DatabaseContract.WeightEntries.TABLE +
                " WHERE " + DatabaseContract.WeightEntries.USER_ID + " = ?" +
                " ORDER BY " + DatabaseContract.WeightEntries.ENTRY_DATE + " DESC, " +
                DatabaseContract.WeightEntries.ID + " DESC LIMIT 1) AS latest_weight, " +
                "(SELECT " + DatabaseContract.WeightEntries.ENTRY_DATE +
                " FROM " + DatabaseContract.WeightEntries.TABLE +
                " WHERE " + DatabaseContract.WeightEntries.USER_ID + " = ?" +
                " ORDER BY " + DatabaseContract.WeightEntries.ENTRY_DATE + " DESC, " +
                DatabaseContract.WeightEntries.ID + " DESC LIMIT 1) AS latest_date " +
                "FROM " + DatabaseContract.WeightEntries.TABLE +
                " WHERE " + DatabaseContract.WeightEntries.USER_ID + " = ?";
        String id = String.valueOf(userId);
        try (Cursor cursor = helper.getReadableDatabase().rawQuery(
                sql,
                new String[]{id, id, id, id, id})) {
            if (!cursor.moveToFirst()) {
                return new ProgressSummary(0, null, null, null, null);
            }
            int count = cursor.getInt(0);
            if (count == 0) {
                return new ProgressSummary(0, null, null, null, null);
            }
            return new ProgressSummary(
                    count,
                    cursor.getDouble(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4));
        }
    }

    private WeightEntry map(Cursor cursor) {
        return new WeightEntry(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getDouble(2),
                cursor.getString(3),
                cursor.getString(4));
    }
}
