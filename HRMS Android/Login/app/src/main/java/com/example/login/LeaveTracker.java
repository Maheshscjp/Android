package com.example.login;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LeaveTracker extends AppCompatActivity {

    private static final String TAG = "Leave Tracker";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    Spinner userTypeSpinner1;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray,statusDataJsonArray;
    String UserId = "" , userType="",year="",leaveStatus="";
    User LoggedInUser;
    JsonObject leaveStatusObj;
    int pendingCount=0,canceledCount=0,approveCount=0,rejectedCount=0;
    TextView AcceptedLeaveValue, PendingLeaveValue, RejectedLeaveValue, CanceledLeaveValue;
    public static LeaveTracker newInstance() {
        return new LeaveTracker();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_tracker);
        LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        userTypeSpinner1=findViewById(R.id.userTypeSpinner1);
        compositeDisposable = new CompositeDisposable();
        findViewById(R.id.Panel).setVisibility(View.GONE);
        findViewById(R.id.textView3).setVisibility(View.GONE);
        findViewById(R.id.floatingActionButton2).setVisibility(View.GONE);
        findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
        findViewById(R.id.yearSpineer).setVisibility(View.GONE);

        AcceptedLeaveValue=(TextView) findViewById(R.id.AcceptedLeaveValue);
        PendingLeaveValue=(TextView) findViewById(R.id.PendingLeaveValue);
        RejectedLeaveValue=(TextView) findViewById(R.id.RejectedLeaveValue);
        CanceledLeaveValue=(TextView) findViewById(R.id.CanceledLeaveValue);

         addYearOnSpineer();

        JsonObject Json = new JsonObject();
        Json.addProperty("requiredUser", "Employee','Manager");
        Log.d(TAG, "accept: Requets ::" + Json.toString());
        new GetEmployeeListAsync().execute(Json);
        if(year.equals("")){
            year=Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                findViewById(R.id.Panel).setVisibility(View.VISIBLE);
                findViewById(R.id.floatingActionButton2).setVisibility(view.VISIBLE);
                findViewById(R.id.yearSpineer).setVisibility(view.VISIBLE);
                findViewById(R.id.floatingActionButton).setVisibility(view.GONE);


            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                findViewById(R.id.Panel).setVisibility(View.GONE);
                findViewById(R.id.floatingActionButton2).setVisibility(view.GONE);
                findViewById(R.id.floatingActionButton).setVisibility(view.VISIBLE);
                findViewById(R.id.yearSpineer).setVisibility(view.VISIBLE);

            }
        });


    }


    class GetEmployeeListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LeaveTracker.this,
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

                                       DialogForServiceDown(LeaveTracker.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(LeaveTracker.this).show();
                               }
                           }


                )
        );


    }

    public void addItemsOnSpinner(JsonObject data) {
        final List<String> list = new ArrayList<String>();
        list.add(0,"select");
        dataAsJsonArray = data.getAsJsonArray("data");
        Log.d(TAG,"dataAsJsonArray"+dataAsJsonArray.size());
        for (JsonElement TLList : dataAsJsonArray) {
            JsonObject Obj = TLList.getAsJsonObject();

            list.add(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());
            //Log.d(TAG,"list"+list.get(i));

        }


        Log.d(TAG,"List :: " +String.valueOf(list));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner1.setAdapter(dataAdapter);
        userTypeSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position );

         /*       if (first) {
                    first = false;
                } else {

           */         if (position==0) {
                    Toast.makeText(LeaveTracker.this, "Please select user ", Toast.LENGTH_SHORT).show();
                    userType = "";

                    findViewById(R.id.Panel).setVisibility(View.INVISIBLE);
                } else {
                    position--;

                    Log.d("Test :: ", String.valueOf(position));
                    JsonElement data = dataAsJsonArray.get(position);

                    JsonObject dataObj = data.getAsJsonObject();
                    Log.d(TAG, "dataObj accept:  Service Call Successful.." + dataObj.toString());
                    UserId = dataObj.get("user_id").getAsString();
                    Log.d(TAG, "user_id accept:  Service Call Successful.." + UserId);
                    if(!UserId.equals("")){
                        JsonObject Json = new JsonObject();
                        Json.addProperty("user_id", UserId);
                        Json.addProperty("year", year);

                        new getLeaveDetailsByDate().execute(Json);


                        findViewById(R.id.userTypeSpinner1).setVisibility(View.VISIBLE);
                        findViewById(R.id.SelectUser1).setVisibility(View.VISIBLE);
                        findViewById(R.id.Panel).setVisibility(View.INVISIBLE);
                        findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                        findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
                        findViewById(R.id.floatingActionButton2).setVisibility(View.GONE);
                        //  startActivity(getIntent());
                    }

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
  public  void  addYearOnSpineer(){

        Spinner spinYear = (Spinner)findViewById(R.id.yearSpineer);
        final ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2015; i <= thisYear+5; i++) {
            years.add(Integer.toString(i));
        }
         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYear.setAdapter(adapter);
        spinYear.setSelection(adapter.getPosition(Integer.toString(Calendar.getInstance().get(Calendar.YEAR))));
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);
                if (position == 0) {
                    year=Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
                    Log.d("IF YEAR :: ", year);
                        findViewById(R.id.Panel).setVisibility(View.VISIBLE);
                } else {
                   // position--;
                    year = years.get(position);
                    JsonObject Json = new JsonObject();
                    Json.addProperty("user_id", UserId);
                    Json.addProperty("year",year);

                    new getLeaveDetailsByDate().execute(Json);


                    Log.d("IF else :: " ,year);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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


    class getLeaveDetailsByDate extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LeaveTracker.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            getLeaveDetails(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void getStatusCount(JsonObject jsonObject){
        statusDataJsonArray = jsonObject.getAsJsonArray("data");
        for (JsonElement LeaveList : statusDataJsonArray) {
            JsonObject Obj = LeaveList.getAsJsonObject();
            JsonElement approved = Obj.get("approved");
            leaveStatus = (approved instanceof JsonNull) ? "Pending" : approved.getAsString();


            if (leaveStatus.equals("Y")) {
                leaveStatus = "Approved";
                approveCount++;
            }
            else if (leaveStatus.equals("N")) {
                leaveStatus = "Rejected";
                rejectedCount++;
            }
            else if(leaveStatus.equals("C")){
                leaveStatus = "Canceled";
                canceledCount++;
            }else if(leaveStatus.equals("Pending")){
                leaveStatus="Pending";
                pendingCount++;
            }


        }
        AcceptedLeaveValue.setText(String.valueOf(approveCount));
        PendingLeaveValue.setText(String.valueOf(pendingCount));
        RejectedLeaveValue.setText(String.valueOf(rejectedCount));
        CanceledLeaveValue.setText(String.valueOf(canceledCount));

        Log.d("LeaveTracker Count","PCount :: "+pendingCount+" ACount :: "+approveCount+" Ccount ::"+canceledCount +" RCount :: "+rejectedCount);
    }
    private void getLeaveDetails(JsonObject jsonObject) {
        compositeDisposable.add(api.getLeaveByYear(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "getLeaveDetails accept: response :: " + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, " getLeaveDetails accept:  Service Call Successful..");
                                     leaveStatusObj=jsonElements;
                                       pendingCount=0;
                                       canceledCount=0;
                                       approveCount=0;
                                       rejectedCount=0;
                                     getStatusCount(leaveStatusObj);
                                    Log.d(TAG, " leaveStatusObj "+leaveStatusObj);
                                   } else {

                                       Log.d(TAG, "accept:  Service Call Successful..Error.........");
                                       DialogForServiceDown(LeaveTracker.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                   DialogForServiceDown(LeaveTracker.this).show();
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
