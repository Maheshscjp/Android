package com.example.login.asset;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
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
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class Asset extends AppCompatActivity {
    private static final String TAG = "Asset";
    private FloatingActionButton addAsset;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ProgressDialog progressDialog;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Spinner assetCategorySpinner;
    NodeJS api;
    CompositeDisposable compositeDisposable;
    boolean first = true;
    JsonArray dataAsJsonArray;
    String category_id = "";
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        expListView = (ExpandableListView) findViewById(R.id.ExpList);
        assetCategorySpinner = findViewById(R.id.assetCategorySpinner);
        new GetCategoryListAsync().execute("");


        addAsset = findViewById(R.id.addAsset);
        addAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Asset.this, AddAssetActivity.class);
                startActivity(intent);
            }
        });


    }


    private void prepareListData(JsonObject jsonElements) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        List<List<String>> listOfList = new ArrayList<List<String>>();
        Set<String> hash_Set = new HashSet<String>();    //It is an unordered collection of objects in which duplicate values cannot be stored.
        JsonArray data = jsonElements.getAsJsonArray("data");
        for (JsonElement ob : data) {
            JsonObject obj = ob.getAsJsonObject();
            // Log.e(TAG, "prepareListData: data :: "+obj.toString() );
            hash_Set.add(obj.get("asset_id").getAsString());

        }
        Log.e(TAG, "prepareListData: set size ::" + hash_Set.size());
        Log.e(TAG, "prepareListData: hash set " + hash_Set.toString());
        for (String asset_id : hash_Set) {
            for (JsonElement oo : data) {
                JsonObject obj = oo.getAsJsonObject();
                if (obj.get("asset_id").getAsString().equals(asset_id)) {

                    listDataHeader.add(++count+". "+obj.get("asset_manufacturer").getAsString() + " : " + obj.get("asset_model").getAsString());
                    break;

                }

            }
        }
        Log.e(TAG, "prepareListData: listDataHeader size::" + listDataHeader.size());

        for (String asset_id : hash_Set) {
           List<String> childData = new ArrayList<String>();
            childData.add("Add New Serial No.");
            for (JsonElement oo : data) {
                JsonObject obj = oo.getAsJsonObject();

                if (obj.get("asset_id").getAsString().equals(asset_id)) {

                    childData.add(obj.get("asset_serial").getAsString());


                }


            }

            listOfList.add(childData);
            Log.e(TAG, "prepareListData: size ::"+listOfList.size());
        }





        for(int i = 0 ; i < listDataHeader.size() ; i++  ){

            Log.e(TAG, "prepareListData: "+i +" : "+listDataHeader.get(i) +" :: " + listOfList.get(i).toString());

            listDataChild.put(listDataHeader.get(i), listOfList.get(i));


        }
        Log.e(TAG, "prepareListData: listDataChild :: "+listDataChild.size());





    }

    class GetCategoryListAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Asset.this,
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


    class getAllAssetAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Asset.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... a) {
            compositeDisposable.add(api.getAllAsset(a[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           prepareListData(jsonElements);

                                           final JsonObject Jsondata = jsonElements;


                                           listAdapter = new ExpandableListAdapter(Asset.this, listDataHeader, listDataChild);

                                           // setting list adapter
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

                                                   JsonArray data = Jsondata.getAsJsonArray("data");
                                                   if (childPosition == 0){

                                                       Intent intent = new Intent(Asset.this,UpdateAssetActivity.class);

                                                       String [] manu_model = listDataHeader.get(groupPosition).split("[.:]+");


                                                       for(JsonElement element : data){

                                                           JsonObject obj = element.getAsJsonObject();

                                                           if((obj.get("asset_manufacturer").getAsString().equals(manu_model[1].trim())) && (obj.get("asset_model").getAsString().equals(manu_model[2].trim())) )
                                                           {

                                                               intent.putExtra("asset_id",obj.get("asset_id").getAsString());
                                                               break;
                                                           }
                                                       }


                                                       intent.putExtra("manufacturer",manu_model[1].trim());
                                                       intent.putExtra("model",manu_model[2].trim());
                                                       intent.putExtra("serial","");

                                                       startActivity(intent);

                                                   }

                                                   else{
                                                       Intent intent = new Intent(Asset.this,UpdateAssetActivity.class);
                                                       String [] manu_model = listDataHeader.get(groupPosition).split("[.:]+");

                                                       for(JsonElement element : data){
                                                           JsonObject obj = element.getAsJsonObject();
                                                           if((obj.get("asset_manufacturer").getAsString().equals(manu_model[1].trim())) && (obj.get("asset_model").getAsString().equals(manu_model[2].trim()))
                                                           && (obj.get("asset_serial").getAsString().equals(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition))))
                                                           {
                                                               intent.putExtra("asset_id",obj.get("asset_id").getAsString());
                                                               intent.putExtra("categoryid",obj.get("category_id").getAsString());
                                                               intent.putExtra("count_id",obj.get("count_id").getAsString());
                                                               intent.putExtra("status",obj.get("status").getAsString());

                                                               break;
                                                           }
                                                       }



                                                       intent.putExtra("manufacturer",manu_model[1].trim());
                                                       intent.putExtra("model",manu_model[2].trim());
                                                       intent.putExtra("serial", listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));

                                                       startActivity(intent);


                                                   }



                                                   return false;
                                               }
                                           });

                                       }
                                       else if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("data not found")){
                                           Toast.makeText(Asset.this,"Asset is not available.",Toast.LENGTH_SHORT).show();
                                       }
                                       else {

                                           Dailog.DialogForServiceDown(Asset.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(Asset.this).show();
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

                                       Dailog.DialogForServiceDown(Asset.this).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                   Dailog.DialogForServiceDown(Asset.this).show();
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
                        Toast.makeText(Asset.this, "Please select user ", Toast.LENGTH_SHORT).show();
                        // userType = "";
                        //  scrollView.setVisibility(View.INVISIBLE);
                        //submit_Update.setVisibility(View.INVISIBLE);
                        category_id = "";
                    } else {
                        position--;
                        JsonElement data = dataAsJsonArray.get(position);

                        JsonObject dataObj = data.getAsJsonObject();
                        category_id = dataObj.get("category_id").getAsString();
                        JsonObject req = new JsonObject();
                        req.addProperty("categoryid", category_id);

                        new getAllAssetAsync().execute(req);


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

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
