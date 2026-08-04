package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    Button login, signup;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.editTextTextPersonName);
        password = (EditText) findViewById(R.id.editText);
        signup = (Button) findViewById(R.id.button2);
        login = (Button) findViewById(R.id.button);
        DB = new DBHelper(this);

        login.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if(user.equals("") || pass.equals(""))
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            else{
                Boolean checkuserpass = DB.checkusernamepassword(user, pass);
                if (checkuserpass){
                    Toast.makeText(MainActivity.this, "Successful Signin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Invalid Login Information", Toast.LENGTH_SHORT).show();
                }
            }

        });
        signup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
            startActivity(intent);
        });
    }

}