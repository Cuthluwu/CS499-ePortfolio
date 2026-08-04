package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project.data.DBHelper;
import com.example.project.data.WeightEntryRepository;
import com.example.project.model.ProgressSummary;
import com.example.project.model.WeightEntry;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/** Authenticated dashboard. Every database operation is restricted by user ID. */
public class MainActivity2 extends AppCompatActivity {
    public static final String EXTRA_USER_ID = "authenticated_user_id";
    public static final String EXTRA_USERNAME = "authenticated_username";

    private DBHelper database;
    private WeightEntryRepository entries;
    private long userId;
    private TextView progressSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getLongExtra(EXTRA_USER_ID, -1L);
        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (userId <= 0 || username == null || username.trim().isEmpty()) {
            Toast.makeText(this, "Please sign in before opening the dashboard.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main2);
        database = new DBHelper(this);
        entries = new WeightEntryRepository(database);

        TextView currentUser = findViewById(R.id.currentUser);
        progressSummary = findViewById(R.id.progressSummary);
        EditText entryId = findViewById(R.id.entryId);
        EditText weight = findViewById(R.id.weight);
        EditText date = findViewById(R.id.date1);
        EditText note = findViewById(R.id.note);
        Button insert = findViewById(R.id.btnInsert);
        Button update = findViewById(R.id.btnUpdate);
        Button delete = findViewById(R.id.btnDelete);
        Button view = findViewById(R.id.btnView);
        Button settings = findViewById(R.id.button1);
        Button logout = findViewById(R.id.btnLogout);

        currentUser.setText(getString(R.string.signed_in_as, username));
        date.setText(LocalDate.now().toString());
        refreshSummary();

        insert.setOnClickListener(button -> runDatabaseAction(() -> {
            long newId = entries.addEntry(
                    userId,
                    parseWeight(weight),
                    date.getText().toString(),
                    note.getText().toString());
            entryId.setText(String.valueOf(newId));
            Toast.makeText(this, "Entry " + newId + " was saved.", Toast.LENGTH_SHORT).show();
            refreshSummary();
        }));

        update.setOnClickListener(button -> runDatabaseAction(() -> {
            boolean changed = entries.updateEntry(
                    userId,
                    parseEntryId(entryId),
                    parseWeight(weight),
                    date.getText().toString(),
                    note.getText().toString());
            Toast.makeText(
                    this,
                    changed ? "Entry updated." : "Entry was not found for this account.",
                    Toast.LENGTH_SHORT).show();
            refreshSummary();
        }));

        delete.setOnClickListener(button -> runDatabaseAction(() -> {
            boolean removed = entries.deleteEntry(userId, parseEntryId(entryId));
            Toast.makeText(
                    this,
                    removed ? "Entry deleted." : "Entry was not found for this account.",
                    Toast.LENGTH_SHORT).show();
            refreshSummary();
        }));

        view.setOnClickListener(button -> runDatabaseAction(this::showHistory));
        settings.setOnClickListener(button ->
                startActivity(new Intent(this, MainActivity3.class)));
        logout.setOnClickListener(button -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void showHistory() {
        List<WeightEntry> history = entries.getHistory(userId);
        if (history.isEmpty()) {
            Toast.makeText(this, "No weight entries exist for this account.", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder message = new StringBuilder();
        for (WeightEntry entry : history) {
            message.append("Entry ID: ").append(entry.getEntryId()).append('\n');
            message.append("Weight: ")
                    .append(String.format(Locale.US, "%.1f", entry.getWeight()))
                    .append('\n');
            message.append("Date: ").append(entry.getEntryDate()).append('\n');
            if (!entry.getNote().isEmpty()) {
                message.append("Note: ").append(entry.getNote()).append('\n');
            }
            message.append('\n');
        }
        new AlertDialog.Builder(this)
                .setTitle("Your Weight History")
                .setMessage(message.toString())
                .setPositiveButton("Close", null)
                .show();
    }

    private void refreshSummary() {
        ProgressSummary summary = entries.getProgressSummary(userId);
        if (!summary.hasData()) {
            progressSummary.setText(R.string.no_progress_data);
            return;
        }
        progressSummary.setText(getString(
                R.string.progress_summary,
                summary.getEntryCount(),
                summary.getFirstWeight(),
                summary.getFirstDate(),
                summary.getLatestWeight(),
                summary.getLatestDate(),
                summary.getChange()));
    }

    private long parseEntryId(EditText field) {
        try {
            return Long.parseLong(field.getText().toString().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a numeric entry ID for update or delete.");
        }
    }

    private double parseWeight(EditText field) {
        try {
            return Double.parseDouble(field.getText().toString().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a numeric weight.");
        }
    }

    private void runDatabaseAction(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        } catch (RuntimeException exception) {
            Toast.makeText(this, "The database operation could not be completed.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}
