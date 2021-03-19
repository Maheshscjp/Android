package com.example.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.util.Dailog;
import com.google.gson.JsonObject;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    ConstraintLayout userdatalayout;
    ConstraintLayout otplayout;
    ConstraintLayout passwordlayout;
    EditText email, otp, newPassword, confirmNewPassword;
    ImageButton dateOfBirth;
    TextView dateOfBirthText;
    Button submitUserData, submitOTP, submitPassword;
    String DOBDateString="";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
         userdatalayout = findViewById(R.id.userdata);
         otplayout = findViewById(R.id.otplayout);
         passwordlayout = findViewById(R.id.passwordlayout);
         passwordlayout.setVisibility(View.INVISIBLE);
        otplayout.setVisibility(View.INVISIBLE);


        email = findViewById(R.id.email);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        dateOfBirth.setOnClickListener(this);
        dateOfBirthText = findViewById(R.id.dateOfBirthText);
        submitUserData = findViewById(R.id.submitUserData);
        submitUserData.setOnClickListener(this);
        otp = findViewById(R.id.otp);
        submitOTP = findViewById(R.id.submitOTP);
        submitOTP.setOnClickListener(this);
        newPassword = findViewById(R.id.newPassword);
        confirmNewPassword = findViewById(R.id.confirmNewPassword);
        confirmNewPassword.setOnFocusChangeListener(this);
        submitPassword = findViewById(R.id.submitPassword);
        submitPassword.setOnClickListener(this);

        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.dateOfBirth:
                if (email.getText().toString().equals("") || !(email.getText().toString().trim().matches(emailPattern))) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter valid Email-Id.", Toast.LENGTH_SHORT).show();

                } else {

                    showDatePickerDialog(DOB_dateListener);

                }

                break;

            case R.id.submitUserData:

                if (email.getText().toString().equals("") || !(email.getText().toString().trim().matches(emailPattern))) {
                    email.setError("Please enter email id.");
                    email.requestFocus();

                }
                else if (DOBDateString.equals("")){
                    Toast.makeText(ForgetPasswordActivity.this, "Please select DOB.", Toast.LENGTH_SHORT).show();

                }
                else{
                    JsonObject verifyUser = new JsonObject();
                    verifyUser.addProperty("email", email.getText().toString());
                    verifyUser.addProperty("dob",DOBDateString);
                    new VerifyUserAsync().execute(verifyUser);


                }


                break;

            case R.id.submitOTP:
                if (otp.getText().toString().equals("") || otp.getText().toString().length() != 6 ) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter valid OTP.", Toast.LENGTH_SHORT).show();

                }
                else {
                    JsonObject verifyOTP = new JsonObject();
                    verifyOTP.addProperty("email", email.getText().toString());
                    verifyOTP.addProperty("user_otp",otp.getText().toString());

                    new VerifyOTPAsync().execute(verifyOTP);

                }

                break;

            case R.id.submitPassword:
                if(newPassword.getText().toString().equals("")){


                    newPassword.setError("Please enter password.");
                    newPassword.requestFocus();

                }
                else if (confirmNewPassword.getText().toString().equals("")){
                    confirmNewPassword.setError("Please confirm password.");
                    confirmNewPassword.requestFocus();
                }

                else if (!confirmNewPassword.getText().toString().equals(newPassword.getText().toString()))
                {
                    confirmNewPassword.setError("Please confirm password.");
                    confirmNewPassword.requestFocus();
                }

                else{
                    JsonObject updatePass = new JsonObject();
                    updatePass.addProperty("email", email.getText().toString());
                    updatePass.addProperty("password",confirmNewPassword.getText().toString());

                    new UpdatePassAsync().execute(updatePass);
                }

                break;



            default:
                break;
        }
    }



    public void showDatePickerDialog(DatePickerDialog.OnDateSetListener dateSetListener) {


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }


    //1st Listner
    DatePickerDialog.OnDateSetListener DOB_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            String DOBdate = dayOfMonth + "/" + month + "/" + year;

            DOBDateString = year + "/" + month + "/" + dayOfMonth;
            dateOfBirthText.setText(DOBdate);
            email.setSelection(0, 0);


        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {

            case R.id.confirmNewPassword:
                if (hasFocus == true) {
                    if(newPassword.getText().toString().equals("")){


                        newPassword.setError("Please enter password.");
                        newPassword.requestFocus();

                    }
                }
                break;
            default:
                break;

                }
    }


    class VerifyUserAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ForgetPasswordActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            verifyUser(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    class VerifyOTPAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ForgetPasswordActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            verifyOTP(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }


    class UpdatePassAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ForgetPasswordActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

           updatePassword(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

   public void verifyUser(JsonObject verifyuser){
       compositeDisposable.add(api.verifyUser(verifyuser).subscribeOn(
               Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Consumer<JsonObject>() {
                              @Override
                              public void accept(JsonObject jsonObject) throws Exception {
                                  try {

                                      if (jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("success")) {

                                          DialogFoOTP(ForgetPasswordActivity.this).show();


                                      } else if (!jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("failed")) {

                                          Toast.makeText(ForgetPasswordActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();

                                      }
                                      else if (!jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("Please Check Email And/OR DOB.")) {

                                          Toast.makeText(ForgetPasswordActivity.this, "Please Check Email And/OR DOB.", Toast.LENGTH_SHORT).show();

                                      }

                                      else if (!jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("OTP is not generated.")) {

                                          Toast.makeText(ForgetPasswordActivity.this, "OTP is not generated. Please try again later.", Toast.LENGTH_SHORT).show();

                                      }

                                      else {
                                          Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                                      }

                                  } catch (Exception e) {
                                      Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                                  }
                              }
                          }, new Consumer<Throwable>() {
                              @Override
                              public void accept(Throwable throwable) throws Exception {
                                  Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                              }
                          }
               )
       );
   }



    public void verifyOTP(JsonObject verifyotp){
        compositeDisposable.add(api.verifyOTP(verifyotp).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonObject) throws Exception {
                                   try {

                                       if (jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("success")) {

                                           otplayout.setVisibility(View.INVISIBLE);
                                           userdatalayout.setVisibility(View.INVISIBLE);
                                           passwordlayout.setVisibility(View.VISIBLE);


                                       } else if (!jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("failed")) {

                                           Toast.makeText(ForgetPasswordActivity.this, "OTP has not match.", Toast.LENGTH_SHORT).show();

                                       }

                                       else {
                                           Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                                       }

                                   } catch (Exception e) {
                                       Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                               }
                           }
                )
        );
    }



    public void updatePassword(JsonObject updatePass){
        compositeDisposable.add(api.updatePassword(updatePass).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonObject) throws Exception {
                                   try {

                                       if (jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("success")) {

                                           Toast.makeText(ForgetPasswordActivity.this, "Password changed.", Toast.LENGTH_SHORT).show();

                                           Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                          finish();
                                           startActivity(intent);



                                       } else if (!jsonObject.get("success").getAsBoolean() && jsonObject.get("message").getAsString().equals("failed")) {

                                           Toast.makeText(ForgetPasswordActivity.this, "Password not changed. Please try again later.", Toast.LENGTH_SHORT).show();

                                       }

                                       else {
                                           Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                                       }

                                   } catch (Exception e) {
                                       Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Dailog.DialogForServiceDown(ForgetPasswordActivity.this).show();
                               }
                           }
                )
        );
    }


    public  AlertDialog.Builder DialogFoOTP(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Forget Password :");
        builder.setMessage("Your OTP for forget password has been sent to your email id. ");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


otplayout.setVisibility(View.VISIBLE);
userdatalayout.setVisibility(View.INVISIBLE);
passwordlayout.setVisibility(View.INVISIBLE);

            }
        });

        return builder;
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
