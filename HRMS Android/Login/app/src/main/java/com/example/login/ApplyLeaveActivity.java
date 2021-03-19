package com.example.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.email.MailSender;
import com.example.login.util.ApplicationContext;
import com.example.login.util.ConfigProperties;
import com.example.login.util.InputValidation;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ApplyLeaveActivity extends AppCompatActivity {
    private static final String TAG = "ApplyLeaveActivity";

    NodeJS api;
    CompositeDisposable compositeDisposable;

    String TlEmail;
    private EditText reason;
    private Button Submit;
    private ImageButton fromDate, toDate;
    private TextView fromDateText, toDateText;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private String fromDateString = "", toDateString = "", fromDateStringYMD = "", toDateStringYMD = "";
    private Date fromdateDate, todateDate;
    private Boolean fromDateButton;
    private TextView leaveCount, superUserText;
    ProgressDialog progressDialog;
    int fromDateYear, fromDateMonth, fromDateDay;
    User LoggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_leave);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        fromDateButton = false;
        reason = findViewById(R.id.Reason);
        reason.setFilters(InputValidation.inputFilterAllowAlpha);
        fromDate = findViewById(R.id.FromDate);
        toDate = findViewById(R.id.ToDate);
        fromDateText = findViewById(R.id.FromDateText);
        toDateText = findViewById(R.id.ToDateText);
        Submit = findViewById(R.id.submit);
        leaveCount = findViewById(R.id.name);
        superUserText = findViewById(R.id.superUserText);

        if (LoggedInUser != null) {
            superUserText.setText(LoggedInUser.getTlname());
            TlEmail = LoggedInUser.getTlEmailId();

        } else {
            Toast.makeText(this, "Please Login Again.", Toast.LENGTH_SHORT).show();
        }

        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        JsonObject json = new JsonObject();
        json.addProperty("employee_id", LoggedInUser.getUserid());
        Log.d(TAG, "onCreate: " + json.toString());
        new GetLeaveCount().execute(json);


        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDateButton = true;
                showDatePickerDialog("FromDateCode", 0, 0, 0);
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog("ToDateCode", fromDateYear, fromDateMonth, fromDateDay);
            }
        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject leaveData = new JsonObject();
                leaveData.addProperty("employeeId", LoggedInUser.getUserid());
                leaveData.addProperty("approverId", LoggedInUser.getTlid());
                leaveData.addProperty("fromDate", fromDateStringYMD);
                leaveData.addProperty("toDate", toDateStringYMD);
                leaveData.addProperty("pendingCount", leaveCount.getText().toString());
                leaveData.addProperty("reason", reason.getText().toString());

                if (TextUtils.isEmpty(leaveData.get("employeeId").getAsString())) {
                    Toast.makeText(ApplyLeaveActivity.this, "Please Login.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(leaveData.get("approverId").getAsString())) {
                    Toast.makeText(ApplyLeaveActivity.this, "Please Login Again.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(leaveData.get("fromDate").getAsString())) {
                    Toast.makeText(ApplyLeaveActivity.this, "Please select From Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(leaveData.get("toDate").getAsString())) {
                    Toast.makeText(ApplyLeaveActivity.this, "Please select To Date.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(leaveData.get("pendingCount").getAsString())) {
                    Toast.makeText(ApplyLeaveActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(leaveData.get("reason").getAsString())) {
                    Toast.makeText(ApplyLeaveActivity.this, "Please write reason.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new RegisterLeaveDetail().execute(leaveData);
                }

            }
        });
    }


    class GetLeaveCount extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {


            super.onPreExecute();
            progressDialog = ProgressDialog.show(ApplyLeaveActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            getLeave(jsonObjects[0]);


            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    class RegisterLeaveDetail extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ApplyLeaveActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            registerLeaveDetail(jsonObjects[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void getLeave(JsonObject jsonObject) {

        compositeDisposable.add(api.getPendingLeaveCount(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: leaveCount Response ::" + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {

                                       leaveCount.setText(jsonElements.getAsJsonObject("data").get("pd_count").getAsString());
                                   } else {
                                       DialogForServiceDown(ApplyLeaveActivity.this).show();
                                   }

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(ApplyLeaveActivity.this).show();
                               }
                           }


                )
        );
    }

    public void registerLeaveDetail(JsonObject jsonObject) {

        compositeDisposable.add(api.registerLeaveDetail(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {

                                   if (jsonElements.get("success").getAsBoolean()) {

                                       Toast.makeText(ApplyLeaveActivity.this, "Leave request successfully registered.", Toast.LENGTH_SHORT).show();

                                      /* MailSender sender = new MailSender();
                                       sender.sendMail("TEST", "Hiiiii", "mayur.prajapati@upvoteconsulting.com");*/
                                       finish();

                                   } else if (!jsonElements.get("success").getAsBoolean() && jsonElements.get("errorCode").getAsString().equals("23505")) {
                                       Toast.makeText(ApplyLeaveActivity.this, "Approver does not exist.", Toast.LENGTH_SHORT).show();
                                   } else {
                                       DialogForServiceDown(ApplyLeaveActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..");
                                   DialogForServiceDown(ApplyLeaveActivity.this).show();
                               }
                           }


                )
        );


    }


    public void showDatePickerDialog(String a, int year, int month, int day) {
        if (a.equals("FromDateCode")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    from_dateListener,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
        if (a.equals("ToDateCode")) {

            if (fromDateButton && !fromDateText.getText().toString().equals("")) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        to_dateListener,
                        year, month - 1, day);

                long minDate = fromdateDate.getTime();

                datePickerDialog.getDatePicker().setMinDate(minDate);
                datePickerDialog.show();
            } else {
                Toast.makeText(ApplyLeaveActivity.this, "Please select From Date.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    DatePickerDialog.OnDateSetListener from_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            fromDateString = dayOfMonth + "/" + month + "/" + year;
            fromDateStringYMD = year + "/" + month + "/" + dayOfMonth;
            fromDateYear = year;
            fromDateMonth = month;
            fromDateDay = dayOfMonth;

            //Log.d(TAG, "onDateSet: " + fromDateDay + fromDateMonth + fromDateYear);

            fromDateText.setText(fromDateString);

            try {
                fromdateDate = formatter.parse(fromDateString);
                Log.d(TAG, "onDateSet: " + fromdateDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };

    DatePickerDialog.OnDateSetListener to_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            toDateString = dayOfMonth + "/" + month + "/" + year;
            toDateStringYMD = year + "/" + month + "/" + dayOfMonth;
            toDateText.setText(toDateString);


            try {
                todateDate = formatter.parse(toDateString);
                Log.d(TAG, "onDateSet: " + todateDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (((todateDate.getTime() - fromdateDate.getTime()) / (24 * 60 * 60 * 1000)) > Integer.parseInt(leaveCount.getText().toString())) {
                DialogForExceedLeaveDays(ApplyLeaveActivity.this).show();
            }

        }
    };


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

    public AlertDialog.Builder DialogForExceedLeaveDays(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Exceed Days..!");
        builder.setMessage("Your apply leave count is exceed than remaining leave count.");

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


}
