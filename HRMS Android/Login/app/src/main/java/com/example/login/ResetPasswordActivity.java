package com.example.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity";

    public SharedPreferences prefs;
    public static final String TEXT_VALUE_KEY="nothing";
    public static String Password;

    EditText currentpassword,newpassword,confirmpassword;
    ProgressDialog progressDialog;
    CompositeDisposable compositeDisposable;
    User LoggedInUser;
    NodeJS api;
    private Button Submit;
    //private String curPassword, nwPassword, confPassword;

    //String PasswordPattern="(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])";
    // "(?=.*?[#?!@$%^&*-]).{8,}$";
    String PasswordPattern="[a-zA-Z0-9#?!@$%^&*-]+";

    public static String  getPassword()
    {
        return Password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();


        currentpassword = (EditText) findViewById(R.id.CurrentPassword);
        newpassword = (EditText) findViewById(R.id.NewPassword);
        newpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)  {
                if (hasFocus == true) {
                    if (currentpassword.getText().toString().isEmpty()) {
                        currentpassword.setError("Enter current password.");
                        currentpassword.requestFocus();
                        newpassword.clearFocus();
                        newpassword.setCursorVisible(false);
                    }
                    else {
                        // currentpassword.setSelected(true);
                        newpassword.requestFocus();
                        newpassword.setCursorVisible(true);

                    }
                }
            }
        });
        confirmpassword = (EditText) findViewById(R.id.ConfirmPassword);
        confirmpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)  {
                if (hasFocus==true)  {
                    if ( newpassword.getText().toString().isEmpty()) {
                        newpassword.setError("Enter new password.");
                        newpassword.requestFocus();
                        confirmpassword.clearFocus();
                        confirmpassword.setCursorVisible(false);
                    }else if (!( newpassword.getText().toString().trim().matches(PasswordPattern))) {
                        newpassword.setError("Please enter valid new password");
                        newpassword.requestFocus();
                        confirmpassword.clearFocus();
                        confirmpassword.setCursorVisible(false);
//                    }else if ((newpassword.getText().toString().equals(currentpassword.getText().toString()))) {
//                        newpassword.setError("NewPassword and CurrentPassword should be match");
//                        newpassword.requestFocus();
//                        confirmpassword.clearFocus();
//                        confirmpassword.setCursorVisible(false);
                    }else if((newpassword.length()<6||newpassword.length()>15)){
                        newpassword.setError("Password length should be min.6 or max.15");
                        newpassword.requestFocus();
                        confirmpassword.clearFocus();
                        confirmpassword.setCursorVisible(false);
                    }
                    else {
                        //currentpassword.setSelectio
                        confirmpassword.requestFocus();
                        confirmpassword.setCursorVisible(true);
                    }
                }
//                else{
//
//                    if(confirmpassword.getText().toString().isEmpty())
//                    {
//
//                    }
//
//                    if (!(confirmpassword.getText().toString().equals(newpassword.getText().toString())))
//                    {
//                        confirmpassword.setError("Please confirm password.");
//                        confirmpassword.requestFocus();
//                        confirmpassword.setCursorVisible(true);
//                        //confirmpassword.clearFocus();
//                        newpassword.clearFocus();
//                        newpassword.setCursorVisible(false);
//                        currentpassword.clearFocus();
//                        currentpassword.setCursorVisible(false);
//                    }
//                    else
//                    {
//                        newpassword.clearFocus();
//                        newpassword.setCursorVisible(true);
//                    }
//                }
            }
        });

        Submit = (Button) findViewById(R.id.resetButton);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JsonObject Jsonobject = new JsonObject();
                Jsonobject.addProperty("userPwd", currentpassword.getText().toString());
                Jsonobject.addProperty("userNewPwd", newpassword.getText().toString());
                Jsonobject.addProperty("userId", LoggedInUser.getUserid());
                ;
                ////Jsonobject.addProperty("confirmpassword", confirmpassword.getText().toString());

                if (TextUtils.isEmpty(Jsonobject.get("userPwd").getAsString())) {
                    currentpassword.setError("Enter current password.");
                    currentpassword.requestFocus();

                } else if (TextUtils.isEmpty(Jsonobject.get("userNewPwd").getAsString())) {
                    newpassword.setError("Enter new password.");
                    newpassword.requestFocus();
                } else if (TextUtils.isEmpty(confirmpassword.getText().toString())) {
                    confirmpassword.setError("Please confirm password.");
                    confirmpassword.requestFocus();
                }else if (!(confirmpassword.getText().toString().equals(newpassword.getText().toString()))) {
                    confirmpassword.setError("New password and confirm password has not match.");
                    confirmpassword.requestFocus();
//                }else if ((newpassword.getText().toString().equals(currentpassword.getText().toString()))) {
//                   newpassword.setError("Please enter your newpassword");
//                    newpassword.requestFocus();
                }
                else {
                    new PasswordCheck().execute(Jsonobject);
                }
            }


        });
    }
    class PasswordCheck extends AsyncTask<JsonObject, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ResetPasswordActivity.this,
                    "Please Wait",
                    "Wait for moments");

        }
        protected String doInBackground(JsonObject... params) {

            updatepassword(params[0]);


            return null;
        }
        protected void onPostExecute(String result) {

            progressDialog.dismiss();

        }
    }

    //api = retroFit.create(NodeJS.class);
    private void updatepassword(final JsonObject jsonobject)
    {
        compositeDisposable.add(api.resetpassword(jsonobject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       Toast.makeText(ResetPasswordActivity.this, "Reset password successfully done.", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(ResetPasswordActivity.this, DashboardActivity.class);
                                       finish();
                                       startActivity(intent);
                                   }
                                   else if(!jsonElements.get("success").getAsBoolean() && ( jsonElements.get("message").getAsString().equals("error while updating data")  || jsonElements.get("message").getAsString().equals("error while fetching data") ))
                                   {
                                       Toast.makeText(ResetPasswordActivity.this,"Please try again later.",Toast.LENGTH_SHORT).show();
                                   }
                                   else if (!jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("invalid user id") ){
                                       Toast.makeText(ResetPasswordActivity.this,"Invalid user.",Toast.LENGTH_SHORT).show();
                                   }

                                   else if (!jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("invalid current password") ){
                                       Toast.makeText(ResetPasswordActivity.this,"Invalid current password.",Toast.LENGTH_SHORT).show();
                                       currentpassword.setError("Enter correct current password.");
                                       currentpassword.requestFocus();


                                   }
                                   else {
                                       //Toast.makeText(ResetPasswordActivity.this,"enter the current ID",Toast.LENGTH_SHORT).show();
                                       DialogForServiceDown(ResetPasswordActivity.this).show();
                                   }

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(ResetPasswordActivity.this).show();
                               }
                           }
                )
        );
    }
    private AlertDialog.Builder DialogForServiceDown(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Unable to connect service :");
        builder.setMessage("Check internet connectivity or try again later");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        return builder;
    }
}
