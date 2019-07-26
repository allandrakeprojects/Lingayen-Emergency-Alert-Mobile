package com.example.user.appalert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseKo extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "appalert.db";
    public static final String TABLE_NAME1="user_info";
    public static final String TABLE_NAME2="contact_info";

    public DataBaseKo(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME1 +" (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age TEXT, address TEXT)");
        db.execSQL("create table " + TABLE_NAME2 +" (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, number TEXT, status TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME2);
        onCreate(db);
    }

    //USER START
    public boolean insertUser(String name, String age, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("age",age);
        contentValues.put("address",address);
        long result = db.insert(TABLE_NAME1,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public boolean updateUser(String id, String name, String age, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("age",age);
        contentValues.put("address",address);
        contentValues.put("id",id);
        db.update(TABLE_NAME1, contentValues, "id = ?",new String[] { id });
        return true;
    }
    public Cursor getUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME1,null);
        return res;
    }
    //USER END

    //CONTACT START
    public boolean insertContact(String name, String number, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("number",number);
        contentValues.put("status",status);
        long result = db.insert(TABLE_NAME2,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public boolean updateContact(String id, String name, String number, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("number",number);
        contentValues.put("status",status);
        contentValues.put("id",id);
        db.update(TABLE_NAME2, contentValues, "id = ?",new String[] { id });
        return true;
    }
    public Cursor getContact() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME2,null);
        return res;
    }
    //CONTACT END



}
