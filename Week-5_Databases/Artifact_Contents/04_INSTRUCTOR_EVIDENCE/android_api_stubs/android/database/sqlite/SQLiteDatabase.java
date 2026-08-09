package android.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

/** Minimal compile-time stub used only for the recorded source syntax check. */
public class SQLiteDatabase {
    public void setForeignKeyConstraintsEnabled(boolean enabled) {
    }

    public void execSQL(String sql) {
    }

    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        return 0L;
    }

    public Cursor query(
            String table,
            String[] columns,
            String selection,
            String[] selectionArgs,
            String groupBy,
            String having,
            String orderBy) {
        return null;
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return null;
    }
}
