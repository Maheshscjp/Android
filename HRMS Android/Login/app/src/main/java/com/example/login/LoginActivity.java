package com.example.login;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.util.Dailog;
import com.google.gson.JsonObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";


    Button Login;
    EditText empCode;
    EditText Password;
    TextView forgetPassword;
    //    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//    String empCodePattern= "[A-Z0-9]";
    ProgressDialog progressDialog;

    NodeJS api;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);


        if (!isConnected(LoginActivity.this)) DialogForInternet(LoginActivity.this).show();
        else {

            setContentView(R.layout.activity_login);


            if (SharedPrefManager.getInstance(this).isLoggedIn()) {
                finish();
                startActivity(new Intent(this, DashboardActivity.class));
            }
            //InIt API
            Retrofit retroFit = RetroFitClient.getInstance();
            api = retroFit.create(NodeJS.class);

            //View
            Login = (Button) findViewById(R.id.Login);
            empCode = (EditText) findViewById(R.id.Email);
            forgetPassword = findViewById(R.id.forgetPassword);
            Password = (EditText) findViewById(R.id.Password);
            forgetPassword.setOnClickListener(new  View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);


                    LoginActivity.this.startActivity(intent);

                }});


            Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus == true) {
                        if (empCode.getText().toString().isEmpty()) {
                            empCode.setError("Please enter your Employee Code");
                            empCode.requestFocus();
                            Password.clearFocus();
                            Password.setCursorVisible(false);

                        }
//                        else if (!(empCode.getText().toString().trim().matches(empCodePattern))) {
//                            empCode.setError("Please enter valid Employee Code");
//                            empCode.requestFocus();
//                            Password.clearFocus();
//                            Password.setCursorVisible(false);
//                        }
                        else {
                            empCode.setSelection(0, 0);
                            Password.requestFocus();
                            Password.setCursorVisible(true);

                        }
                    }
                }
            });

            //calling the method userLogin() for login the user
            findViewById(R.id.Login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    JsonObject Jsonobject = new JsonObject();
                    Jsonobject.addProperty("empCode", empCode.getText().toString());
                    Jsonobject.addProperty("password", Password.getText().toString());
                    if (TextUtils.isEmpty(Jsonobject.get("empCode").getAsString())) {
                        empCode.setError("Please enter your email");
                        empCode.requestFocus();

                    } else if (TextUtils.isEmpty(Jsonobject.get("password").getAsString())) {
                        Password.setError("Please enter your password");
                        Password.requestFocus();

                    } else {
                        new LoginCheck().execute(Jsonobject);
                    }

                }
            });


        }
    }

    class LoginCheck extends AsyncTask<JsonObject, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait",
                    "Wait for moments");

        }

        @Override
        protected String doInBackground(JsonObject... params) {

            userLogin(params[0]);


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();

        }


    }


    private void userLogin(JsonObject jsonObject) {

        compositeDisposable.add(api.loginUser(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(JsonObject object) throws Exception {
                        try {

                            Log.d(TAG, "accept: " + object.toString());

                            if (object.get("success").getAsBoolean()) {


                                User user = new User(

                                        object.getAsJsonObject("data").get("user_id").getAsString(),
                                        object.getAsJsonObject("data").get("firstname").getAsString(),
                                        object.getAsJsonObject("data").get("lastname").getAsString(),
                                        object.getAsJsonObject("data").get("email").getAsString(),
                                        object.getAsJsonObject("data").get("password").getAsString(),
                                        object.getAsJsonObject("data").get("role").getAsString(),
                                        object.getAsJsonObject("data").get("tl_id").getAsString(),
                                        object.getAsJsonObject("data").get("tl_name").getAsString(),
                                        object.getAsJsonObject("data").get("tl_email").getAsString(),
                                        object.getAsJsonObject("data").get("emp_code").getAsString(),
                                        object.getAsJsonObject("data").get("warehouse_code").getAsString()
                                );

                                Log.d("Looged in user role ::" ,user.getRole());

                                if (object.getAsJsonObject("data").get("role").getAsString().equals("Admin") || object.getAsJsonObject("data").get("role").getAsString().equals("Manager") || object.getAsJsonObject("data").get("role").getAsString().equals("WAD") || object.getAsJsonObject("data").get("role").getAsString().equals("Employee")) {
                                    Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);

                                    finish();
                                    LoginActivity.this.startActivity(intent);
                                }
//
//                                else if (object.getAsJsonObject("data").get("role").getAsString().equals("Employee")) {
//                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
//                                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//
//                                    finish();
//                                    LoginActivity.this.startActivity(intent);
//                                }
//
//                                else {
//                                    if (object.getAsJsonObject("data").get("role").getAsString().equals("Manager")) {
//                                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
//                                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
//
//                                        finish();
//                                        LoginActivity.this.startActivity(intent);
//                                    }
//                                }


                                else{
                                    DialogForServiceDown(LoginActivity.this);
                                }
                            }


                            else if (!object.get("success").getAsBoolean() && object.get("message").getAsString().equals("failed")){


                                Password.setText("");
                                Password.requestFocus();
                                Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();

                            }
                            else if (!object.get("success").getAsBoolean() && object.get("message").getAsString().equals("email id not registered.")) {
                                empCode.setText("");
                                empCode.requestFocus();
                                Password.setText("");
                                Toast.makeText(LoginActivity.this, "Email id is not registered.", Toast.LENGTH_SHORT).show();

                            }
                            else if (!object.get("success").getAsBoolean() && object.get("message").getAsString().equals("error while fetching data")){
                                empCode.setText("");
                                empCode.requestFocus();
                                Password.setText("");
                                Toast.makeText(LoginActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Dailog.DialogForServiceDown(LoginActivity.this).show();

                            }

                        } catch (Exception e) {
                            Log.d(TAG, "accept: " + e.getMessage());
                        }
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        DialogForServiceDown(LoginActivity.this).show();

                    }
                })

        );


    }


    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder DialogForInternet(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection !");
        builder.setMessage("You need to have mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }


    public  AlertDialog.Builder DialogForServiceDown(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Unable to connect service :");
        builder.setMessage("Check internet connectivity or try again later");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


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

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}