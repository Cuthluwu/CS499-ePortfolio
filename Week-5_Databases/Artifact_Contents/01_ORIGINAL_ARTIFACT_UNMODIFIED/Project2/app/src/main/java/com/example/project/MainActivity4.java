package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity4 extends AppCompatActivity {

    EditText username, password, repassword;
    Button signup, signin;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText);
        repassword = (EditText) findViewById(R.id.editText2);
        signup = (Button) findViewById(R.id.button);
        signin = (Button) findViewById(R.id.button1);
        DB = new DBHelper(this);

        signup.setOnClickListener(view -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();
            String repass = repassword.getText().toString();

            if(user.equals("")||pass.equals("")||repass.equals(""))
                Toast.makeText(MainActivity4.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
            else{
                if(pass.equals(repass)){
                    Boolean checkuser = DB.checkusername(user);
                    if(!checkuser){
                        Boolean insert = DB.insertData(user, pass);
                        if(insert){
                            Toast.makeText(MainActivity4.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity4.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(MainActivity4.this, "User already exists! please sign in", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity4.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                }
            } });

        signin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(intent);
        });
    }
}