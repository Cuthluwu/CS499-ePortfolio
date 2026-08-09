package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project.data.DBHelper;
import com.example.project.data.UserRepository;
import com.example.project.model.UserSession;
import java.util.Arrays;

/** Login screen. Successful authentication passes a stable user ID to the dashboard. */
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_PREFILL_USERNAME = "prefill_username";

    private DBHelper database;
    private UserRepository users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username = findViewById(R.id.editTextTextPersonName);
        EditText password = findViewById(R.id.editText);
        Button login = findViewById(R.id.button);
        Button signup = findViewById(R.id.button2);
        database = new DBHelper(this);
        users = new UserRepository(database);

        String prefill = getIntent().getStringExtra(EXTRA_PREFILL_USERNAME);
        if (prefill != null) {
            username.setText(prefill);
        }

        login.setOnClickListener(view -> {
            String usernameText = username.getText().toString();
            char[] passwordCharacters = password.getText().toString().toCharArray();
            try {
                UserSession session = users.authenticate(usernameText, passwordCharacters);
                if (session == null) {
                    Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent dashboard = new Intent(this, MainActivity2.class);
                dashboard.putExtra(MainActivity2.EXTRA_USER_ID, session.getUserId());
                dashboard.putExtra(MainActivity2.EXTRA_USERNAME, session.getUsername());
                startActivity(dashboard);
                password.setText("");
            } finally {
                Arrays.fill(passwordCharacters, '\0');
            }
        });

        signup.setOnClickListener(view ->
                startActivity(new Intent(this, MainActivity4.class)));
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}
