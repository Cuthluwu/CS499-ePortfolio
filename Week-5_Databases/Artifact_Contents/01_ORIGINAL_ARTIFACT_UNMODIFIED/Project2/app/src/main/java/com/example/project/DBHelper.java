package com.example.project;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(username TEXT primary key, password TEXT)");
        MyDB.execSQL("create Table Userdetails(name TEXT primary key, weight TEXT, date TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
        MyDB.execSQL("drop Table if exists Userdetails");
    }
    public Boolean insertuserdata(String name, String weight, String date)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("weight", weight);
        contentValues.put("date", date);
        long result=MyDB.insert("Userdetails", null, contentValues);
        return result != -1;
    }
    public Boolean updateuserdata(String name, int weight, String date)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("date", date);
        contentValues.put("weight", weight);
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from Userdetails where name = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = MyDB.update("Userdetails", contentValues, "name=?", new String[]{name});
            return result != -1;
        } else {
            return false;
        }
    }
    public Boolean deletedata (String name)
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from Userdetails where name = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = MyDB.delete("Userdetails", "name=?", new String[]{name});
            return result != -1;
        } else {
            return false;
        }
    }
    public Cursor getdata ()
    {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        return MyDB.rawQuery("Select * from Userdetails", null);
    }
    public Boolean insertData(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;
    }
    public Boolean checkusername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from users where username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }
    public Boolean checkusernamepassword(String username, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = MyDB.rawQuery("Select * from users where username = ? and password = ?", new String[] {username,password});
        return cursor.getCount() > 0;
    }
}