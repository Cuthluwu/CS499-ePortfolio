package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project.data.DBHelper;
import com.example.project.data.UserRepository;
import java.util.Arrays;

/** Registration screen. It no longer provides a route around authentication. */
public class MainActivity4 extends AppCompatActivity {
    private DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        EditText username = findViewById(R.id.editText1);
        EditText password = findViewById(R.id.editText);
        EditText repeatedPassword = findViewById(R.id.editText2);
        Button signup = findViewById(R.id.button);
        Button signin = findViewById(R.id.button1);
        database = new DBHelper(this);
        UserRepository users = new UserRepository(database);

        signup.setOnClickListener(view -> {
            String usernameText = username.getText().toString();
            char[] passwordCharacters = password.getText().toString().toCharArray();
            char[] repeatedCharacters = repeatedPassword.getText().toString().toCharArray();
            try {
                if (!Arrays.equals(passwordCharacters, repeatedCharacters)) {
                    throw new IllegalArgumentException("Passwords do not match.");
                }
                users.registerUser(usernameText, passwordCharacters);
                Toast.makeText(this, "Account created. Please sign in.", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(this, MainActivity.class);
                login.putExtra(MainActivity.EXTRA_PREFILL_USERNAME, usernameText.trim());
                startActivity(login);
                finish();
            } catch (IllegalArgumentException exception) {
                Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                Arrays.fill(passwordCharacters, '\0');
                Arrays.fill(repeatedCharacters, '\0');
            }
        });

        signin.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}
