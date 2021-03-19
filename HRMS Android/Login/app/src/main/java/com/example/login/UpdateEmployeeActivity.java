package com.example.login;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.util.Dailog;
import com.example.login.util.InputValidation;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

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


public class UpdateEmployeeActivity extends AppCompatActivity implements View.OnFocusChangeListener  {
    private static final String TAG = "UpdateUserActivity";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    private Button submit_Update;
    private ImageButton DOB, JoiningDate;
    private TextView DOBtext, JoiningDateText, lblTLName;
    private EditText FirstName, Lastname, Email, Password, ContactNo, EmpCode, PancardNo, AadharNo, selectWarehouse;
    private RadioGroup Gender;
    private RadioButton checkedRadioButton;
    ScrollView scrollView;
    private CheckBox isActive;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
    private String userID;
    Boolean JoindateButtonClick = false;
    Boolean DOBdateButtonClick = false;
    private String joinDateString = "", DOBDateString = "", gender = "", TLID = "", userType = "",isActiveValue="",tl_id="", warehouse = "", shift="";

    private Spinner superUserSpinner, userTypeSpinner_update,userTypeSpineer, selectShift;
    boolean first = true;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray,dataAsJsonArraySuperUser;
    JsonObject managerAdminJsonData;
    String UserId = "";
    User LoggedInUser;
    private String Tag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_employee);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        submit_Update = findViewById(R.id.submit_Update);
        scrollView = findViewById(R.id.scrollView_Update);
        scrollView.setVisibility(View.INVISIBLE);
        userTypeSpinner_update = findViewById(R.id.userTypeSpinner);
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        JsonObject Json = new JsonObject();
        Json.addProperty("requiredUser", "Employee','Manager");
        Log.d(TAG, "accept: Requets ::" + Json.toString());
        new GetEmployeeListAsync().execute(Json);

        LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        Log.d(TAG, "accept: LoggedInUser ::" + LoggedInUser.getUserid());
        DOBtext = (TextView) findViewById(R.id.DOBtextUpdate);
        JoiningDateText = (TextView) findViewById(R.id.JoiningDateText_Update);
        DOB = (ImageButton) findViewById(R.id.DOB_Update);
        JoiningDate = (ImageButton) findViewById(R.id.JoiningDateUpdate);
        submit_Update = (Button) findViewById(R.id.submit_Update);
        submit_Update.setOnFocusChangeListener(this);
        submit_Update.setVisibility(View.INVISIBLE);
        FirstName = (EditText) findViewById(R.id.FIRST_NAME_Update);
        FirstName.setFilters(InputValidation.inputFilterAllowAlphaCaps);
        Lastname = (EditText) findViewById(R.id.LAST_NAME_Update);
        Lastname.setFilters(InputValidation.inputFilterAllowAlphaCaps);
        Email = (EditText) findViewById(R.id.EMAIL_Update);
        Email.setOnFocusChangeListener(this);
        Password = (EditText) findViewById(R.id.PASSWORD_Update);
        ContactNo = (EditText) findViewById(R.id.CONTACTNO_Update);
        EmpCode = (EditText) findViewById(R.id.EMPCODE_Update);
        EmpCode.setFilters(InputValidation.inputFilterAllowAlphaNumCaps);
        PancardNo = (EditText) findViewById(R.id.PANCARD_Update);
        PancardNo.setFilters(InputValidation.inputFilterAllowAlphaNumCaps);
        AadharNo = (EditText) findViewById(R.id.AADHARCARD_Update);
        superUserSpinner = findViewById(R.id.superUserSpinner);
        userTypeSpineer =findViewById(R.id.userTypeSpinner1);
        isActive = findViewById(R.id.checkBox_Update);
        Gender=findViewById(R.id.radioGroup);
        selectShift = (Spinner) findViewById(R.id.selectedShift);
        selectWarehouse = (EditText) findViewById(R.id.selectedWarehouse);
        lblTLName = (TextView) findViewById(R.id.tlname);


        if(isActive.isChecked()){
            isActiveValue="Y";
        }else{
            isActiveValue="N";
        }

        if(gender=="F")
        {
            findViewById(R.id.taskGroup).setSelected(true);
        }else if(gender == "M"){
            findViewById(R.id.taskIndividual).setSelected(true);
        }else if (gender == "O"){
            findViewById(R.id.radioOther).setSelected(true);
        }

        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.isEmpty()) {
                    Toast.makeText(UpdateEmployeeActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();

                } else {
                    DOBdateButtonClick = true;
                    showDatePickerDialog(DOB_dateListener);

                }
            }
        });
        JoiningDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Email.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateEmployeeActivity.this, "Please Enter Email Address.", Toast.LENGTH_SHORT).show();

                } else {
                    JoindateButtonClick = true;
                    showDatePickerDialog(JoiningDate_dateListener);
                }
            }
        });
     Gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {


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


        submit_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject registerUserObject = new JsonObject();
                registerUserObject.addProperty("firstName", FirstName.getText().toString());
                registerUserObject.addProperty("lastName", Lastname.getText().toString());
                registerUserObject.addProperty("email", Email.getText().toString());
                registerUserObject.addProperty("password", Password.getText().toString());
                registerUserObject.addProperty("contact_no", ContactNo.getText().toString());
                registerUserObject.addProperty("dob", DOBDateString);
                registerUserObject.addProperty("tl_id", TLID);
                registerUserObject.addProperty("role", userType);
                registerUserObject.addProperty("isactive",isActiveValue);//// DEFAULT
                registerUserObject.addProperty("joining_dt", joinDateString);
                registerUserObject.addProperty("gender", gender);
                registerUserObject.addProperty("emp_code", EmpCode.getText().toString());
                registerUserObject.addProperty("aadhar_no", AadharNo.getText().toString());
                registerUserObject.addProperty("pan_no", PancardNo.getText().toString());
                registerUserObject.addProperty("userId",userID);
                registerUserObject.addProperty("warehouse", warehouse);
                registerUserObject.addProperty("shift", shift);

                if(JoiningDateText.getText().toString().equals("")){
                    JoindateButtonClick = false;
                }else {
                    JoindateButtonClick = true;
                }
                if(DOBtext.getText().toString().equals("")){
                    DOBdateButtonClick = false;
                }else {
                    DOBdateButtonClick = true;
                }


                Log.d(TAG, "accept: DOBtext ::" + (JoindateButtonClick==false || JoiningDateText.getText().toString().equals("")));

                if (JoindateButtonClick == false || JoiningDateText.getText().toString().equals("")) {

                    Toast.makeText(UpdateEmployeeActivity.this, "Please Select Joining Date.", Toast.LENGTH_SHORT).show();

                }else if (TextUtils.isEmpty(registerUserObject.get("firstName").getAsString())) {
                    FirstName.setError("Please enter  First Name");
                    FirstName.requestFocus();
                } else if (TextUtils.isEmpty(registerUserObject.get("lastName").getAsString())) {
                    Lastname.setError("Please enter  Last Name");
                    Lastname.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("email").getAsString()) || !registerUserObject.get("email").getAsString().contains("@")) {

                    Email.setError("Please enter valid Email");
                    Email.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("contact_no").getAsString()) || registerUserObject.get("contact_no").getAsString().length() != 10) {

                    ContactNo.setError("Please enter valid contactNo");
                    ContactNo.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("gender").getAsString())) {

                    Toast.makeText(UpdateEmployeeActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();

                } else if (DOBdateButtonClick == false || DOBtext.getText().toString().equals("")) {

                    Toast.makeText(UpdateEmployeeActivity.this, "Please Select DOB.", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(registerUserObject.get("emp_code").getAsString()) || (registerUserObject.get("emp_code").getAsString()).length() > 10) {

                    EmpCode.setError("Please enter valid Emp Code of less than 10 characters.");
                    EmpCode.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("pan_no").getAsString()) || (registerUserObject.get("pan_no").getAsString()).length() != 10) {

                    PancardNo.setError("Please enter valid Pan No.");
                    PancardNo.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("aadhar_no").getAsString()) || (registerUserObject.get("aadhar_no").getAsString()).length() != 12) {

                    AadharNo.setError("Please enter valid Aadhar No.");
                    AadharNo.requestFocus();

                } else if (TextUtils.isEmpty(registerUserObject.get("tl_id").getAsString())) {

                Toast.makeText(UpdateEmployeeActivity.this, "Please Select Super User.", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(registerUserObject.get("role").getAsString())) {

                    Toast.makeText(UpdateEmployeeActivity.this, "Please Select User Type.", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(registerUserObject.get("warehouse").getAsString())) {

                    Toast.makeText(UpdateEmployeeActivity.this, "Please Select warehouse.", Toast.LENGTH_SHORT).show();

                }
                else if (TextUtils.isEmpty(registerUserObject.get("shift").getAsString())) {

                    Toast.makeText(UpdateEmployeeActivity.this, "Please Select Shift.", Toast.LENGTH_SHORT).show();
                }else
                {
//                    Log.d("Requestbody update :: ", registerUserObject.toString());
                    new UpdateUserAsync().execute(registerUserObject);
                }
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
                                               Toast.makeText(UpdateEmployeeActivity.this, "No Shifts Avaliable", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                       else {
                                           Dailog.DialogForServiceDown(UpdateEmployeeActivity.this).show();
                                       }

                                   } catch (Exception e) {
                                       Dailog.DialogForServiceDown(UpdateEmployeeActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Dailog.DialogForServiceDown(UpdateEmployeeActivity.this).show();
                               }
                           }
                )
        );
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
        int shiftAdaptorPosition = shiftAdaptor.getPosition(shift);
