package com.example.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.util.InputValidation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AddTLActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    private static final String TAG = "AddTLActivity";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    private Button Submit;
    private ImageButton DOB, JoiningDate;
    private TextView DOBtext, JoiningDateText;
    private EditText FirstName, Lastname, Email, Password, ContactNo, EmpCode, PancardNo, AadharNo;
    private RadioGroup Gender;
    private RadioButton checkedRadioButton;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

    Boolean JoindateButtonClick = false;
    Boolean DOBdateButtonClick = false;
    private String joinDateString = "", DOBDateString = "", gender = "", TLID = "";
    TextView AdminName;
    boolean first = true;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tl);


        DOBtext = (TextView) findViewById(R.id.DOBtext);
        JoiningDateText = (TextView) findViewById(R.id.JoiningDateText);
        DOB = (ImageButton) findViewById(R.id.DOB);
        JoiningDate = (ImageButton) findViewById(R.id.JoiningDate);
        Submit = (Button) findViewById(R.id.submit);
        FirstName = (EditText) findViewById(R.id.FIRST_NAME);
        FirstName.setOnFocusChangeListener(this);
        FirstName.setFilters(InputValidation.inputFilterAllowAlphaCaps);
        Lastname = (EditText) findViewById(R.id.LAST_NAME);
        Lastname.setOnFocusChangeListener(this);
        Lastname.setFilters(InputValidation.inputFilterAllowAlphaCaps);
        Email = (EditText) findViewById(R.id.EMAIL);
        Email.setOnFocusChangeListener(this);
        Password = (EditText) findViewById(R.id.PASSWORD);
        Password.setOnFocusChangeListener(this);
        ContactNo = (EditText) findViewById(R.id.CONTACTNO);
        ContactNo.setOnFocusChangeListener(this);
        Gender = (RadioGroup) findViewById(R.id.radioGroup);
        EmpCode = (EditText) findViewById(R.id.EMPCODE);
        EmpCode.setOnFocusChangeListener(this);
        EmpCode.setFilters(InputValidation.inputFilterAllowAlphaNumCaps);
        PancardNo = (EditText) findViewById(R.id.PANCARD);
        PancardNo.setOnFocusChangeListener(this);
        PancardNo.setFilters(InputValidation.inputFilterAllowAlphaNumCaps);
        AadharNo = (EditText) findViewById(R.id.AADHARCARD);
        AadharNo.setOnFocusChangeListener(this);
        AdminName = findViewById(R.id.adminText);


        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        JsonObject Json = new JsonObject();
        Json.addProperty("requiredUser", "Admin");

        new GetAdminListAsync().execute(Json);


        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.isEmpty()) {
                    Toast.makeText(AddTLActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();

                } else {
                    DOBdateButtonClick = true;
                    showDatePickerDialog(DOB_dateListener);
                }
            }
        });
        JoiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoindateButtonClick = true;
                showDatePickerDialog(JoiningDate_dateListener);
            }
        });
        Gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (ContactNo.getText().toString().isEmpty() || ContactNo.getText().toString().length() != 10) {
                    ContactNo.setError("Please enter valid Contact no.");
                    ContactNo.requestFocus();
                }

                checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {

                    if (checkedRadioButton.getText().toString().equals("Male")) {
                        gender = "M";
                    }

                    if (checkedRadioButton.getText().toString().equals("Female")) {
                        gender = "F";
                    }
                }
            }
        });


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject registerUserObject = new JsonObject();
                registerUserObject.addProperty("firstName", FirstName.getText().toString());
                registerUserObject.addProperty("lastName", Lastname.getText().toString());
                registerUserObject.addProperty("email", Email.getText().toString());
                registerUserObject.addProperty("password", Password.getText().toString());
                registerUserObject.addProperty("contactNo", ContactNo.getText().toString());
                registerUserObject.addProperty("dob", DOBDateString);
                registerUserObject.addProperty("tlId", TLID);
                registerUserObject.addProperty("role", "Manager");////DEFAULT
                registerUserObject.addProperty("isActive", "Y");//// DEFAULT
                registerUserObject.addProperty("joiningDate", joinDateString);
                registerUserObject.addProperty("gender", gender);
                registerUserObject.addProperty("empCode", EmpCode.getText().toString());
                registerUserObject.addProperty("aadharNo", AadharNo.getText().toString());
                registerUserObject.addProperty("panNo", PancardNo.getText().toString());


                if (!JoindateButtonClick || JoiningDateText.getText().toString().equals("")) {

                    Toast.makeText(AddTLActivity.this, "Please Select Joining Date.", Toast.LENGTH_SHORT).show();


                } else if (TextUtils.isEmpty(registerUserObject.get("firstName").getAsString())) {
                    FirstName.setError("Please enter valid First Name");
                    FirstName.requestFocus();



                } else if (TextUtils.isEmpty(registerUserObject.get("lastName").getAsString())) {
                    Lastname.setError("Please enter valid Last Name");
                    Lastname.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("email").getAsString()) || !registerUserObject.get("email").getAsString().contains("@")) {

                    Email.setError("Please enter valid Email");
                    Email.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("password").getAsString())) {

                    Password.setError("Please enter valid Password");
                    Password.requestFocus();

                } else if ((registerUserObject.get("password").getAsString()).length() < 8) {

                    Password.setError("Password must be greater than 8 Characters");
                    Password.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("contactNo").getAsString()) || registerUserObject.get("contactNo").getAsString().length() != 10) {

                    ContactNo.setError("Please enter valid Contact No.");
                    ContactNo.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("gender").getAsString())) {

                    Toast.makeText(AddTLActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();

                } else if (!DOBdateButtonClick || DOBtext.getText().toString().equals("")) {

                    Toast.makeText(AddTLActivity.this, "Please Select DOB.", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(registerUserObject.get("empCode").getAsString()) || (registerUserObject.get("empCode").getAsString()).length() > 10) {

                    EmpCode.setError("Please enter valid Emp Code of less than 10 characters.");
                    EmpCode.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("panNo").getAsString()) || (registerUserObject.get("panNo").getAsString()).length() != 10) {

                    PancardNo.setError("Please enter valid Pan No.");
                    PancardNo.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("aadharNo").getAsString()) || (registerUserObject.get("aadharNo").getAsString()).length() != 12) {

                    AadharNo.setError("Please enter valid Aadhar No.");
                    AadharNo.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("tlId").getAsString())) {

                    Toast.makeText(AddTLActivity.this, "Please Select Manager", Toast.LENGTH_SHORT).show();

                }


                /*  Log.d(TAG, "Request ::" + registerUserObject.toString());*/
                else {

                    new AddTLAsync().execute(registerUserObject);
                }
            }
        });
    }


    class GetAdminListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AddTLActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            getAdminList(jsonObjects[0]);

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();

        }
    }

    class AddTLAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddTLActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            registerUser(jsonObjects[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }


    public void getAdminList(JsonObject Json) {


        compositeDisposable.add(api.getSuperUserList(Json).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Successful..");
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       dataAsJsonArray = jsonElements.getAsJsonArray("data");
                                       for (JsonElement TLList : dataAsJsonArray) {
                                           JsonObject Obj = TLList.getAsJsonObject();
                                           TLID = Obj.get("user_id").getAsString();
                                           AdminName.setText(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());
                                       }
                                   } else {
                                       DialogForServiceDown(AddTLActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..");
                                   DialogForServiceDown(AddTLActivity.this).show();
                               }
                           }


                )
        );
    }


    private void registerUser(JsonObject registerUserObject) {


        compositeDisposable.add(api.registerUser(registerUserObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonObject) throws Exception {
                                   try {
                                       Log.d(TAG, "accept: response ::" + jsonObject.toString());

                                       if (jsonObject.get("success").getAsBoolean()) {
                                           Toast.makeText(AddTLActivity.this, "User Registeration Successfully Done.", Toast.LENGTH_SHORT).show();

                                           finish();
                                       } else if (!jsonObject.get("success").getAsBoolean() && jsonObject.get("errorCode").getAsString().equals("23505")) {
                                           Toast.makeText(AddTLActivity.this, "Email ID already registered.", Toast.LENGTH_SHORT).show();

                                       } else {
                                           DialogForServiceDown(AddTLActivity.this).show();
                                       }

                                   } catch (Exception e) {
                                       Log.d(TAG, "accept: error :: " + e.getMessage());
                                       DialogForServiceDown(AddTLActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   DialogForServiceDown(AddTLActivity.this).show();
                               }
                           }
                )
        );


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
            //  DOBDateString =  month + "/" + dayOfMonth + "/" + year;
            DOBDateString = year + "/" + month + "/" + dayOfMonth;
            DOBtext.setText(DOBdate);


        }
    };


    //2nd Listner
    DatePickerDialog.OnDateSetListener JoiningDate_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            String Joiningdate = dayOfMonth + "/" + month + "/" + year;
            joinDateString = year + "/" + month + "/" + dayOfMonth;//yyyy/mm/dd
            JoiningDateText.setText(Joiningdate);


        }
    };


    public AlertDialog.Builder DialogForServiceDown(Context c) {

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
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {

            case R.id.FIRST_NAME:
                if (hasFocus == true) {

                    if (!JoindateButtonClick || JoiningDateText.getText().toString().equals("")) {


                        Toast.makeText(AddTLActivity.this, "Please Select Joining Date.", Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            case R.id.LAST_NAME:

                if (hasFocus == true) {
                    if (FirstName.getText().toString().isEmpty()) {

                        FirstName.setError("Please enter  First Name");
                        FirstName.requestFocus();
                        Lastname.clearFocus();
                        Lastname.setCursorVisible(false);
                    } else {
                        FirstName.setSelection(0, 0);
                        Lastname.requestFocus();
                        Lastname.setCursorVisible(true);
                    }


                }


                break;


            case R.id.EMAIL:
                if (hasFocus == true) {
                    if (Lastname.getText().toString().isEmpty()) {
                        Lastname.setError("Please enter last Name");
                        Lastname.requestFocus();
                        Email.clearFocus();
                        Email.setCursorVisible(false);

                    } else {
                        Lastname.setSelection(0, 0);
                        Email.requestFocus();
                        Email.setCursorVisible(true);
                    }
                }

                break;


            case R.id.PASSWORD:
                if (hasFocus == true) {
                    if (Email.getText().toString().isEmpty()) {
                        Email.setError("Please enter your email");
                        Email.requestFocus();
                        Password.clearFocus();
                        Password.setCursorVisible(false);

                    } else if (!(Email.getText().toString().trim().matches(emailPattern))) {
                        Email.setError("Please enter valid email");
                        Email.requestFocus();
                        Password.clearFocus();
                        Password.setCursorVisible(false);
                    } else {
                        Email.setSelection(0, 0);
                        Password.requestFocus();
                        Password.setCursorVisible(true);

                    }


                }


                break;

            case R.id.CONTACTNO:

                if (hasFocus == true) {

                    if (Password.getText().toString().isEmpty() || Password.getText().toString().length() < 8) {
                        Password.setError("Please enter valid Password");
                        Password.requestFocus();
                        ContactNo.clearFocus();
                        ContactNo.setCursorVisible(false);

                    } else {

                        Password.setSelection(0, 0);
                        ContactNo.requestFocus();
                        ContactNo.setCursorVisible(true);
                    }
                }
                break;


            case R.id.EMPCODE:

                if (hasFocus == true) {


                    if (!DOBdateButtonClick || DOBtext.getText().toString().equals("")) {

                        Toast.makeText(AddTLActivity.this, "Please Select DOB.", Toast.LENGTH_SHORT).show();

                    }

                }
                break;

            case R.id.PANCARD:

                if (hasFocus == true) {

                    if (EmpCode.getText().toString().isEmpty() || !(EmpCode.getText().toString().contains("UCS00"))) {

                        EmpCode.setError("Please enter valid Emp code starting from UCS00");
                        EmpCode.requestFocus();
                        PancardNo.clearFocus();
                        PancardNo.setCursorVisible(false);
                    } else {

                        EmpCode.setSelection(0, 0);
                        PancardNo.requestFocus();
                        PancardNo.setCursorVisible(true);
                    }

                }
                break;


            case R.id.AADHARCARD:
                if (hasFocus == true) {

                    if (PancardNo.getText().toString().isEmpty() || !(PancardNo.getText().toString().trim().matches(panPattern))) {
                        PancardNo.setError("Please enter valid Pan No.");
                        PancardNo.requestFocus();
                        AadharNo.clearFocus();
                        AadharNo.setCursorVisible(false);

                    } else {
                        PancardNo.setSelection(0, 0);
                        AadharNo.requestFocus();
                        AadharNo.setCursorVisible(true);

                    }

                }

                break;


        }
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
