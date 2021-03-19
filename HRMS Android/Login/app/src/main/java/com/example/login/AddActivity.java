package com.example.login;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.util.Dailog;
import com.example.login.util.InputValidation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AddActivity extends AppCompatActivity implements View.OnFocusChangeListener {
    private static final String TAG = "AddActivity";


    NodeJS api;
    CompositeDisposable compositeDisposable;
    private Button Submit;
    private ImageButton DOB, JoiningDate;
    private TextView DOBtext, JoiningDateText;
    private EditText FirstName, Lastname, Email, Password, ContactNo, EmpCode, PancardNo, AadharNo, selectWarehouse;
    private RadioGroup Gender;
    private RadioButton checkedRadioButton;
    ScrollView scrollView;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

    Boolean JoindateButtonClick = false;
    Boolean DOBdateButtonClick = false;
    private String joinDateString = "", DOBDateString = "", gender = "", TLID = "", userType = "", warehouse = "", shift="";
    private Spinner superUserSpinner, userTypeSpinner, selectShift;
    boolean first = true;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;
    JsonObject managerAdminJsonData;
    User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        addItemOnUserTypeSpinner();
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.INVISIBLE);
        DOBtext = (TextView) findViewById(R.id.DOBtext);
        JoiningDateText = (TextView) findViewById(R.id.JoiningDateText);
        DOB = (ImageButton) findViewById(R.id.DOB);
        JoiningDate = (ImageButton) findViewById(R.id.JoiningDate);
        Submit = (Button) findViewById(R.id.submit);
        Submit.setOnFocusChangeListener(this);
        Submit.setVisibility(View.INVISIBLE);
        FirstName = (EditText) findViewById(R.id.FIRST_NAME);
        FirstName.setFilters(InputValidation.inputFilterAllowAlphaCaps);
        FirstName.setOnFocusChangeListener(this);
        Lastname = (EditText) findViewById(R.id.LAST_NAME);
        Lastname.setFilters(InputValidation.inputFilterAllowAlphaCaps);
        Lastname.setOnFocusChangeListener(this);
        Email = (EditText) findViewById(R.id.EMAIL);
        Email.setOnFocusChangeListener(this);
        Password = (EditText) findViewById(R.id.PASSWORD);
        Password.setOnFocusChangeListener(this);
        ContactNo = (EditText) findViewById(R.id.CONTACTNO);
        ContactNo.setOnFocusChangeListener(this);
        Gender = (RadioGroup) findViewById(R.id.radioGroup);
        Gender.setOnFocusChangeListener(this);
        EmpCode = (EditText) findViewById(R.id.EMPCODE);
        EmpCode.setFilters(InputValidation.inputFilterAllowAlphaNumCaps);
        EmpCode.setOnFocusChangeListener(this);
        PancardNo = (EditText) findViewById(R.id.PANCARD);
        PancardNo.setFilters(InputValidation.inputFilterAllowAlphaNumCaps);
        PancardNo.setOnFocusChangeListener(this);
        AadharNo = (EditText) findViewById(R.id.AADHARCARD);
        AadharNo.setOnFocusChangeListener(this);
        superUserSpinner = findViewById(R.id.selectTL);
        selectShift = (Spinner) findViewById(R.id.selectedShift);
        selectWarehouse = (EditText) findViewById(R.id.selectedWarehouse);

        loggedInUser = SharedPrefManager.getInstance(AddActivity.this).getUser();

        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        JsonObject Json = new JsonObject();
        Json.addProperty("requiredUser", "Manager','Admin','WAD");
        new GetManagerAdminListAsync().execute(Json);

        selectWarehouse.setText(loggedInUser.getWarehouse());
        warehouse=selectWarehouse.getText().toString();
        getWorkingShift();

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.isEmpty()) {
                    Toast.makeText(AddActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();

                } else {
                    DOBdateButtonClick = true;
                    showDatePickerDialog(DOB_dateListener);

                }
            }
        });
        JoiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.isEmpty()) {
                    Toast.makeText(AddActivity.this, "Please Select User Type.", Toast.LENGTH_SHORT).show();

                } else {
                    JoindateButtonClick = true;
                    showDatePickerDialog(JoiningDate_dateListener);
                }
            }
        });
        Gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (ContactNo.getText().toString().isEmpty() || ContactNo.getText().toString().length() != 10) {
                    ContactNo.setError("Please enter valid Contact no.");
                    ContactNo.requestFocus();
                }
                ContactNo.setSelection(0, 0);
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
                    if (checkedRadioButton.getText().toString().equals("Other")) {
                        gender = "O";
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
                registerUserObject.addProperty("role", userType);
                registerUserObject.addProperty("isActive", "Y");//// DEFAULT
                registerUserObject.addProperty("joiningDate", joinDateString);
                registerUserObject.addProperty("gender", gender);
                registerUserObject.addProperty("empCode", EmpCode.getText().toString());
                registerUserObject.addProperty("aadharNo", AadharNo.getText().toString());
                registerUserObject.addProperty("panNo", PancardNo.getText().toString());
                registerUserObject.addProperty("warehouse", warehouse);
                registerUserObject.addProperty("shift", shift);

                Log.d("register user Data ::" , registerUserObject.toString());
                if (!JoindateButtonClick || JoiningDateText.getText().toString().equals("")) {

                    Toast.makeText(AddActivity.this, "Please Select Joining Date.", Toast.LENGTH_SHORT).show();


                } else if (TextUtils.isEmpty(registerUserObject.get("firstName").getAsString())) {
                    FirstName.setError("Please enter  First Name");
                    FirstName.requestFocus();


                } else if (TextUtils.isEmpty(registerUserObject.get("lastName").getAsString())) {
                    Lastname.setError("Please enter  Last Name");
                    Lastname.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("email").getAsString()) || !registerUserObject.get("email").getAsString().contains("@")) {

                    Email.setError("Please enter valid Email");
                    Email.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("password").getAsString()) || (registerUserObject.get("password").getAsString()).length() < 8) {

                    Password.setError("Please enter valid Password");
                    Password.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("contactNo").getAsString()) || registerUserObject.get("contactNo").getAsString().length() != 10) {

                    ContactNo.setError("Please enter valid contactNo");
                    ContactNo.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("gender").getAsString())) {

                    Toast.makeText(AddActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();

                } else if (!DOBdateButtonClick || DOBtext.getText().toString().equals("")) {

                    Toast.makeText(AddActivity.this, "Please Select DOB.", Toast.LENGTH_SHORT).show();

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

                    Toast.makeText(AddActivity.this, "Please Select Super User.", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(registerUserObject.get("warehouse").getAsString())) {

                    Toast.makeText(AddActivity.this, "Please Select Warehouse.", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(registerUserObject.get("shift").getAsString())) {

                    Toast.makeText(AddActivity.this, "Please Select Shift.", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(AddActivity.this, "submitting Data", Toast.LENGTH_SHORT).show();
                    new AddUserAsync().execute(registerUserObject);
                }
            }
        });
    }

    class AddUserAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddActivity.this,
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

    class GetManagerAdminListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AddActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            getManagerAdminList(jsonObjects[0]);

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();

        }
    }

    private void registerUser(JsonObject registerUserObject) {
        Log.d(TAG, "registerUser: request ::" + registerUserObject.toString());

        compositeDisposable.add(api.registerUser(registerUserObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonObject) throws Exception {
                                   try {

                                       if (jsonObject.get("success").getAsBoolean()) {
                                           Toast.makeText(AddActivity.this, "User Registered Successfully.", Toast.LENGTH_SHORT).show();

                                           finish();
                                       } else if (!jsonObject.get("success").getAsBoolean() && jsonObject.get("errorCode").getAsString().equals("23505")) {
                                           Toast.makeText(AddActivity.this, "Email ID already registered.", Toast.LENGTH_SHORT).show();

                                       } else {
                                           Dailog.DialogForServiceDown(AddActivity.this).show();
                                       }

                                   } catch (Exception e) {
                                       Dailog.DialogForServiceDown(AddActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Dailog.DialogForServiceDown(AddActivity.this).show();
                               }
                           }
                )
        );


    }

    public void getManagerAdminList(JsonObject Json) {


        compositeDisposable.add(api.getSuperUserList(Json).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {

                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       //addItemsOnSuperUserSpinner(jsonElements);
                                       managerAdminJsonData = jsonElements;
                                   } else {
                                       Dailog.DialogForServiceDown(AddActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..");
                                   Dailog.DialogForServiceDown(AddActivity.this).show();
                               }
                           }


                )
        );


    }

    public void addItemOnUserTypeSpinner() {

        userTypeSpinner = (Spinner) findViewById(R.id.userTypeSpinner);
        List<String> list = new ArrayList<String>();
        list.add("select");
        list.add("Employee");
        list.add("Supervisor");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(dataAdapter);
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) findViewById(R.id.tlname);
                if (position == 0) {
                    Toast.makeText(AddActivity.this, "Please select user type", Toast.LENGTH_SHORT).show();
                    userType = "";
                    scrollView.setVisibility(View.INVISIBLE);
                    Submit.setVisibility(View.INVISIBLE);
                } else {

                    if (parent.getItemAtPosition(position).toString().equals("Employee")) {
                        superUserSpinner.setVisibility(View.VISIBLE);
                        tv.setVisibility(View.VISIBLE);
                        Submit.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                        tv.setText("Supervisor");
                        userType = "Employee";
                        addItemsOnSuperUserSpinner(managerAdminJsonData, "Manager");
                    } else {

                        TLID = loggedInUser.getUserid();
                        superUserSpinner.setVisibility(View.GONE);
                        tv.setVisibility(View.GONE);
                        Submit.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
//                        tv.setText("WAD Name");
                        userType = "Manager";
//                        addItemsOnSuperUserSpinner(managerAdminJsonData, "WAD");
                    }
                    // userType = (parent.getItemAtPosition(position).toString().equals("Employee"))? "Employee" : "Manager";
                }
                //  }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setShiftDataToSpinner(final JsonArray shiftJsonData){

        final List<String> shiftsList = new ArrayList<>();
        shiftsList.add("Select");
        for(JsonElement data:shiftJsonData){
            shiftsList.add(data.getAsJsonObject().get("shift_name").getAsString());
        }
        ArrayAdapter<String> shiftAdaptor= new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, shiftsList);
        shiftAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectShift.setAdapter(shiftAdaptor);
        selectShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                {

                    if (position == 0) {
                        shift = "";
                    } else {
                        String userName = selectShift.getSelectedItem().toString();
                        shift = shiftJsonData.get(position-1).getAsJsonObject().get("working_shift_id").getAsString();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getWorkingShift() {
        compositeDisposable.add(api.getWorkingShiftList().subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonObject) throws Exception {
                                   try {

                                       if (jsonObject.get("success").getAsBoolean()) {
                                           if(jsonObject.get("data")!=null){
                                               setShiftDataToSpinner(jsonObject.get("data").getAsJsonArray());
                                           }
                                           else{
                                               Toast.makeText(AddActivity.this, "No Shifts Avaliable", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                       else {
                                           Dailog.DialogForServiceDown(AddActivity.this).show();
                                       }

                                   } catch (Exception e) {
                                       Dailog.DialogForServiceDown(AddActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Dailog.DialogForServiceDown(AddActivity.this).show();
                               }
                           }
                )
        );
    }

    public void addItemsOnSuperUserSpinner(JsonObject data, String role) {
        List<String> list = new ArrayList<String>();
        list.add("select");
        dataAsJsonArray = data.getAsJsonArray("data");
        for (JsonElement List : dataAsJsonArray) {
            JsonObject Obj = List.getAsJsonObject();
            if (Obj.get("role").getAsString().equals(role))
                list.add(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        superUserSpinner.setAdapter(dataAdapter);

        superUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (first) {
                    first = false;
                } else {

                    if (position == 0) {
                        Toast.makeText(AddActivity.this, "Please select super user.", Toast.LENGTH_SHORT).show();
                        TLID = "";
                    } else {
                       /* position--;
                        JsonElement data = dataAsJsonArray.get(position);
                        JsonObject dataObj = data.getAsJsonObject();

                        TLID = dataObj.get("user_id").getAsString();*/

                        String userName = superUserSpinner.getSelectedItem().toString();
                        for (JsonElement List : dataAsJsonArray) {
                            JsonObject Obj = List.getAsJsonObject();
                            if ((Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString()).equals(userName)) {
                                TLID = Obj.get("user_id").getAsString();
                                Toast.makeText(AddActivity.this, Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString(), Toast.LENGTH_SHORT).show();
                            }

                        }


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
            DOBdate = getDateInDisplayFormat(DOBdate);
            //  DOBDateString =  month + "/" + dayOfMonth + "/" + year;
            DOBDateString = year + "/" + month + "/" + dayOfMonth;
            DOBtext.setText(DOBdate);


        }
    };

    public String getDateInDisplayFormat(String dateData){

        String strDate=null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfDis = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();
        try {
            date = sdf.parse(dateData);
            strDate = sdfDis.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return strDate;
    }


    //2nd Listner
    DatePickerDialog.OnDateSetListener JoiningDate_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            String Joiningdate = dayOfMonth + "/" + month + "/" + year;
            Joiningdate = getDateInDisplayFormat(Joiningdate);
            joinDateString = year + "/" + month + "/" + dayOfMonth;//yyyy/mm/dd
            JoiningDateText.setText(Joiningdate);


        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {

            case R.id.FIRST_NAME:
                if (hasFocus == true) {

                    if (!JoindateButtonClick || JoiningDateText.getText().toString().equals("")) {


                        Toast.makeText(AddActivity.this, "Please Select Joining Date.", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(AddActivity.this, "Please Select DOB.", Toast.LENGTH_SHORT).show();

                    }

                }
                break;

            case R.id.PANCARD:

                if (hasFocus == true) {

                    if (EmpCode.getText().toString().isEmpty() || !(EmpCode.getText().toString().contains("MLL00"))) {

                        EmpCode.setError("Please enter valid Emp code starting from MLL00");
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
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return true;
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
