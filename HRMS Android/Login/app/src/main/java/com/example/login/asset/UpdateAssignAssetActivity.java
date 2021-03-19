package com.example.login.asset;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.util.Dailog;
import com.google.gson.JsonObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class UpdateAssignAssetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "UpdateAssignAsset";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    private String active="", status="";
    private TextView manufacturer, model, serial;
    private Spinner isActiveSpiner, statusSpiner;
    private Button submit;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_assign_asset);

        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        manufacturer = findViewById(R.id.manufacturer);
        manufacturer.setText(getIntent().getStringExtra("man"));
        model = findViewById(R.id.model);
        model.setText(getIntent().getStringExtra("model"));
        serial= findViewById(R.id.serial);
        serial.setText(getIntent().getStringExtra("serial"));
        isActiveSpiner = findViewById(R.id.isActive);
        isActiveSpiner.setOnItemSelectedListener(this);
        statusSpiner = findViewById(R.id.status);
        statusSpiner.setOnItemSelectedListener(this);
        submit= findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               if (active.equals("")){
                   Toast.makeText(UpdateAssignAssetActivity.this, "Please select Is Active Status.", Toast.LENGTH_SHORT).show();
               }

               else if (status.equals("")){
                   Toast.makeText(UpdateAssignAssetActivity.this, "Please select Status.", Toast.LENGTH_SHORT).show();
               }

               else{
                   JsonObject req = new JsonObject();
                   req.addProperty("assign_id",getIntent().getStringExtra("assign_id"));
                   req.addProperty("count_id",getIntent().getStringExtra("count_id"));
                   req.addProperty("is_active",active);
                   req.addProperty("status",status);
                   new UpdateAssignedAssetToUserAsync().execute(req);

               }
            }
        });






    }

    class UpdateAssignedAssetToUserAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(UpdateAssignAssetActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... a) {
            compositeDisposable.add(api.updateAssignedAssetToUser(a[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           Toast.makeText(UpdateAssignAssetActivity.this, "Assigned Asset Updated.", Toast.LENGTH_SHORT).show();

                                           Intent intent = new Intent(UpdateAssignAssetActivity.this, AssetWithUserActivity.class);
                                           finish();
                                           startActivity(intent);

                                       } else if ((!jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("error while updating data"))) {

                                           Toast.makeText(UpdateAssignAssetActivity.this, "Please select appropriate data.", Toast.LENGTH_SHORT).show();


                                       } else {

                                           Dailog.DialogForServiceDown(UpdateAssignAssetActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(UpdateAssignAssetActivity.this).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       switch(parent.getId())
       {
           case R.id.isActive:
               if (position == 0)
               {
                   Toast.makeText(this, "Please select Is Active Status.", Toast.LENGTH_SHORT).show();
                   active="";
               }
               else
               {
                   if(parent.getSelectedItem().toString().equals("Yes")){
                       active="Y";
                   }
                  else{
                      active="N";
                   }
               }

               break;

           case R.id.status:
               if (position == 0)
               {
                   Toast.makeText(this, "Please select Status.", Toast.LENGTH_SHORT).show();
                   status="";
               }
               else
               {
                   status=parent.getSelectedItem().toString().toLowerCase();
               }
               break;

           default:
               break;

       }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
