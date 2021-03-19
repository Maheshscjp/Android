package com.example.login.ui.timesheet;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TaskListActivity extends AppCompatActivity {

    User loggedInUser;
    ListView timesheetListData;
    private List<String> listData;
    CompositeDisposable compositeDisposable;
    NodeJS api;
    ProgressDialog progressDialog;
    JsonArray taskDetailList = new JsonArray();
    List<Task> taskList =  new ArrayList<>();
    boolean isTaskInProcess=false;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_task_list);

        loggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        timesheetListData= (ListView) findViewById(R.id.listview);
        listData = new ArrayList<String>();

        //        getProjectData();
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("user_id", loggedInUser.getUserid());

        getTaskListFromDB(requestBody);

    }

    public void CreateListData(JsonArray jsonData){

        taskDetailList = jsonData;
        for (JsonElement leaveList : taskDetailList) {
            Task task = new Gson().fromJson(leaveList.toString(),Task.class);
            Log.d("List Value :: ", "Task Code Is : " +task.getTask_code()+ " Product Qty Is : "+ task.getProduct_quantity() +" Location Is : "+ task.getProduct_address());
            taskList.add(task);
        }

        TaskDetails_ListAdaptor adaptor = new TaskDetails_ListAdaptor(TaskListActivity.this, R.layout.list_adaptor_task_list, (ArrayList<Task>) taskList);

        timesheetListData = (ListView) findViewById(R.id.listview);
        timesheetListData.setAdapter(adaptor);
        timesheetListData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(TaskListActivity.this, "Task Selected..", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(TaskListActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_task_details, null);
                final JsonObject requestBody = new JsonObject();

                JsonObject jsonData = taskDetailList.get(position).getAsJsonObject();
                JSONObject obj = new JSONObject();
//                try {
//                    obj = new JSONObject(taskDetailList.get(position).toString());
//                    for (int i=0;i<obj.names().length();i++){
//                        String lable="tvVal_"+obj.names().getString(i);
//                        int res = mView.getResources().getIdentifier(lable, "id", getPackageName());
//                        if(res==0){
//                            Log.d("Lable:: ",lable);
//                            Log.d("UI id not exists :: ",obj.names().getString(i));
//                        }
//                        else {
//                            ((TextView) mView.findViewById(res)).setText(obj.get(obj.names().getString(i)).toString());
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                TextView tvUserName = (TextView) mView.findViewById(R.id.tvValUserName);
//                TextView tvProdName = (TextView) mView.findViewById(R.id.tvValProductName);
//                TextView tvProdCode = (TextView) mView.findViewById(R.id.tvValProductCode);
//                TextView tvProdQty = (TextView) mView.findViewById(R.id.tvValProductQty);
//                TextView tvProdLoc = (TextView) mView.findViewById(R.id.tvValProductLoc);
//                TextView tvTimeReq = (TextView) mView.findViewById(R.id.tvValTimeReq);
//                TextView tvStatus = (TextView) mView.findViewById(R.id.tvValStatus);
//                TextView tvStTime = (TextView) mView.findViewById(R.id.tvValStartTime);
//                TextView tvEndTime = (TextView) mView.findViewById(R.id.tvValEndTime);

//                int tag= mView.getResources().getIdentifier("tvValEndTime", "id", getPackageName());
//                ((TextView) mView.findViewById(tag)).setText("");

                Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
                ImageButton btnWhatsapp = (ImageButton) mView.findViewById(R.id.btnWhatsApp);
                ImageButton btnCalling = (ImageButton) mView.findViewById(R.id.btnCalling);

                Task task = taskList.get(position);

                TextView tvUserName = (TextView) mView.findViewById(R.id.tvVal_user_name);
                TextView tvProdName = (TextView) mView.findViewById(R.id.tvVal_product_name);
                TextView tvProdCode = (TextView) mView.findViewById(R.id.tvVal_sku_code);
                TextView tvProdQty = (TextView) mView.findViewById(R.id.tvVal_product_quantity);
                TextView tvProdLoc = (TextView) mView.findViewById(R.id.tvVal_product_address);
                TextView tvTimeReq = (TextView) mView.findViewById(R.id.tvVal_time_required);
                TextView tvStatus = (TextView) mView.findViewById(R.id.tvVal_status);
                TextView tvStTime = (TextView) mView.findViewById(R.id.tvVal_start_time);
                TextView tvEndTime = (TextView) mView.findViewById(R.id.tvVal_end_time);

                tvUserName.setText(task.getFirstname()+" " +task.getLastname());
                tvProdName.setText(task.getProduct_name());
                tvProdCode.setText(task.getSku_code());
                tvProdQty.setText(task.getProduct_quantity());
                tvProdLoc.setText(task.getProduct_address());
                tvTimeReq.setText(task.getTime_required()+" hrs");
                tvStatus.setText(task.getStatus());
                tvStTime.setText(task.getStart_time());
                tvEndTime.setText(task.getEnd_time());

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
                        Toast.makeText(TaskListActivity.this, "WhatsApp...." ,
                                Toast.LENGTH_SHORT).show();
//                        PackageManager pm=getPackageManager();
                        boolean isWhatsappInstalled = appInstalled("com.whatsapp");
                        if (isWhatsappInstalled){
                            Uri uri = Uri.parse("smsto:" +"7208590840");
                            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                            intent.setPackage("com.whatsapp");
                            startActivity(intent);
//                            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+"+91"+"7208590840"+"&text="+"hellow World.."));
                        }else{
                            Toast.makeText(TaskListActivity.this, "Whatsapp Is not Installed..." ,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btnCalling.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TaskListActivity.this, "Calling Driver...." , Toast.LENGTH_SHORT).show();
                        makePhoneCall();
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

    private void makePhoneCall(){
        String number = "1234567890";
        if (number.trim().length()>0){
            if(ContextCompat.checkSelfPermission(TaskListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(TaskListActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            }
            else{
                String dial = "tel:"+number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{
            Toast.makeText(TaskListActivity.this,"Phone No. Not avaliable..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            }
            else{
                Toast.makeText(TaskListActivity.this, "Permission Denied..", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public androidx.appcompat.app.AlertDialog.Builder DialogForServiceDown(Context c, String altTile, String msg) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(c);
        builder.setTitle(altTile);
        builder.setMessage(msg);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return builder;
    }
    public void getTaskListFromDB(JsonObject obejct) {
        compositeDisposable.add(api.getAssignedTaskByUserId(obejct).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JsonObject>() {
                                       @Override
                                       public void accept(JsonObject jsonElements) throws Exception {
                                           Log.d("Project Data", jsonElements.toString());
                                           if (jsonElements.get("success").getAsBoolean()) {
                                               Log.d("Task node Data", jsonElements.toString());
                                               CreateListData(jsonElements.getAsJsonArray("data"));
//                                       taskDetailList.add(jsonElements.getAsJsonArray("data"));
                                           } else {
                                               DialogForServiceDown(TaskListActivity.this, "Service Error", "Unable to load data. Please try again later...").show();
                                           }

                                       }
                                   }, new Consumer<Throwable>() {
                                       @Override
                                       public void accept(Throwable throwable) throws Exception {
                                           Log.d("TAG", "accept:  Service Call Failed.." + throwable.getMessage());
                                           DialogForServiceDown(TaskListActivity.this, "System Error", throwable.getMessage()).show();
                                       }
                                   }

                        )
        );
    }

    public void updateTaskListToDB(JsonObject obejct) {
        compositeDisposable.add(api.updateTaskDetails(obejct).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d("Project Data", jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d("Task node Data", jsonElements.toString());
                                   } else {
                                       DialogForServiceDown(TaskListActivity.this, "Service Error", "Unable to load data. Please try again later...").show();
                                   }

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d("TAG", "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(TaskListActivity.this, "System Error", throwable.getMessage()).show();
                               }
                           }

                )
        );
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
