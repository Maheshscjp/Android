package com.example.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.login.util.InputValidation;
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

public class DeleteUserActivity extends AppCompatActivity {
    private static final String TAG = "DeleteUserActivity";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    Button del_user;
    Spinner spinnerDeleteUSer;
    EditText reason;
    JsonArray dataAsJsonArray;
    String UserId = "";

    boolean first = true;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        //from activity to fragment on back button click
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        del_user = findViewById(R.id.del_user);
        spinnerDeleteUSer = findViewById(R.id.spinnerDeleteUSer);
        reason = findViewById(R.id.reason);
        reason.setFilters(InputValidation.inputFilterAllowAlpha);
        reason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus == true){
                    if(UserId.isEmpty()){
                        Toast.makeText(DeleteUserActivity.this, "Please Select User", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        JsonObject Json = new JsonObject();
        Json.addProperty("requiredUser", "Employee','Manager");


        new GetEmployeeListAsync().execute(Json);

        del_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject Json = new JsonObject();
                Json.addProperty("userId", UserId);
                Json.addProperty("reason", reason.getText().toString());
                if (TextUtils.isEmpty(Json.get("userId").getAsString())) {

                    Toast.makeText(DeleteUserActivity.this, "Please Select User", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new DeleteUserAsync().execute(Json);
                }

            }
        });
    }

    class GetEmployeeListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(DeleteUserActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            getEmployeeList(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }


    class DeleteUserAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(DeleteUserActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            deleteUser(jsonObjects[0]);
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

                                       DialogForServiceDown(DeleteUserActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(DeleteUserActivity.this).show();
                               }
                           }


                )
        );


    }

    public void deleteUser(JsonObject Json) {

        compositeDisposable.add(api.deleteUser(Json).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {

                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       Toast.makeText(DeleteUserActivity.this, "User Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                       finish();
                                   } else {
                                       DialogForServiceDown(DeleteUserActivity.this).show();
                                   }

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..");
                                   DialogForServiceDown(DeleteUserActivity.this).show();
                               }
                           }


                )
        );


    }


    public void addItemsOnSpinner(JsonObject data) {
        List<String> list = new ArrayList<String>();
        list.add("select");
        dataAsJsonArray = data.getAsJsonArray("data");
        for (JsonElement TLList : dataAsJsonArray) {
            JsonObject Obj = TLList.getAsJsonObject();

            list.add(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeleteUSer.setAdapter(dataAdapter);
        spinnerDeleteUSer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first) {
                    first = false;
                } else {

                    if (position == 0) {
                        Toast.makeText(DeleteUserActivity.this, "Please select appropriate option!", Toast.LENGTH_SHORT).show();
                    } else {
                        position--;
                        JsonElement data = dataAsJsonArray.get(position);
                        JsonObject dataObj = data.getAsJsonObject();

                        UserId = dataObj.get("user_id").getAsString();

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
