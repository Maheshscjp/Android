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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.util.Dailog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AssignAssetToUserActivity extends AppCompatActivity {
    private static final String TAG = "AssignAssetToUser";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    Spinner spinnerCategory, spinnerManufacturer, spinnerModel, spinnerSerial;
    TextView manufacturerText, modelText, serialText;
    Button submit;
    JsonArray dataAsJsonArray;
    boolean first = true;
    String category_id = "", manufacturer = "", model = "", serial = "";


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_asset_to_user);

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerManufacturer = findViewById(R.id.spinnerManufacturer);
        spinnerModel = findViewById(R.id.spinnerModel);
        spinnerSerial = findViewById(R.id.spinnerSerial);
        manufacturerText = findViewById(R.id.manufacturerText);
        modelText = findViewById(R.id.modelText);
        serialText = findViewById(R.id.serialText);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (category_id.equals("")){
                    Toast.makeText(AssignAssetToUserActivity.this, "Please select category.", Toast.LENGTH_SHORT).show();
                }
                else  if (manufacturer.equals("")){
                    Toast.makeText(AssignAssetToUserActivity.this, "Please select Manufacturer.", Toast.LENGTH_SHORT).show();
                }
                else  if (model.equals("")){
                    Toast.makeText(AssignAssetToUserActivity.this, "Please select Model.", Toast.LENGTH_SHORT).show();
                }
                else  if (serial.equals("")){
                    Toast.makeText(AssignAssetToUserActivity.this, "Please select Serial.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String count_id = "";
                    for (JsonElement element : dataAsJsonArray) {
                        JsonObject jsonObject = element.getAsJsonObject();
                        if (jsonObject.get("asset_serial").getAsString().equals(serial)) {
                            count_id = jsonObject.get("count_id").getAsString();

                        }


                    }

                    JsonObject req = new JsonObject();
                    req.addProperty("user_id", getIntent().getStringExtra("user_id"));
                    req.addProperty("count_id", count_id);

                    new assignAssetAsync().execute(req);

                }

            }
        });


        new GetCategoryListAsync().execute("");
    }

    class GetCategoryListAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AssignAssetToUserActivity.this,
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

    class assignAssetAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AssignAssetToUserActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... a) {
            compositeDisposable.add(api.assignAsset(a[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           Intent intent = new Intent(AssignAssetToUserActivity.this, AssetWithUserActivity.class);
                                           finish();
                                           startActivity(intent);

                                       } else if ((!jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("error while inserting data"))) {

                                           Toast.makeText(AssignAssetToUserActivity.this, "Please select appropriate data.", Toast.LENGTH_SHORT).show();


                                       } else {

                                           Dailog.DialogForServiceDown(AssignAssetToUserActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(AssignAssetToUserActivity.this).show();
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


    class GetAllAvailableAssetAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AssignAssetToUserActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... a) {

            compositeDisposable.add(api.getAllAvailableAsset(a[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           spinnerManufacturer.setAdapter(null);
                                           addItemsOnManufacturerSpinner(jsonElements);
                                       } else if ((jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("data not found"))) {

                                           Toast.makeText(AssignAssetToUserActivity.this, "Assets not available.", Toast.LENGTH_SHORT).show();

                                           spinnerManufacturer.setAdapter(null);
                                           spinnerModel.setAdapter(null);
                                           spinnerSerial.setAdapter(null);
                                       } else {

                                           Dailog.DialogForServiceDown(AssignAssetToUserActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(AssignAssetToUserActivity.this).show();
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

                                       Dailog.DialogForServiceDown(AssignAssetToUserActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   Dailog.DialogForServiceDown(AssignAssetToUserActivity.this).show();
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
        spinnerCategory.setAdapter(dataAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);

                if (first) {
                    first = false;
                } else {

                    if (position == 0) {
                        Toast.makeText(AssignAssetToUserActivity.this, "Please select Category. ", Toast.LENGTH_SHORT).show();

                        category_id = "";
                    } else {
                        position--;
                        JsonElement data = dataAsJsonArray.get(position);

                        JsonObject dataObj = data.getAsJsonObject();
                        category_id = dataObj.get("category_id").getAsString();
                        JsonObject req = new JsonObject();
                        req.addProperty("categoryid", category_id);
                        new GetAllAvailableAssetAsync().execute(req);


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void addItemsOnManufacturerSpinner(JsonObject data) {
        final List<String> list = new ArrayList<String>();
        list.add(0, "select");
        Set<String> hash_Set = new HashSet<String>();
        dataAsJsonArray = data.getAsJsonArray("data");
        Log.d(TAG, "dataAsJsonArray" + dataAsJsonArray);


        for (JsonElement category : dataAsJsonArray) {
            JsonObject Obj = category.getAsJsonObject();

            hash_Set.add(Obj.get("asset_manufacturer").getAsString());


        }

        for (String asset_manufacturer : hash_Set) {
            list.add(asset_manufacturer);
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerManufacturer.setAdapter(dataAdapter);
        spinnerManufacturer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);


                if (position == 0) {
                    Toast.makeText(AssignAssetToUserActivity.this, "Please select Manufacturer.", Toast.LENGTH_SHORT).show();

                    manufacturer = "";
                } else {

                    manufacturer = parent.getSelectedItem().toString();
                    addItemsOnModelSpinner(dataAsJsonArray, manufacturer);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void addItemsOnModelSpinner(JsonArray data, String asset_manufact) {
        final List<String> list = new ArrayList<String>();
        list.add(0, "select");
        Set<String> hash_Set = new HashSet<String>();

        Log.d(TAG, "dataAsJsonArray" + data);

        final JsonArray dataArray = data;
        for (JsonElement category : data) {
            JsonObject Obj = category.getAsJsonObject();

            if (Obj.get("asset_manufacturer").getAsString().equals(asset_manufact)) {
               // list.add(Obj.get("asset_model").getAsString());
                hash_Set.add(Obj.get("asset_model").getAsString());

            }

        }
        for (String asset_model : hash_Set) {
            list.add(asset_model);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModel.setAdapter(dataAdapter);
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);


                if (position == 0) {
                    Toast.makeText(AssignAssetToUserActivity.this, "Please select Model.", Toast.LENGTH_SHORT).show();
                    model = "";

                } else {
                    model = parent.getSelectedItem().toString();

                    addItemsOnSerialSpinner(dataArray, model);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void addItemsOnSerialSpinner(JsonArray data, String asset_model) {
        final List<String> list = new ArrayList<String>();
        list.add(0, "select");
        Set<String> hash_Set = new HashSet<String>();

        Log.d(TAG, "dataAsJsonArray" + data);


        for (JsonElement category : data) {
            JsonObject Obj = category.getAsJsonObject();

            if (Obj.get("asset_model").getAsString().equals(asset_model)) {
                list.add(Obj.get("asset_serial").getAsString());
            }


        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSerial.setAdapter(dataAdapter);
        spinnerSerial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);


                if (position == 0) {
                    Toast.makeText(AssignAssetToUserActivity.this, "Please select Model.", Toast.LENGTH_SHORT).show();
                    serial = "";

                } else {
                    serial = parent.getSelectedItem().toString();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
