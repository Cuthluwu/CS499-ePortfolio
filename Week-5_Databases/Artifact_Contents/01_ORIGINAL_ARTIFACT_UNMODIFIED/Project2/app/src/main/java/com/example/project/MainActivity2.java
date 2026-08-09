package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    EditText name, date, weight;
    Button insert, update, delete, view, settings;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        {
            setContentView(R.layout.activity_main2);
            settings = findViewById(R.id.button1);
            name = findViewById(R.id.name);
            weight = findViewById(R.id.weight);
            date = findViewById(R.id.date1);
            date.setText(getString(R.string.current_date));
            insert = findViewById(R.id.btnInsert);
            update = findViewById(R.id.btnUpdate);
            delete = findViewById(R.id.btnDelete);
            view = findViewById(R.id.btnView);
            DB = new DBHelper(this);


            settings.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                startActivity(intent);
            });

            insert.setOnClickListener(v -> {
                String nameTXT = name.getText().toString();
                String weightTXT = weight.getText().toString();
                String dateTXT = date.getText().toString();

                Boolean checkinsertdata = DB.insertuserdata(nameTXT, weightTXT, dateTXT);
                if (checkinsertdata)
                    Toast.makeText(MainActivity2.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity2.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
            });

            update.setOnClickListener(v -> {
                String nameTXT = name.getText().toString();
                String weightTXT = weight.getText().toString();
                String dateTXT = date.getText().toString();

                Boolean checkupdatedata = DB.updateuserdata(nameTXT, Integer.parseInt(weightTXT), dateTXT);
                if (checkupdatedata)
                    Toast.makeText(MainActivity2.this, "Entry Updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity2.this, "Entry Not Updated", Toast.LENGTH_SHORT).show();
            });

            delete.setOnClickListener(view -> {
                String nameTXT = name.getText().toString();

                Boolean checkdeletedata = DB.deletedata(nameTXT);
                if (checkdeletedata)
                    Toast.makeText(MainActivity2.this, "Entry Deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity2.this, "Entry Not Deleted", Toast.LENGTH_SHORT).show();
            });

            view.setOnClickListener(view -> {
                Cursor res = DB.getdata();
                if (res.getCount() == 0) {
                    Toast.makeText(MainActivity2.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder buffer = new StringBuilder();
                while (res.moveToNext()) {
                    buffer.append("Name :").append(res.getString(0)).append("\n");
                    buffer.append("Weight :").append(res.getString(1)).append("\n");
                    buffer.append("Date :").append(res.getString(2)).append("\n\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
                builder.setCancelable(true);
                builder.setTitle("User Entries");
                builder.setMessage(buffer.toString());
                builder.show();
            });
        }
        }
    }