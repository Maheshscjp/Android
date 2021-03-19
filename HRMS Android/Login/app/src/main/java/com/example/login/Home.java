package com.example.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
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

public class Home extends AppCompatActivity {
    private static final String TAG = "Home";
    AnyChartView anyChartView;
    String[] status={"Completed","Pending","In Progress"};
    int[] statusCount={20,56,20};
    NodeJS api;
    CompositeDisposable compositeDisposable;
    User LoggedInUser;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        JsonObject Json = new JsonObject();
        Json.addProperty("user_id", LoggedInUser.getUserid());

        Log.d(TAG, "onCreate: request :: " + Json.toString());
        new GetStatusCount().execute(Json);
        anyChartView = findViewById(R.id.chart);
        setupPieChart();
    }

    public void setupPieChart() {
        Pie pie= AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0;i<status.length;i++){
            dataEntries.add(new ValueDataEntry(status[i],statusCount[i]));
        }
        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }



    class GetStatusCount extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Home.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            getstatusCount(jsonObjects[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void getstatusCount(JsonObject jsonObject) {

        Log.d(TAG, jsonObject.toString());
        compositeDisposable.add(api.getstatusCount(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: response :: " + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       dataAsJsonArray = jsonElements.getAsJsonArray("data");
                                       for (JsonElement statusjson : dataAsJsonArray) {
                                           JsonObject Obj = statusjson.getAsJsonObject();
                                           for(int i=0;i<3;i++){
                                               status[i]= new String(Obj.get("status").toString());
                                               statusCount[i]= new Integer(Obj.get("count").getAsInt());
                                               
                                           }

                                       }

                                       Log.d(TAG, "Array: response :: " + status +""+statusCount);
                                           Pie pie= AnyChart.pie();
                                           List<DataEntry> dataEntries = new ArrayList<>();
                                          for (int i=0;i<status.length;i++){
                                             dataEntries.add(new ValueDataEntry(status[i],statusCount[i]));
                                          }
                                           pie.data(dataEntries);
                                           anyChartView.setChart(pie);



                                       Log.d(TAG, "accept:  Service Call Successful..");
                                   } else {

                                       Log.d(TAG, "accept:  Service Call Successful..Error.........");
                                       DialogForServiceDown(Home.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                   DialogForServiceDown(Home.this).show();
                               }
                           }


                )
        );

    } public AlertDialog.Builder DialogForServiceDown(Context c) {

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