//        if(shiftAdaptorPosition!=null) {
            selectShift.setSelection(shiftAdaptorPosition);
//        }
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

    class GetManagerAdminListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(UpdateEmployeeActivity.this,
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



    class GetEmployeeListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateEmployeeActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            getEmployeeList(jsonObjects[0]);
            // getEmployeeDetails(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    class GetEmployeeDetailsAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateEmployeeActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            getEmployeeDetails(jsonObjects[0]);
            getWorkingShift();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    class UpdateUserAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateEmployeeActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            UpdateUser(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void getEmployeeList(JsonObject Json) {

        compositeDisposable.add(api.getSuperUserList(Json).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       addItemsOnSpinner(jsonElements);
                                   } else {

                                       DialogForServiceDown(UpdateEmployeeActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(UpdateEmployeeActivity.this).show();
                               }
                           }


                )
        );


    }


    public void UpdateUser(JsonObject Json) {

        Log.d("updateing user :: " , Json.toString());
        compositeDisposable.add(api.UpdateEmployeeDetails(Json).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       Toast.makeText(UpdateEmployeeActivity.this, "User Details Update Successfully Done.", Toast.LENGTH_SHORT).show();
                                       finish();
                                   } else {

                                       DialogForServiceDown(UpdateEmployeeActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(UpdateEmployeeActivity.this).show();
                               }
                           }


                )
        );


    }

    public void getEmployeeDetails(JsonObject Json) {

        compositeDisposable.add(api.getEmployeeDetail(Json).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "getEmployeeDetail accept: Response ::" + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       JsonObject Json1 = new JsonObject();
                                       Json1.addProperty("requiredUser", "Manager','Admin','WAD");
                                       new GetManagerAdminListAsync().execute(Json1);
                                       Log.d(TAG, "getEmployeeDetail accept:  Service Call Successful..");
                                       Log.d(TAG, "getEmployeeDetail accept:  Service Call Successful..");
                                       JSONObject obj = new JSONObject(jsonElements.toString());
                                       Log.d(TAG, "obj"+obj);
                                       JSONArray data = obj.getJSONArray("data");
                                       Log.d(TAG, "data"+data);

                                       for (int i = 0; i < data.length(); i++) {
                                           JSONObject userDetail = data.getJSONObject(i);
                                           addItemOnUserTypeSpinner(userDetail.getString("role"),userDetail.getString("tl_id"));
                                           userID=userDetail.getString("user_id");
                                           FirstName.setText(userDetail.getString("firstname"));
                                           Lastname.setText(userDetail.getString("lastname"));
                                           Email.setText(userDetail.getString("email"));
                                           shift= userDetail.getString("shift_name");
                                           warehouse = userDetail.getString("warehouse_code");
                                           selectWarehouse.setText(warehouse);
                                          // Password.setText(userDetail.getString("password"));
                                           Password.setText("");
                                           String dobData = userDetail.getString("dob");
                                           if (dobData!= null){
                                               DOBtext.setText(getDateInDisplayFormat(dobData));
                                               DOBDateString = userDetail.getString("dob");
                                           }
                                           if(userDetail.getString("isactive").equals("Y")) {
                                               isActive.setChecked(true);
                                           }else{
                                               isActive.setChecked(false);
                                           }
                                           String joiningDt = userDetail.getString("joining_dt");
                                           if (joiningDt!= null){
                                               JoiningDateText.setText(getDateInDisplayFormat(joiningDt));
                                               joinDateString =  userDetail.getString("joining_dt");
                                           }

                                           Log.d(TAG, "gender"+userDetail.getString("gender"));
                                           if ((userDetail.getString("gender")).equals("M")) {
                                               gender = "M";
                                               Gender.check(R.id.taskIndividual);
                                           }
                                           else if ((userDetail.getString("gender")).equals("F")) {
                                               gender = "F";
                                               Gender.check(R.id.taskGroup);
                                           }
                                           else if ((userDetail.getString("gender")).equals("O")) {
                                               gender = "O";
                                               Gender.check(R.id.radioOther);
                                           }
                                           ContactNo.setText(userDetail.getString("contact_no"));
                                           EmpCode.setText(userDetail.getString("emp_code"));
                                           AadharNo.setText(userDetail.getString("aadhar_no"));
                                           PancardNo.setText(userDetail.getString("pan_no"));

                                       }
                                   } else {

                                       DialogForServiceDown(UpdateEmployeeActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "getEmployeeDetail accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(UpdateEmployeeActivity.this).show();
                               }
                           }


                )
        );


    }

    public String getDateInDisplayFormat(String dateData){

        String strDate=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

    public void addItemsOnSpinner(JsonObject data) {
        final List<String> list = new ArrayList<String>();
        list.add(0,"select");
        dataAsJsonArray = data.getAsJsonArray("data");
    Log.d(TAG,"dataAsJsonArray"+dataAsJsonArray);
        for (JsonElement TLList : dataAsJsonArray) {
            JsonObject Obj = TLList.getAsJsonObject();

            list.add(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());
            //Log.d(TAG,"list"+list.get(i));

        }


        Log.d(TAG, String.valueOf(list));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner_update.setAdapter(dataAdapter);
        userTypeSpinner_update.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position );

         /*       if (first) {
                    first = false;
                } else {

           */         if (position==0) {
                        Toast.makeText(UpdateEmployeeActivity.this, "Please select user ", Toast.LENGTH_SHORT).show();
                        userType = "";
                        scrollView.setVisibility(View.INVISIBLE);
                        submit_Update.setVisibility(View.INVISIBLE);
                    } else {
                          position--;
                        JsonElement data = dataAsJsonArray.get(position);

                    JsonObject dataObj = data.getAsJsonObject();
                        UserId = dataObj.get("user_id").getAsString();
                        Log.d(TAG, "user_id accept:  Service Call Successful.."+UserId);
                        JsonObject Json = new JsonObject();
                        Json.addProperty("userID", UserId);
                          // addItemOnUserTypeSpinner("");
                            new GetEmployeeDetailsAsync().execute(Json);

                    scrollView.setVisibility(View.VISIBLE);
                        submit_Update.setVisibility(View.VISIBLE);
                        findViewById(R.id.SelectUser).setVisibility(View.GONE);
                        findViewById(R.id.userTypeSpinner).setVisibility(View.GONE);
                    }
                }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void addItemOnUserTypeSpinner(final String role,final String tl_id) {
        List<String> list = new ArrayList<String>();
        list.add("select");
        list.add("Employee");
        list.add("Supervisor");
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpineer.setAdapter(dataAdapter);
        Log.d("Role Data :: ", role);
        Log.d("Data Adaptor :: ", dataAdapter.toString());
        if(role.equals("Manager")){
            lblTLName.setText("WAD Name");
            userTypeSpineer.setSelection(dataAdapter.getPosition("Supervisor"));
        }
        else{
            lblTLName.setText("Supervisor");
            userTypeSpineer.setSelection(dataAdapter.getPosition(role));
        }


            Log.d(TAG, " Inside addItemOnUserTypeSpinner");
            userTypeSpineer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               /* if (first) {
                    first = false;
                } else {*/

                    if (position == 0) {
                        Toast.makeText(UpdateEmployeeActivity.this, "Please select user type", Toast.LENGTH_SHORT).show();
                        userType = "";
                    } else {

                        if (parent.getItemAtPosition(position).toString().equals("Employee")) {
                            userType = "Employee";
                            addItemsOnSuperUserSpinner(managerAdminJsonData, "Manager",tl_id);
                        } else {
                             userType = "Manager";
                            addItemsOnSuperUserSpinner(managerAdminJsonData, "WAD",tl_id);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
      }

    public void addItemsOnSuperUserSpinner(JsonObject data, String role,String tl_id) {
        String userName = null;
        List<String> list = new ArrayList<String>();
        list.add("select");
        dataAsJsonArraySuperUser = data.getAsJsonArray("data");
        Log.d(TAG,"dataAsJsonArraySuperUser : :"+dataAsJsonArraySuperUser);
        for (JsonElement List : dataAsJsonArraySuperUser) {
            JsonObject Obj = List.getAsJsonObject();
            if (Obj.get("role").getAsString().equals(role))
                list.add(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());
               if(tl_id.equals(Obj.get("user_id").getAsString())){
                              userName=Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString();
                              TLID=tl_id;
            }
        }



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        superUserSpinner.setAdapter(dataAdapter);
        superUserSpinner.setSelection(dataAdapter.getPosition(userName));
     //   if(tl_id.equals("")) {
            superUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Super user position", String.valueOf(position));

                    if (first) {
                        first = false;
                    } else {

                        if (position == 0) {
                            Log.d("if position Test", String.valueOf(position));
                           Toast.makeText(UpdateEmployeeActivity.this, "Please select super user.", Toast.LENGTH_SHORT).show();
                            TLID = "";
                        } else {
                            String userName = superUserSpinner.getSelectedItem().toString();
                            Log.d("Sper User userName :: ", userName);
                            for (JsonElement List : dataAsJsonArraySuperUser) {
                                JsonObject Obj = List.getAsJsonObject();
                                if ((Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString()).equals(userName)) {
                                    TLID = Obj.get("user_id").getAsString();
                                    Log.d("TL ID :: ", TLID);
                                    Toast.makeText(UpdateEmployeeActivity.this, Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString(), Toast.LENGTH_SHORT).show();
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
            String DOBdate = year+ "-" + month + "-" +dayOfMonth ;
            DOBDateString = year + "/" + month + "/" + dayOfMonth;

            DOBtext.setText(getDateInDisplayFormat(DOBdate));
        }
    };


    //2nd Listner
    DatePickerDialog.OnDateSetListener JoiningDate_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            String Joiningdate = year+ "-" + month + "-" +dayOfMonth ;
            joinDateString = year + "/" + month + "/" + dayOfMonth;//yyyy/mm/dd
            JoiningDateText.setText(getDateInDisplayFormat(Joiningdate));
        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {

            case R.id.FIRST_NAME_Update:
                if (hasFocus == true) {
Log.d(TAG,"JoiningDateText.getText().toString().equals(\"\")"+(JoiningDateText.getText().toString().equals("")));
                    if (JoindateButtonClick || JoiningDateText.getText().toString().equals("")) {


                        Toast.makeText(UpdateEmployeeActivity.this, "Please Select Joining Date.", Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            case R.id.LAST_NAME_Update:

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


            case R.id.EMAIL_Update:
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


            case R.id.PASSWORD_Update:
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

            case R.id.EMPCODE_Update:

                if (hasFocus == true) {


                    if (DOBdateButtonClick || DOBtext.getText().toString().equals("")) {

                        Toast.makeText(UpdateEmployeeActivity.this, "Please Select DOB.", Toast.LENGTH_SHORT).show();

                    }

                }
                break;

            case R.id.PANCARD_Update:

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


            case R.id.AADHARCARD_Update:
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
                                       Dailog.DialogForServiceDown(UpdateEmployeeActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..");
                                   Dailog.DialogForServiceDown(UpdateEmployeeActivity.this).show();
                               }
                           }


                )
        );


    }

    //from activity to fragment on back button click
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