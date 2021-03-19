package com.example.login.asset;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.UpdateEmployeeActivity;
import com.example.login.util.Dailog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AssetWithUserActivity extends AppCompatActivity {
    private static final String TAG = "AssetWithUserActivity";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    private Spinner userSpinner;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private ProgressDialog progressDialog;
    private int count = 0;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    JsonArray dataAsJsonArray;
    private boolean first = true;
    private String user_id = "";
    private FloatingActionButton AssignAsset;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_with_user);

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        userSpinner = findViewById(R.id.userSpinner);
        expListView = findViewById(R.id.ExpList);
        AssignAsset = findViewById(R.id.AssignAsset);
        AssignAsset.setVisibility(View.INVISIBLE);
        AssignAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AssetWithUserActivity.this, AssignAssetToUserActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);

            }
        });

        JsonObject Json = new JsonObject();
        Json.addProperty("requiredUser", "Employee','Manager");
        Log.d(TAG, "accept: Requets ::" + Json.toString());
        new GetEmployeeListAsync().execute(Json);

    }

    class GetEmployeeListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AssetWithUserActivity.this,
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

                                       Dailog.DialogForServiceDown(AssetWithUserActivity.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   Dailog.DialogForServiceDown(AssetWithUserActivity.this).show();
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
        for (JsonElement TLList : dataAsJsonArray) {
            JsonObject Obj = TLList.getAsJsonObject();

            list.add(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());


        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(dataAdapter);
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "position: Requets ::" + position);

                if (first) {
                    first = false;
                } else {


                    if (position == 0) {
                        Toast.makeText(AssetWithUserActivity.this, "Please select user.", Toast.LENGTH_SHORT).show();
                        user_id = "";

                    } else {
                        position--;
                        JsonElement data = dataAsJsonArray.get(position);

                        JsonObject dataObj = data.getAsJsonObject();
                        user_id = dataObj.get("user_id").getAsString();

                        JsonObject Json = new JsonObject();
                        Json.addProperty("user_id", user_id);

                        new GetAssignedAssetToUserAsync().execute(Json);


                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    class GetAssignedAssetToUserAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AssetWithUserActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            compositeDisposable.add(api.getAssignedAssetToUser(jsonObjects[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @SuppressLint("RestrictedApi")
                                   @Override
                                   public void accept(final JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           AssignAsset.setVisibility(View.VISIBLE);
                                           prepareListData(jsonElements);

                                           final JsonObject Jsondata = jsonElements;


                                           listAdapter = new ExpandableListAdapter(AssetWithUserActivity.this, listDataHeader, listDataChild);


                                           expListView.setAdapter(listAdapter);

                                           expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                                               @Override
                                               public boolean onChildClick(ExpandableListView parent, View v,
                                                                           int groupPosition, int childPosition, long id) {
                                                 /*  Toast.makeText(
                                                          Asset.this,
                                                           listDataHeader.get(groupPosition)
                                                                   + " : "
                                                                   + listDataChild.get(
                                                                   listDataHeader.get(groupPosition)).get(
                                                                   childPosition), Toast.LENGTH_SHORT)
                                                           .show();*/
                                                   JsonArray data = jsonElements.getAsJsonArray("data");
                                                   String[] man_model_serial =  listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).split(":");
                                                   String man = man_model_serial[0].trim();
                                                   String model = man_model_serial[1].trim();
                                                   String serial = man_model_serial[2].trim();

                                                   Intent intent = new Intent(AssetWithUserActivity.this, UpdateAssignAssetActivity.class);
                                                    intent.putExtra("man",man);
                                                    intent.putExtra("model",model);
                                                    intent.putExtra("serial",serial);

                                                   for ( JsonElement jsonElement : data) {
                                                       JsonObject jsonObject = jsonElement.getAsJsonObject();
                                                       if ((jsonObject.get("asset_serial").getAsString().equals(serial))
                                                               && (jsonObject.get("asset_manufacturer").getAsString().equals(man) )
                                                               &&(jsonObject.get("asset_model").getAsString().equals(model))){


                                                           intent.putExtra("count_id",jsonObject.get("count_id").getAsString());
                                                           intent.putExtra("assign_id",jsonObject.get("assign_id").getAsString());
                                                           break;

                                                       }

                                                   }


                                                   startActivity(intent);

                                                   return false;
                                               }
                                           });

                                       } else if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("data not found")) {
                                           AssignAsset.setVisibility(View.VISIBLE);
                                           Toast.makeText(AssetWithUserActivity.this, "No Assigned Asset.", Toast.LENGTH_SHORT).show();
                                       } else {

                                           Dailog.DialogForServiceDown(AssetWithUserActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(AssetWithUserActivity.this).show();
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

    private void prepareListData(JsonObject jsonElements) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        List<List<String>> listOfList = new ArrayList<List<String>>();
        Set<String> hash_Set = new HashSet<String>();    //It is an unordered collection of objects in which duplicate values cannot be stored.
        JsonArray data = jsonElements.getAsJsonArray("data");
        for (JsonElement ob : data) {
            JsonObject obj = ob.getAsJsonObject();

            hash_Set.add(obj.get("category_id").getAsString());

        }
        for (String category_id : hash_Set) {
            for (JsonElement oo : data) {
                JsonObject obj = oo.getAsJsonObject();
                if (obj.get("category_id").getAsString().equals(category_id)) {

                    // listDataHeader.add(++count + ". " + obj.get("asset_manufacturer").getAsString() + " : " + obj.get("asset_model").getAsString());
                    listDataHeader.add(++count + ". " + obj.get("category_name").getAsString());
                    break;

                }

            }
        }
        Log.e(TAG, "prepareListData: listDataHeader size::" + listDataHeader.size());

        for (String category_id : hash_Set) {
            List<String> childData = new ArrayList<String>();

            for (JsonElement oo : data) {
                JsonObject obj = oo.getAsJsonObject();

                if (obj.get("category_id").getAsString().equals(category_id)) {

                    childData.add(obj.get("asset_manufacturer").getAsString() + " : " + obj.get("asset_model").getAsString() + " : " + obj.get("asset_serial").getAsString());


                }


            }

            listOfList.add(childData);
            Log.e(TAG, "prepareListData: size ::" + listOfList.size());
        }


        for (int i = 0; i < listDataHeader.size(); i++) {

            Log.e(TAG, "prepareListData: " + i + " : " + listDataHeader.get(i) + " :: " + listOfList.get(i).toString());

            listDataChild.put(listDataHeader.get(i), listOfList.get(i));


        }
        Log.e(TAG, "prepareListData: listDataChild :: " + listDataChild.size());


    }


}
