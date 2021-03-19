package com.example.login;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MahindraL";

    // Table Names
    private static final String TABLE_USERLOGIN = "user_login";
    private static final String TABLE_TASK = "user_task";


    // TABLE_USERLOGIN Table - column nmaes
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // TABLE_TASK Table - column nmaes
    private static final String KEY_ID = "task_id";
    private static final String KEY_NAME = "task_name";


    // Table Create Statements
    // TABLE_USERLOGIN table create statement
    private static final String CREATE_TABLE_TODO = "CREATE TABLE "
            + TABLE_USERLOGIN + "(" + KEY_USERNAME + " TEXT," + KEY_PASSWORD
            + " TEXT" + ")";
    // TABLE_TASK table create statement
    private static final String CREATE_TABLE_TASK = "CREATE TABLE " + TABLE_TASK + "(" + KEY_ID + " INTEGER, " + KEY_NAME + " TEXT " + ")";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables

        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(CREATE_TABLE_TASK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERLOGIN);


        // create new tables
        onCreate(db);
    }

    public void addData() {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", "suyog"); //
        values.put("password", "suyog"); //


        ContentValues task_values = new ContentValues();
        task_values.put("task_id", 1); //
        task_values.put("task_name", "abc"); //


        // Inserting Row
        db.insert(TABLE_USERLOGIN, null, values);
        //2nd argument is String containing nullColumnHack
        db.insert(TABLE_TASK, null, task_values);
        db.close(); // Closing database connection
    }


    public boolean checkUserExist(String username, String password) {
        //String[] columns = {"username", "password"};
        SQLiteDatabase db = this.getReadableDatabase();

        /*String selection = "username=? and password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);*/


        String query = "SELECT * FROM " + TABLE_USERLOGIN + " WHERE username= ? and password =?";
        String[] selectionArgs = {username, password};
        Cursor cursor1 = db.rawQuery(query, selectionArgs);


        int count = cursor1.getCount();

        cursor1.close();
        close();

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }


    public ArrayList<String> GetTaskList(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> taskList = new ArrayList<>();
        String query = "SELECT task_id , task_name FROM "+ TABLE_TASK;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){

            taskList.add(cursor.getString(cursor.getColumnIndex("task_name")));

        }
        return  taskList;
    }


}