package com.example.login.ui.timesheet;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.example.login.util.Dailog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TaskTrackActivity extends AppCompatActivity {
    private static final String TAG = "TaskTrackActivity";
    private ListView listview;
    NodeJS api;
    CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;
    private ArrayList<String> List_file;

    List<Task> taskList;
    Task task;
    ListView listView;

    private static final int REQUEST_CALL = 1;
    static final int CUSTOM_DIALOG_ID_PENDING = 0;
    static final int CUSTOM_DIALOG_ID_INPROGRESS = 0;
    static final int CUSTOM_DIALOG_ID_COMPLETED = 0;
    static final int CUSTOM_DIALOG_ID = 0;
    User LoggedInUser;

    TextView status, EmpName, ProductCode, ProductName, ProductQuantity, ProductAddress, TimeRequired, StartTime, EndTime, TaskCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_track);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        LoggedInUser = SharedPrefManager.getInstance(TaskTrackActivity.this).getUser();

        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        List_file = new ArrayList<String>();


        listview = findViewById(R.id.listview);
        taskList = new ArrayList<>();
        new GetTaskDetails().execute();

    }

    class GetTaskDetails extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TaskTrackActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            compositeDisposable.add(api.getAllTaskForTrack().subscribeOn(
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
                                           Dailog.DialogForServiceDown(TaskTrackActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                       Dailog.DialogForServiceDown(TaskTrackActivity.this).show();
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

    private void CreateListView(JsonObject jsonObject) throws JSONException {

        dataAsJsonArray = jsonObject.getAsJsonArray("data");
        for (JsonElement leaveList : dataAsJsonArray) {
            Task task = new Gson().fromJson(leaveList.toString(),Task.class);
            taskList.add(task);
        }

        TaskDetails_ListAdaptor adaptor = new TaskDetails_ListAdaptor(TaskTrackActivity.this, R.layout.list_adaptor_task_list, (ArrayList<Task>) taskList);

        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(TaskTrackActivity.this, "Task Selected..", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(TaskTrackActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_task_details, null);
                final JsonObject requestBody = new JsonObject();

                TextView tvUserName = (TextView) mView.findViewById(R.id.tvVal_user_name);
                TextView tvProdName = (TextView) mView.findViewById(R.id.tvVal_product_name);
                TextView tvProdCode = (TextView) mView.findViewById(R.id.tvVal_sku_code);
                TextView tvProdQty = (TextView) mView.findViewById(R.id.tvVal_product_quantity);
                TextView tvProdLoc = (TextView) mView.findViewById(R.id.tvVal_product_address);
                TextView tvTimeReq = (TextView) mView.findViewById(R.id.tvVal_time_required);
                TextView tvStatus = (TextView) mView.findViewById(R.id.tvVal_status);
                TextView tvStTime = (TextView) mView.findViewById(R.id.tvVal_start_time);
                TextView tvEndTime = (TextView) mView.findViewById(R.id.tvVal_end_time);

                Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
                ImageButton btnWhatsapp = (ImageButton) mView.findViewById(R.id.btnWhatsApp);
                ImageButton btnCalling = (ImageButton) mView.findViewById(R.id.btnCalling);

                Task task = taskList.get(position);
                tvUserName.setText(task.getFirstname()+" " +task.getLastname());
                tvProdName.setText(task.getProduct_name());
                tvProdCode.setText(task.getSku_code());
                tvProdQty.setText(task.getProduct_quantity());
                tvProdLoc.setText(task.getProduct_address());
                tvTimeReq.setText(task.getTime_required()+" hrs");
                tvStatus.setText(task.getStatus());
                tvStTime.setText(task.getStart_time());
                tvEndTime.setText(task.getEnd_time());

                final String contact = task.getContact();
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnWhatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TaskTrackActivity.this, "WhatsApp...." ,
                                Toast.LENGTH_SHORT).show();
//                        PackageManager pm=getPackageManager();
                        boolean isWhatsappInstalled = appInstalled("com.whatsapp");
                        if (isWhatsappInstalled){
                            Uri uri = Uri.parse("smsto:" + contact);
                            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                            intent.setPackage("com.whatsapp");
                            startActivity(intent);
//                            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"+91"+"7208590840"+"&text="+"hellow World.."));
                        }else{
                            Toast.makeText(TaskTrackActivity.this, "Whatsapp Is not Installed..." ,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnCalling.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TaskTrackActivity.this, "Calling Driver...." , Toast.LENGTH_SHORT).show();
                        makePhoneCall(contact);
                    }
                });

                dialog.show();
            }

        });

    }



    public boolean appInstalled(String url){

        PackageManager packageManager = getPackageManager();
        boolean isAppInstalled;
        try{
            packageManager.getPackageInfo(url,PackageManager.GET_ACTIVITIES);
            isAppInstalled = true;
        }
        catch (PackageManager.NameNotFoundException e){
            isAppInstalled = false;
        }

        return isAppInstalled;
    }

    private void makePhoneCall(String contact){
        String number = "+91 ".concat(contact);
        if (number.trim().length()>0){
            if(ContextCompat.checkSelfPermission(TaskTrackActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(TaskTrackActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            }
            else{
                String dial = "tel:"+number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{
            Toast.makeText(TaskTrackActivity.this,"Phone No. Not avaliable..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall(null);
            }
            else{
                Toast.makeText(TaskTrackActivity.this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            }
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

}
