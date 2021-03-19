package com.example.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.util.InputValidation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LeaveRequestDetailsActivity extends AppCompatActivity {
    private static final String TAG = "LeaveRequestDetails";


    NodeJS api;
    CompositeDisposable compositeDisposable;
    // User LoggedInUser;
    ProgressDialog progressDialog;
    // JsonArray dataAsJsonArray;


    private TextView name;
    private TextView FromDateText;
    private TextView ToDateText;
    private TextView LeaveCount;
    private TextView Reason;
    private EditText Comments;
    private Spinner ApproverSpinner;
    private boolean first = true;
    private Button submit;
    private String leaveID = "";
    private String comments = "";
    private String isApproved = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_request_details);
        addItemsOnSpinner();


        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();


        name = findViewById(R.id.name);
        FromDateText = findViewById(R.id.FromDateText);
        ToDateText = findViewById(R.id.ToDateText);
        LeaveCount = findViewById(R.id.LeaveCount);
        Reason = findViewById(R.id.Reason);
        Comments = findViewById(R.id.Comments);
        Comments.setFilters(InputValidation.inputFilterAllowAlpha);
        submit = findViewById(R.id.submit);

        leaveID = getIntent().getStringExtra("LEAVEID");
        name.setText(getIntent().getStringExtra("NAME"));
        FromDateText.setText(getIntent().getStringExtra("FROMDATE"));
        ToDateText.setText(getIntent().getStringExtra("TODATE"));
        LeaveCount.setText(getIntent().getStringExtra("LEAVECOUNT"));
        Reason.setText(getIntent().getStringExtra("REASON"));


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comments = (TextUtils.isEmpty(Comments.getText().toString())) ? "" : Comments.getText().toString();

                JsonObject jsonObjectRequest = new JsonObject();


                jsonObjectRequest.addProperty("tlComment", comments);
                jsonObjectRequest.addProperty("isApproved", isApproved);
                jsonObjectRequest.addProperty("leaveId", leaveID);

                Log.d(TAG, "onClick: Request ::" + jsonObjectRequest.toString());
                if (TextUtils.isEmpty(jsonObjectRequest.get("isApproved").getAsString())) {
                    Toast.makeText(LeaveRequestDetailsActivity.this, "Please approved the leave.", Toast.LENGTH_SHORT).show();
                    return;

                } else if (TextUtils.isEmpty(jsonObjectRequest.get("leaveId").getAsString())) {
                    Toast.makeText(LeaveRequestDetailsActivity.this, "Please Login Again.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new UpdateLeaveRequest().execute(jsonObjectRequest);
                }

            }
        });


    }

    class UpdateLeaveRequest extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LeaveRequestDetailsActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            updateLeave(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void updateLeave(JsonObject jsonObject) {
        compositeDisposable.add(api.updateLeaveReq(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: Response " + jsonElements.toString());

                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       Toast.makeText(LeaveRequestDetailsActivity.this, "Leave updated successfully.", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(LeaveRequestDetailsActivity.this, GetLeaveRequestActivity.class);
                                       finish();
                                       startActivity(intent);


                                   } else {
                                       DialogForServiceDown(LeaveRequestDetailsActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..");
                                   DialogForServiceDown(LeaveRequestDetailsActivity.this).show();
                               }
                           }


                )
        );
    }


    public void addItemsOnSpinner() {

        ApproverSpinner = (Spinner) findViewById(R.id.ApproverSpinner);
        List<String> list = new ArrayList<String>();
        list.add("select");
        list.add("Yes");
        list.add("No");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ApproverSpinner.setAdapter(dataAdapter);
        ApproverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first) {
                    first = false;
                } else {

                    if (position == 0) {
                        Toast.makeText(LeaveRequestDetailsActivity.this, "Please select appropriate option!", Toast.LENGTH_SHORT).show();
                    } else {
                        // isApproved = parent.getItemAtPosition(position).toString();

                        if (parent.getItemAtPosition(position).toString().equals("Yes")) {
                            isApproved = "Y";
                        }
                       else if (parent.getItemAtPosition(position).toString().equals("No")) {
                            isApproved = "N";
                        }
                        // isApproved = (parent.getItemAtPosition(position).toString().equals("Yes"))? "Y" : "N";
                    }
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
