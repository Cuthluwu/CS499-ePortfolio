package android.database;

/** Minimal compile-time stub used only for the recorded source syntax check. */
public interface Cursor extends AutoCloseable {
    boolean moveToFirst();

    boolean moveToNext();

    String getString(int columnIndex);

    @Override
    void close();
}
