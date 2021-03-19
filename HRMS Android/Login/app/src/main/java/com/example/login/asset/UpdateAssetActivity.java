package com.example.login.asset;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.util.Dailog;
import com.google.gson.Gson;
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

public class UpdateAssetActivity extends AppCompatActivity {
    private static final String TAG = "UpdateAssetActivity";
    private Spinner assetCategorySpinner, statusSpinner;
    private TextView textViewCategory, textStatus;
    private EditText manufacturer, model, serial;
    private Button submit;
    private JsonArray dataAsJsonArray;
    String categoryid = "", status = "";
    boolean first = true, second = true;
    ProgressDialog progressDialog;
    NodeJS api;
    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_asset);

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        new GetCategoryListAsync().execute("");
        assetCategorySpinner = findViewById(R.id.assetCategorySpinner);
        textViewCategory = findViewById(R.id.textViewCategory);
        manufacturer = findViewById(R.id.manufacturer);
        model = findViewById(R.id.model);
        serial = findViewById(R.id.serial);
        submit = findViewById(R.id.submit);
        statusSpinner = findViewById(R.id.statusSpinner);
        textStatus = findViewById(R.id.textStatus);

        if (getIntent().getStringExtra("serial").equals("")) {
            assetCategorySpinner.setVisibility(View.INVISIBLE);
            textViewCategory.setVisibility(View.INVISIBLE);
            statusSpinner.setVisibility(View.INVISIBLE);
            textStatus.setVisibility(View.INVISIBLE);

            manufacturer.setEnabled(false);
            model.setEnabled(false);


        }

        manufacturer.setText(getIntent().getStringExtra("manufacturer"));
        model.setText(getIntent().getStringExtra("model"));
        serial.setText(getIntent().getStringExtra("serial"));


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (getIntent().getStringExtra("serial").equals("")) {

                    if (serial.getText().toString().equals("")) {

                        serial.setError("Enter Serial No.");
                        serial.requestFocus();
                    } else {
                        JsonObject request = new JsonObject();
                        request.addProperty("asset_id", getIntent().getStringExtra("asset_id"));
                        request.addProperty("asset_serial", serial.getText().toString());
                        new AddSerialAsync().execute(request);
                    }
                } else {
                    if (categoryid.equals("")) {

                        Toast.makeText(UpdateAssetActivity.this, "Please Select Category.", Toast.LENGTH_SHORT).show();
                    } else if (manufacturer.getText().toString().equals("")) {
                        manufacturer.setError("Enter Manufacturer Name");
                        manufacturer.requestFocus();
                    } else if (model.getText().toString().equals("")) {
                        model.requestFocus();
                        model.setError("Enter Model No.");
                    } else if (serial.getText().toString().equals("")) {

                        serial.setError("Enter Serial No.");
                        serial.requestFocus();
                    } else if (status.equals("")) {


                        Toast.makeText(UpdateAssetActivity.this, "Please Select Status.", Toast.LENGTH_SHORT).show();
                    } else {

                        JsonObject request = new JsonObject();
                        request.addProperty("asset_id", getIntent().getStringExtra("asset_id"));
                        request.addProperty("manufacturer", manufacturer.getText().toString());
                        request.addProperty("model", model.getText().toString());
                        request.addProperty("categoryid", categoryid);


                        //  JsonArray array = new Gson().toJsonTree(serialList).getAsJsonArray();
                        JsonArray array = new JsonArray();
                        JsonObject serialJson = new JsonObject();
                        serialJson.addProperty("count_id", getIntent().getStringExtra("count_id"));
                        serialJson.addProperty("asset_id", getIntent().getStringExtra("asset_id"));
                        serialJson.addProperty("asset_serial", serial.getText().toString());
                        serialJson.addProperty("status", status);


                        array.add(serialJson);

                        request.add("serial", array);
                        new UpdateAssetAsync().execute(request);


                    }

                }


            }
        });

        int index = getIntent().getStringExtra("status").equals("available")?1:2;
        statusSpinner.setSelection(index);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (second) {
                    second = false;
                } else {
                    if (position == 0) {
                        Toast.makeText(UpdateAssetActivity.this, "Please select Status.", Toast.LENGTH_SHORT).show();

                        status = "";
                    } else {

                        status = parent.getItemAtPosition(position).toString().toLowerCase();


                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    class AddSerialAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateAssetActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... a) {

            compositeDisposable.add(api.addSerial(a[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");

                                           Toast.makeText(UpdateAssetActivity.this, "Serial No. Added.", Toast.LENGTH_SHORT).show();
                                           Intent intent = new Intent(UpdateAssetActivity.this, Asset.class);
                                           finish();
                                           startActivity(intent);


                                       } else {

                                           Dailog.DialogForServiceDown(UpdateAssetActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(UpdateAssetActivity.this).show();
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

    class GetCategoryListAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateAssetActivity.this,
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

    class UpdateAssetAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateAssetActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... a) {

            compositeDisposable.add(api.updateAsset(a[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");

                                           Toast.makeText(UpdateAssetActivity.this, "Asset Updated.", Toast.LENGTH_SHORT).show();
                                           Intent intent = new Intent(UpdateAssetActivity.this, Asset.class);
                                           finish();
                                           startActivity(intent);


                                       } else {

                                           Dailog.DialogForServiceDown(UpdateAssetActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(UpdateAssetActivity.this).show();
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

                                       Dailog.DialogForServiceDown(UpdateAssetActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   Dailog.DialogForServiceDown(UpdateAssetActivity.this).show();
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
        JsonArray dataArray = data.getAsJsonArray("data");
        String category_name = "";
        for (JsonElement element : dataArray) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.get("category_id").getAsString().equals(getIntent().getStringExtra("categoryid"))) {
                category_name = obj.get("category_name").getAsString();
                categoryid = getIntent().getStringExtra("categoryid");
                break;
            }


        }

        assetCategorySpinner.setSelection(dataAdapter.getPosition(category_name));
        assetCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);

                if (first) {
                    first = false;
                } else {

                    if (position == 0) {
                        Toast.makeText(UpdateAssetActivity.this, "Please select Category ", Toast.LENGTH_SHORT).show();

                        categoryid = "";
                    } else {
                        position--;
                        JsonElement data = dataAsJsonArray.get(position);

                        JsonObject dataObj = data.getAsJsonObject();
                        categoryid = dataObj.get("category_id").getAsString();

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
