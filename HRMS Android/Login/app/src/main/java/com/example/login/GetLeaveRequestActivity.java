package com.example.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class GetLeaveRequestActivity extends AppCompatActivity {

    private static final String TAG = "GetLeaveRequestActivity";

    NodeJS api;
    CompositeDisposable compositeDisposable;
    User LoggedInUser;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;


    ListView list;
    private List<String> List_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_leave_request);

        LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        List_file = new ArrayList<String>();
        list = (ListView) findViewById(R.id.listview);


        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        JsonObject Json = new JsonObject();
        Json.addProperty("employeeId", LoggedInUser.getUserid());
        new GetLeaveDetailsOfUser().execute(Json);


    }

    class GetLeaveDetailsOfUser extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(GetLeaveRequestActivity.this,
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


        compositeDisposable.add(api.getEmployeeLeaves(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: response ::"+jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       CreateListView(jsonElements);
                                   }

                                   else  {
                                       DialogForServiceDown(GetLeaveRequestActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>()  {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.."+throwable.getMessage());
                                   DialogForServiceDown(GetLeaveRequestActivity.this).show();
                               }
                           }


                )
        );

    }

    private void CreateListView(JsonObject jsonObject) {
        dataAsJsonArray = jsonObject.getAsJsonArray("data");
        if(dataAsJsonArray.size() == 0 )
        {
            Toast.makeText(GetLeaveRequestActivity.this,"No leave request.",Toast.LENGTH_SHORT).show();
        }
        for (JsonElement LeaveList : dataAsJsonArray) {
            JsonObject Obj = LeaveList.getAsJsonObject();

            /*  List_file.add(Obj.get("from_date").getAsString() + " TO " + Obj.get("to_date").getAsString() + " : " + Obj.get("firstname").getAsString());*/
            List_file.add(Obj.get("employee_name").getAsString()+" : " + Obj.get("leave_count")+" Days");
        }


        //Create an adapter for the listView and add the ArrayList to the adapter.
        list.setAdapter(new ArrayAdapter<String>(GetLeaveRequestActivity.this, android.R.layout.simple_list_item_1, List_file));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //position is the listViews Selected index

                JsonElement jsonElement = dataAsJsonArray.get(position);

                JsonObject data = jsonElement.getAsJsonObject();


                Intent intent = new Intent(GetLeaveRequestActivity.this, LeaveRequestDetailsActivity.class);

                intent.putExtra("NAME", data.get("employee_name").getAsString());
                intent.putExtra("FROMDATE", data.get("from_date").getAsString());
                intent.putExtra("TODATE", data.get("to_date").getAsString());
                intent.putExtra("LEAVECOUNT", data.get("leave_count").getAsString());
                intent.putExtra("REASON", data.get("reason").getAsString());
                intent.putExtra("LEAVEID", data.get("leave_id").getAsString());
finish();

                startActivity(intent);

            }
        });
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
