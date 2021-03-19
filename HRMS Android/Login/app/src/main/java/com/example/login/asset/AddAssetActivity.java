package com.example.login.asset;

import androidx.appcompat.app.AppCompatActivity;


import com.example.login.NodeJS;
import com.example.login.RetroFitClient;

import com.example.login.util.Dailog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;
import com.example.login.ui.timesheet.ActivitySubmitTimesheet;
import com.example.login.util.InputValidation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class AddAssetActivity extends AppCompatActivity {
    private static final String TAG = "AddAssetActivity";
    private Button addRow, submit;
    String category_id = "";
    boolean first = true;
    NodeJS api;
    CompositeDisposable compositeDisposable;
    private Spinner assetCategorySpinner;
    EditText manufacturer, model;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_asset);
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        new GetCategoryListAsync().execute("");

        assetCategorySpinner = findViewById(R.id.assetCategorySpinner);
        manufacturer = findViewById(R.id.manufacturer);
        model = findViewById(R.id.model);
        addRow = (Button) findViewById(R.id.addRow);
        addRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTableRow();
            }
        });

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (category_id.equals("")) {
                    Toast.makeText(AddAssetActivity.this, "Please select category.", Toast.LENGTH_SHORT).show();
                } else if (manufacturer.getText().toString().equals("")) {
                    manufacturer.setError("Enter manufacturer");
                    manufacturer.setFocusable(true);
                } else if (model.getText().toString().equals("")) {
                    model.setError("Enter model");
                    model.setFocusable(true);
                } else {
                    List<String> serialList = new ArrayList<>();
                    TableLayout t1 = (TableLayout) findViewById(R.id.tableabl);
                    if (t1.getChildCount() == 1) {
                        Toast.makeText(AddAssetActivity.this, "Please Add Serial no.", Toast.LENGTH_SHORT).show();
                    } else if (t1.getChildCount() > 1) {
                        TableRow row1 = (TableRow) t1.getChildAt(t1.getChildCount() - 1);
                        EditText serial1 = (EditText) row1.getChildAt(1);
                        if (serial1.getText().toString().equals("")) {
                            serial1.setError("Enter Serial No.");
                            serial1.setFocusable(true);
                        } else {
                            for (int j = 1; j < t1.getChildCount(); j++) {
                                TableRow row = (TableRow) t1.getChildAt(j);
                                EditText serial = (EditText) row.getChildAt(1);

                                serialList.add(serial.getText().toString().toUpperCase());


                            }


                            JsonArray array = new Gson().toJsonTree(serialList).getAsJsonArray();


                            JsonObject request = new JsonObject();
                            request.addProperty("manufacturer", manufacturer.getText().toString().toUpperCase());
                            request.addProperty("model", model.getText().toString().toUpperCase());
                            request.addProperty("categoryid", category_id);
                            request.add("serial", array);
                            Log.e(TAG, "onClick: request ::" + request);
                            new SubitAssetAsync().execute(request);

                        }


                    }

                }
            }
        });


    }


    private void addTableRow() {
        TableLayout t1;
        TableRow tr;
        EditText ed1;
        TextView tv1;
        String seriaText = null;
        EditText serial;
        boolean flag = true;
        t1 = (TableLayout) findViewById(R.id.tableabl);
        Log.d("TAG", "addTableRow: " + Integer.toString(t1.getChildCount()));

        if (t1.getChildCount() != 0) {

            if (t1.getChildCount() == 1) {
                flag = true;
            } else {

                TableRow row = (TableRow) t1.getChildAt(t1.getChildCount() - 1);

                serial = (EditText) row.getChildAt(1); // get child index on particular row
                seriaText = serial.getText().toString();
                if ("".equals(seriaText)) {

                    serial.setError("Enter Serial No.");
                    serial.setFocusable(true);
                    flag = false;

                } else {
                    flag = true;
                }
            }

        }


        if (flag) {

  /*  for (int i = 0; i < t1.getChildCount(); i++) {
        t1.setColumnStretchable(i, true);
    }*/


            tr = new TableRow(this);
            tv1 = new TextView(this);
            ed1 = new EditText(this);


            InputFilter[] inputFilters = new InputFilter[1];
            inputFilters[0] = new InputFilter.LengthFilter(4);

            tv1.setText(Integer.toString(t1.getChildCount()));
            tv1.setTextSize(15);
            tv1.setGravity(Gravity.CENTER);
            tv1.setFreezesText(true);
            tv1.setTag(null);


            ed1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed1.setFilters(InputValidation.inputFilterAllowAlphaNumCaps);


            tr.addView(tv1);

            tr.addView(ed1);


            t1.addView(tr);


        }

    }


    class GetCategoryListAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddAssetActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(String... a) {

            getCategoryList();
            // getEmployeeDetails(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }


    class SubitAssetAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddAssetActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... a) {

            compositeDisposable.add(api.submitAsset(a[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean()) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           Toast.makeText(AddAssetActivity.this, "Asset Submitted Successfully.",Toast.LENGTH_SHORT).show();
                                           Intent intent = new Intent(AddAssetActivity.this, Asset.class);
                                           finish();
                                           startActivity(intent);
                                       }
                                       else if (!jsonElements.get("success").getAsBoolean() &&jsonElements.get("message").getAsString().equals("manufacturer and model should be unique") ){

                                           Toast.makeText(AddAssetActivity.this, "Manufacturer And Model is already exist.", Toast.LENGTH_SHORT).show();
                                       }
                                       else {

                                           Dailog.DialogForServiceDown(AddAssetActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(AddAssetActivity.this).show();
                                   }
                               }


                    )
            );
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void getCategoryList() {

        compositeDisposable.add(api.getAssetCategory().subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d(TAG, "accept:  Service Call Successful..");
                                       addItemsOnSpinner(jsonElements);
                                   } else {

                                       Dailog.DialogForServiceDown(AddAssetActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   Dailog.DialogForServiceDown(AddAssetActivity.this).show();
                               }
                           }


                )
        );


    }


    public void addItemsOnSpinner(JsonObject data) {
        final List<String> list = new ArrayList<String>();
        list.add(0, "select");
        dataAsJsonArray = data.getAsJsonArray("data");
        Log.d(TAG, "dataAsJsonArray" + dataAsJsonArray);
        for (JsonElement category : dataAsJsonArray) {
            JsonObject Obj = category.getAsJsonObject();

            list.add(Obj.get("category_name").getAsString());


        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assetCategorySpinner.setAdapter(dataAdapter);
        assetCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);

                if (first) {
                    first = false;
                } else {

                    if (position == 0) {
                        Toast.makeText(AddAssetActivity.this, "Please select Category. ", Toast.LENGTH_SHORT).show();
                        // userType = "";
                        //  scrollView.setVisibility(View.INVISIBLE);
                        //submit_Update.setVisibility(View.INVISIBLE);
                        category_id = "";
                    } else {
                        position--;
                        JsonElement data = dataAsJsonArray.get(position);

                        JsonObject dataObj = data.getAsJsonObject();
                        category_id = dataObj.get("category_id").getAsString();

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
