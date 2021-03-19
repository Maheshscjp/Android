package com.example.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import android.widget.Button;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;


import org.json.JSONException;

import java.util.ArrayList;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CheckLeaveStatusActivity extends AppCompatActivity {
    private static final String TAG = "CheckLeaveStatus";

    NodeJS api;
    CompositeDisposable compositeDisposable;
    User LoggedInUser;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;
    ListView list;
    private List<String> List_file;
    // String []item;

    String leaveStatus,leaveID;

    static final int CUSTOM_DIALOG_ID_SUCCESS = 0;
    static final int CUSTOM_DIALOG_ID_REJECT = 1;
    static  final int CUSTOM_DIALOG_ID_PENDING = 2;
    static  final int CUSTOM_DIALOG_ID_Canceled = 3;

    TextView TotalLeaveCountS, leaveFromDateS, leaveToDateS, commentsS;
    TextView TotalLeaveCountR, leaveFromDateR, leaveToDateR, commentsR;
    TextView TotalLeaveCountP, leaveFromDateP, leaveToDateP, commentsP;
    TextView TotalLeaveCountC, leaveFromDateC, leaveToDateC, commentsC;
    EditText ReasonCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_leave_status);
        LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        JsonObject Json = new JsonObject();
        Json.addProperty("user_id", LoggedInUser.getUserid());

        Log.d(TAG, "onCreate: request :: " + Json.toString());
        List_file = new ArrayList<String>();
        list =  findViewById(R.id.listview);

        new GetLeaveDetails().execute(Json);


    }

    class GetLeaveDetails extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CheckLeaveStatusActivity.this,
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

    public void getLeaveDetails(JsonObject jsonObject) {


        compositeDisposable.add(api.getLeaveStatus(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: response :: " + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       CreateListView(jsonElements);
                                   } else {

                                       Log.d(TAG, "accept:  Service Call Successful..Error.........");
                                       DialogForServiceDown(CheckLeaveStatusActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                   DialogForServiceDown(CheckLeaveStatusActivity.this).show();
                               }
                           }


                )
        );

    }

    class UpdateLeaveDetails extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CheckLeaveStatusActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            updateLeaveDetails(jsonObjects[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void updateLeaveDetails(JsonObject jsonObject) {


        compositeDisposable.add(api.updateLeaveReq(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "updateLeaveReq accept: response :: " + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "updateLeaveReq accept:  Service Call Successful..");

                                   } else {

                                       Log.d(TAG, "accept:  Service Call Successful..Error.........");
                                       DialogForServiceDown(CheckLeaveStatusActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                   DialogForServiceDown(CheckLeaveStatusActivity.this).show();
                               }
                           }


                )
        );

    }


    private void CreateListView(JsonObject jsonObject) throws JSONException {

        Log.d(TAG, "CreateListView: data of list ::" + jsonObject.toString());

        dataAsJsonArray = jsonObject.getAsJsonArray("data");
        for (JsonElement LeaveList : dataAsJsonArray) {
            JsonObject Obj = LeaveList.getAsJsonObject();
            Log.d(TAG, "CreateListView: Json Data Leave ::" + Obj.toString());
            JsonElement approved = Obj.get("approved");
            leaveStatus = (approved instanceof JsonNull) ? "Pending" : approved.getAsString();

            if (leaveStatus.equals("Y")) {
                leaveStatus = "Approved";
            }
            else if (leaveStatus.equals("N")) {
                leaveStatus = "Rejected";
            }
            else if(leaveStatus.equals("C")){
                leaveStatus = "Canceled";
            }
            List_file.add(Obj.get("from_date").getAsString() + " TO " + Obj.get("to_date").getAsString() + " : " + leaveStatus);
        }
        ListViewAdapter adapter=new ListViewAdapter(this,R.layout.custom_list_view,R.id.txt,List_file);
        // Bind data to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                //args2 is the listViews Selected index

                JsonElement data = dataAsJsonArray.get(arg2);
                JsonObject dataObj = data.getAsJsonObject();
                String Comments = (dataObj.get("tl_comment") instanceof JsonNull) ? "" : dataObj.get("tl_comment").getAsString();
                String Reason = (dataObj.get("user_comment") instanceof JsonNull) ? "" : dataObj.get("user_comment").getAsString();
                /*if (dataObj.get("approved") instanceof JsonNull) {
                    Toast.makeText(CheckLeaveStatusActivity.this, "Your leave approval is pending.", Toast.LENGTH_SHORT).show();
                }



                else */
                if (!(dataObj.get("approved") instanceof JsonNull) && dataObj.get("approved").getAsString().equals("N")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("KEY_FROMDATE_FAILED", dataObj.get("from_date").getAsString());
                    bundle.putString("KEY_TODATE_FAILED", dataObj.get("to_date").getAsString());
                    bundle.putString("KEY_LEAVECOUNT_FAILED", dataObj.get("leave_count").getAsString());
                    bundle.putString("KEY_COMMENT_FAILED", Comments);

                    showDialog(CUSTOM_DIALOG_ID_REJECT, bundle);

                }
                else if (!(dataObj.get("approved") instanceof JsonNull) && dataObj.get("approved").getAsString().equals("Y")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("KEY_FROMDATE_SUCCESS", dataObj.get("from_date").getAsString());
                    bundle.putString("KEY_TODATE_SUCCESS", dataObj.get("to_date").getAsString());
                    bundle.putString("KEY_LEAVECOUNT_SUCCESS", dataObj.get("leave_count").getAsString());
                    bundle.putString("KEY_COMMENT_SUCCESS", Comments);

                    showDialog(CUSTOM_DIALOG_ID_SUCCESS, bundle);
                }
                else if(!(dataObj.get("approved") instanceof JsonNull) && dataObj.get("approved").getAsString().equals("C")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("KEY_FROMDATE_CANCELED", dataObj.get("from_date").getAsString());
                    bundle.putString("KEY_TODATE_CANCELED", dataObj.get("to_date").getAsString());
                    bundle.putString("KEY_LEAVECOUNT_CANCELED", dataObj.get("leave_count").getAsString());
                    bundle.putString("KEY_COMMENT_CANCELED", Reason);

                    showDialog(CUSTOM_DIALOG_ID_Canceled, bundle);
                }



            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;

        switch (id) {
            case CUSTOM_DIALOG_ID_SUCCESS:
                dialog = new Dialog(CheckLeaveStatusActivity.this);
                dialog.setContentView(R.layout.dialoglayout_approve);

                TotalLeaveCountS = (TextView) dialog.findViewById(R.id.TotalLeaveCountS);
                leaveFromDateS = (TextView) dialog.findViewById(R.id.leaveFromDateS);
                leaveToDateS = (TextView) dialog.findViewById(R.id.leaveToDateS);
                commentsS = (TextView) dialog.findViewById(R.id.commentsS);


                Button dialog_OK = (Button) dialog.findViewById(R.id.OkButton);
                dialog_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismissDialog(CUSTOM_DIALOG_ID_SUCCESS);
                    }
                });


                break;

            case CUSTOM_DIALOG_ID_REJECT:
                dialog = new Dialog(CheckLeaveStatusActivity.this);
                dialog.setContentView(R.layout.dialoglayout_reject);

                TotalLeaveCountR = (TextView) dialog.findViewById(R.id.TotalLeaveCountR);
                leaveFromDateR = (TextView) dialog.findViewById(R.id.leaveFromDateR);
                leaveToDateR = (TextView) dialog.findViewById(R.id.leaveToDateR);
                commentsR = (TextView) dialog.findViewById(R.id.commentsR);

                Button dialog_OK1 = (Button) dialog.findViewById(R.id.OkButton);
                dialog_OK1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismissDialog(CUSTOM_DIALOG_ID_REJECT);
                    }
                });


                break;

            case CUSTOM_DIALOG_ID_PENDING:
                dialog = new Dialog(CheckLeaveStatusActivity.this);
                dialog.setContentView(R.layout.dialoglayout_pending);

                TotalLeaveCountP = (TextView) dialog.findViewById(R.id.TotalLeaveCountP);
                leaveFromDateP = (TextView) dialog.findViewById(R.id.leaveFromDateP);
                leaveToDateP = (TextView) dialog.findViewById(R.id.leaveToDateP);
                commentsP = (TextView) dialog.findViewById(R.id.commentsP);
                ReasonCancel = (EditText) dialog.findViewById(R.id.ReasonCancel);

                Log.d(TAG,"LEAVE ID :: "+leaveID);
                Button dialog_OK2 = (Button) dialog.findViewById(R.id.confirmbutton);
                Button dialog_cancel = (Button) dialog.findViewById(R.id.cancelbutton);
                final JsonObject leaveCancelObj = new JsonObject();
                leaveCancelObj.addProperty("leaveId", leaveID);
                dialog_OK2.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        Log.d(TAG,"LEAVE ID1 :: "+leaveID);

                        leaveCancelObj.addProperty("user_comment", ReasonCancel.getText().toString());
                        leaveCancelObj.addProperty("isApproved", "C");
                        Log.d(TAG,"LEAVE OBJ :: "+leaveCancelObj);
                        if ((leaveCancelObj.get("user_comment").getAsString()).equals("") ) {
                            ReasonCancel.setError("Please enter Reson");
                            ReasonCancel.requestFocus();
                        }else {
                            new UpdateLeaveDetails().execute(leaveCancelObj);
                            dismissDialog(CUSTOM_DIALOG_ID_PENDING);
                            Toast.makeText(CheckLeaveStatusActivity.this, "Your leave is cancel succesfully.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }

                    }
                });

                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog(CUSTOM_DIALOG_ID_PENDING);
                    }
                });
                break;

            case CUSTOM_DIALOG_ID_Canceled:
                dialog = new Dialog(CheckLeaveStatusActivity.this);
                dialog.setContentView(R.layout.dialoglayout_canceled);

                TotalLeaveCountC = (TextView) dialog.findViewById(R.id.TotalLeaveCountC);
                leaveFromDateC = (TextView) dialog.findViewById(R.id.leaveFromDateC);
                leaveToDateC = (TextView) dialog.findViewById(R.id.leaveToDateC);
                commentsC = (TextView) dialog.findViewById(R.id.commentsC);

                Button dialog_OK3 = (Button) dialog.findViewById(R.id.OkButtonC);
                dialog_OK3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dismissDialog(CUSTOM_DIALOG_ID_Canceled);
                    }
                });


                break;
        }

        return dialog;
    }


    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {

        super.onPrepareDialog(id, dialog, bundle);

        switch (id) {
            case CUSTOM_DIALOG_ID_SUCCESS:

                TotalLeaveCountS.setText("Leave Count : " + bundle.getString("KEY_LEAVECOUNT_SUCCESS"));
                leaveFromDateS.setText("From Date : " + bundle.getString("KEY_FROMDATE_SUCCESS"));
                leaveToDateS.setText("To Date : " + bundle.getString("KEY_TODATE_SUCCESS"));
                commentsS.setText("Comments : " + bundle.getString("KEY_COMMENT_SUCCESS"));



                break;

            case CUSTOM_DIALOG_ID_REJECT:

                TotalLeaveCountR.setText("Leave Count : " + bundle.getString("KEY_LEAVECOUNT_FAILED"));
                leaveFromDateR.setText("From Date : " + bundle.getString("KEY_FROMDATE_FAILED"));
                leaveToDateR.setText("To Date : " + bundle.getString("KEY_TODATE_FAILED"));
                commentsR.setText("Comments : " + bundle.getString("KEY_COMMENT_FAILED"));

                break;

            case CUSTOM_DIALOG_ID_PENDING:

                TotalLeaveCountP.setText("Leave Count : " + bundle.getString("KEY_LEAVECOUNT_PAINDING"));
                leaveFromDateP.setText("From Date : " + bundle.getString("KEY_FROMDATE_PAINDING"));
                leaveToDateP.setText("To Date : " + bundle.getString("KEY_TODATE_PAINDING"));
                commentsP.setText("Comments : " + bundle.getString("KEY_COMMENT_PAINDING"));

                break;

            case CUSTOM_DIALOG_ID_Canceled:

                TotalLeaveCountC.setText("Leave Count : " + bundle.getString("KEY_LEAVECOUNT_CANCELED"));
                leaveFromDateC.setText("From Date : " + bundle.getString("KEY_FROMDATE_CANCELED"));
                leaveToDateC.setText("To Date : " + bundle.getString("KEY_TODATE_CANCELED"));
                commentsC.setText("Comments : " + bundle.getString("KEY_COMMENT_CANCELED"));

                break;
        }
    }

    public void clickMe(View view) {
        Button bt = (Button) view;
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        Log.d("Test","Position ::"+position);
        JsonElement data = dataAsJsonArray.get(position);
        JsonObject dataObj = data.getAsJsonObject();
        Log.d("dataObj","dataAsJsonArray ::"+dataObj);
        String Comments = (dataObj.get("tl_comment") instanceof JsonNull) ? "" : dataObj.get("tl_comment").getAsString();
        String reason = (dataObj.get("reason") instanceof JsonNull) ? "" : dataObj.get("reason").getAsString();
        if(dataObj.get("approved") instanceof JsonNull) {
            Bundle bundle = new Bundle();
            bundle.putString("KEY_FROMDATE_PAINDING", dataObj.get("from_date").getAsString());
            bundle.putString("KEY_TODATE_PAINDING", dataObj.get("to_date").getAsString());
            bundle.putString("KEY_LEAVECOUNT_PAINDING", dataObj.get("leave_count").getAsString());
            bundle.putString("KEY_COMMENT_PAINDING", reason);
            //   bundle.putString("KEY_LEAVE _ID",dataObj.get("leave_id").getAsString());
            leaveID=dataObj.get("leave_id").getAsString();
            showDialog(CUSTOM_DIALOG_ID_PENDING, bundle);
        }


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
