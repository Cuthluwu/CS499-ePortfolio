package android.database.sqlite;

import android.content.Context;

/** Minimal compile-time stub used only for the recorded source syntax check. */
public abstract class SQLiteOpenHelper {
    protected SQLiteOpenHelper(Context context, String name, Object factory, int version) {
    }

    public void onConfigure(SQLiteDatabase database) {
    }

    public abstract void onCreate(SQLiteDatabase database);

    public abstract void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);
}
