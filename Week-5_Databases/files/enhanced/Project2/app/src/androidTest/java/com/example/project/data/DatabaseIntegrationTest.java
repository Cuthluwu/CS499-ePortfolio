package com.example.project.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.project.model.ProgressSummary;
import com.example.project.model.UserSession;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseIntegrationTest {
    private Context context;
    private DBHelper helper;
    private UserRepository users;
    private WeightEntryRepository entries;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(DatabaseContract.DATABASE_NAME);
        helper = new DBHelper(context);
        users = new UserRepository(helper);
        entries = new WeightEntryRepository(helper);
    }

    @After
    public void tearDown() {
        helper.close();
        context.deleteDatabase(DatabaseContract.DATABASE_NAME);
    }

    @Test
    public void passwordIsHashedAndAuthenticationUsesStoredVerifier() {
        long userId = users.registerUser("Madison", "not-plain-text".toCharArray());
        assertTrue(userId > 0);
        try (Cursor cursor = helper.getReadableDatabase().rawQuery(
                "SELECT password_hash, password_salt FROM users WHERE user_id = ?",
                new String[]{String.valueOf(userId)})) {
            assertTrue(cursor.moveToFirst());
            assertNotEquals("not-plain-text", cursor.getString(0));
            assertFalse(cursor.getString(1).isEmpty());
        }
        UserSession valid = users.authenticate("madison", "not-plain-text".toCharArray());
        assertNotNull(valid);
        assertNull(users.authenticate("madison", "incorrect".toCharArray()));
    }

    @Test
    public void crudIsRestrictedToTheOwningUser() {
        long owner = users.registerUser("owner", "password-one".toCharArray());
        long other = users.registerUser("other", "password-two".toCharArray());
        long entryId = entries.addEntry(owner, 140.0, "2026-07-01", "start");

        assertNotNull(entries.getOwnedEntry(owner, entryId));
        assertNull(entries.getOwnedEntry(other, entryId));
        assertFalse(entries.updateEntry(other, entryId, 130.0, "2026-07-02", "blocked"));
        assertFalse(entries.deleteEntry(other, entryId));
        assertTrue(entries.updateEntry(owner, entryId, 139.0, "2026-07-02", "owner update"));
        assertTrue(entries.deleteEntry(owner, entryId));
    }

    @Test
    public void historySummaryAndCascadeDeleteAreCorrect() {
        long userId = users.registerUser("progress_user", "password-three".toCharArray());
        entries.addEntry(userId, 140.0, "2026-07-01", "first");
        entries.addEntry(userId, 137.5, "2026-08-01", "latest");

        assertEquals(2, entries.getHistory(userId).size());
        assertEquals("2026-08-01", entries.getLatestEntry(userId).getEntryDate());
        ProgressSummary summary = entries.getProgressSummary(userId);
        assertEquals(2, summary.getEntryCount());
        assertEquals(-2.5, summary.getChange(), 0.0001);

        assertTrue(users.deleteUser(userId));
        try (Cursor cursor = helper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM weight_entries WHERE user_id = ?",
                new String[]{String.valueOf(userId)})) {
            assertTrue(cursor.moveToFirst());
            assertEquals(0, cursor.getInt(0));
        }
    }

    @Test
    public void versionOneDatabaseMigratesWithoutDroppingReadableRecords() {
        helper.close();
        context.deleteDatabase(DatabaseContract.DATABASE_NAME);
        File file = context.getDatabasePath(DatabaseContract.DATABASE_NAME);
        file.getParentFile().mkdirs();
        SQLiteDatabase legacy = SQLiteDatabase.openOrCreateDatabase(file, null);
        legacy.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)");
        legacy.execSQL("CREATE TABLE Userdetails(name TEXT PRIMARY KEY, weight TEXT, date TEXT)");
        legacy.execSQL("INSERT INTO users VALUES('Madison', 'legacy-password')");
        legacy.execSQL("INSERT INTO Userdetails VALUES('Madison', '138.5', 'June 22, 2026')");
        legacy.setVersion(1);
        legacy.close();

        helper = new DBHelper(context);
        users = new UserRepository(helper);
        entries = new WeightEntryRepository(helper);
        UserSession migrated = users.authenticate("Madison", "legacy-password".toCharArray());
        assertNotNull(migrated);
        assertEquals(1, entries.getHistory(migrated.getUserId()).size());
        assertEquals("2026-06-22", entries.getHistory(migrated.getUserId()).get(0).getEntryDate());
    }
}
