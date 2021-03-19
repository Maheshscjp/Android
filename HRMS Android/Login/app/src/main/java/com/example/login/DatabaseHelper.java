package com.example.login;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NewDB";
    private static final int DATABASE_VERSION = 1;
    //private final Context context;
    SQLiteDatabase db;

    private static final String DATABASE_PATH = "/data/data/com.example.login/databases/";
    private final String USER_TABLE = "user_login";
    public static final String USERNAME="username";
    public static final String PASSWORD= "password";
      String CREATE_CONTACTS_TABLE = "CREATE TABLE " + USER_TABLE + "("
            + USERNAME + " TEXT,"
            + USERNAME + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
       // this.context = context;
        //createDb();


        Log.d("Database Connection ", "DatabaseHelper: DB connection Successful");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.d("Database Connection ", "DatabaseHelper: Table Created  Successful");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

/*    public void createDb(){
        boolean dbExist = checkDbExist();

        if(!dbExist){
            this.getReadableDatabase();
            copyDatabase();
        }
    }


    private boolean checkDbExist(){
        SQLiteDatabase sqLiteDatabase = null;

        try{
            String path = DATABASE_PATH + DATABASE_NAME;
            sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception ex){
        }

        if(sqLiteDatabase != null){

            sqLiteDatabase.close();
            return true;
        }

        return false;
    }

    private void copyDatabase(){
        try {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME);

            String outFileName = DATABASE_PATH + DATABASE_NAME;

            OutputStream outputStream = new FileOutputStream(outFileName);

            byte[] b = new byte[1024];
            int length;

            while ((length = inputStream.read(b)) > 0){
                outputStream.write(b, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private SQLiteDatabase openDatabase(){
        String path = DATABASE_PATH + DATABASE_NAME;
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        return db;
    }

    public void close(){
        if(db != null){
            db.close();
        }
    }*/

    public boolean checkUserExist(String username, String password){
        String[] columns = {"username","password"};
        db = this.getReadableDatabase();

        /*String selection = "username=? and password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(USER_TABLE, columns, selection, selectionArgs, null, null, null);*/


        String query = "SELECT * FROM " + USER_TABLE + " WHERE username= ? and passsword =?";
        String[] selectionArgs = {username, password};
        Cursor cursor1 = db.rawQuery(query, selectionArgs);



        int count = cursor1.getCount();

        cursor1.close();
        close();

        if(count > 0){
            return true;
        } else {
            return false;
        }
    }


}