package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "loginPreference";

    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PASSWORD = "keypassword";
    private static final String KEY_FIRSTNAME = "keyfirstname";
    private static final String KEY_LASTNAME = "keylastname";
    private static final String KEY_ROLE = "keyrole";
    private static final String KEY_USER_ID = "keyuserid";
    private static final String KEY_DOB = "keydob";
    private static final String KEY_TL_ID = "keytlid";
    private static final String KEY_TL_NAME = "keytlname";
    private static final String KEY_TL_EMAIL = "keytlemail";
    private static final String KEY_CREATED_ON = "keycretaedon";
    private static final String KEY_UPDATED_ON = "keyupdatedon";
    private static final String KEY_IS_ACTIVE = "keyisactive";
    private static final String KEY_JOINING_DATE = "keyjoiningdate";
    private static final String KEY_GENDER = "keygender";
    private static final String KEY_CONTACTNO = "keycontactno";
    private static final String KEY_EMP_CODE = "keyempcode";
    private static final String KEY_AADHAR_NO = "keyaadharno";
    private static final String KEY_PAN_NO = "keypanno";
    private static final String KEY_WAREHOUSE = "keywarehouse";


    private static SharedPrefManager mInstance;
    private static Context ctx;

    private SharedPrefManager(Context context) {
        ctx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }


    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FIRSTNAME, user.getFirstname());
        editor.putString(KEY_LASTNAME, user.getLastname());
        editor.putString(KEY_EMAIL, user.getEmail());

        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_USER_ID, user.getUserid());


        editor.putString(KEY_TL_ID, user.getTlid());
        editor.putString(KEY_TL_NAME, user.getTlname());
        editor.putString(KEY_TL_EMAIL, user.getTlEmailId());
        editor.putString(KEY_EMP_CODE, user.getEmpcode());
        editor.putString(KEY_WAREHOUSE, user.getWarehouse());


        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_USER_ID, null),
                sharedPreferences.getString(KEY_FIRSTNAME, null),
                sharedPreferences.getString(KEY_LASTNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PASSWORD, null),
                sharedPreferences.getString(KEY_ROLE, null),
                sharedPreferences.getString(KEY_TL_ID, null),
                sharedPreferences.getString(KEY_TL_NAME, null),
                sharedPreferences.getString(KEY_TL_EMAIL, null),
                sharedPreferences.getString(KEY_EMP_CODE, null),
                sharedPreferences.getString(KEY_WAREHOUSE, null)


        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        ctx.startActivity(new Intent(ctx, LoginActivity.class));
    }

    public void firstTimeAsking(String permission, boolean isFirstTime) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(permission, isFirstTime);
        editor.apply();

    }
    public boolean isFirstTimeAsking(String permission) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(permission, true);
    }


}