package com.example.login.ui.timesheet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.example.login.util.Dailog;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

class TaskDetails_ListAdaptor extends ArrayAdapter {

    private LayoutInflater mInFlater;
    private ArrayList<Task> tasks;
    private int mViewResourceId;
    NodeJS api;
    User loggedInUser;
    CompositeDisposable compositeDisposable;
    JsonObject requestBody = new JsonObject();
    boolean rack, level, bin = true;
    String rackData, levelData, binData ;

    public TaskDetails_ListAdaptor(Context context, int textViewResourceId, ArrayList<Task> tasks){

        super (context, textViewResourceId, tasks);

        this.tasks= tasks;
        mInFlater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;

    }
    public View getView(int position, View converView, final ViewGroup parents){

        loggedInUser = SharedPrefManager.getInstance(getContext()).getUser();

        converView = mInFlater.inflate(mViewResourceId, null);
        //InIt API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();


        final Task task = tasks.get(position);
        if(task!=null){
            TextView data1 = (TextView) converView.findViewById(R.id.tvTaskCode);
            TextView data2 = (TextView) converView.findViewById(R.id.TvTaskType);
            TextView data3 = (TextView) converView.findViewById(R.id.TvTaskAltType);
            TextView data4 = (TextView) converView.findViewById(R.id.tvStatus);

            ImageButton btnPlayPause = (ImageButton) converView.findViewById(R.id.btnPlayPause);
            ImageButton btnComplete = (ImageButton) converView.findViewById(R.id.btnComplete);

            if(data1!=null){
                data1.setText(task.getTask_code());
            }
            if(data2!=null){
                data2.setText(task.getTask_type_name());
            }
            if(data3!=null){
                data3.setText(task.getTask_alt_type());
            }
            if(data4!=null){
                data4.setText(task.getStatus());
            }
            if(task.getStatus().equals("Completed")){
                btnComplete.setEnabled(false);
                btnPlayPause.setEnabled(false);
            }
            else if(task.getStatus().equals("Hold")){
                btnPlayPause.setImageResource(R.drawable.ic_btn_start);
            }

            btnComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loggedInUser.getRole().equals("Employee") && task.getIs_sub_task()=='Y'){
                        Toast.makeText(getContext(), "This action cannot perform by you", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Task Selected..", Toast.LENGTH_SHORT).show();
                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        final View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_complete_task, null);
                        ((TextView) mView.findViewById(R.id.specifiedLoc)).setText(task.getProduct_address());
                        final TextView lblSelectLoc = (TextView) mView.findViewById(R.id.lblSelectLoc);
                        final Spinner spRack = (Spinner) mView.findViewById(R.id.spinnerRack);
                        final Spinner spLevel = (Spinner) mView.findViewById(R.id.spinnerLevel);
                        final Spinner spBin = (Spinner) mView.findViewById(R.id.spinnerBin);

                        spRack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0){
                                    rack=false;
                                }else{
                                    rack = true;
                                    rackData = parent.getItemAtPosition(position).toString();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0){
                                    level= false;
                                }else{
                                    level=true;
                                    levelData = "L".concat(parent.getItemAtPosition(position).toString());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        spBin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(position==0){
                                    bin = false;
                                }else{
                                    bin = true;
                                    binData = "B".concat(parent.getItemAtPosition(position).toString());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
                        Button btnSubmit = (Button) mView.findViewById(R.id.btnSubmit);
                        final Switch switchYN = (Switch) mView.findViewById(R.id.swYN);
                        switchYN.setChecked(true);
                        switchYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                // do something, the isChecked will be
                                // true if the switch is in the On position
                                Toast.makeText(getContext(), Boolean.toString(isChecked), Toast.LENGTH_SHORT).show();
                                if (isChecked == true) {
                                    lblSelectLoc.setVisibility(View.GONE);
                                    spRack.setVisibility(View.GONE);
                                    spLevel.setVisibility(View.GONE);
                                    spBin.setVisibility(View.GONE);

//                                buttonView.setText("Yes");

                                } else {

                                    lblSelectLoc.setVisibility(View.VISIBLE);
                                    spRack.setVisibility(View.VISIBLE);
                                    spLevel.setVisibility(View.VISIBLE);
                                    spBin.setVisibility(View.VISIBLE);
//                                buttonView.setText("No");
                                }
                            }
                        });

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        btnSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String location = null;
                                Log.d("boolean Data :: ",Boolean.toString(rack)+" - "+Boolean.toString(level)+" - "+Boolean.toString(bin));
                                Log.d("loc data :: ",rackData+" - "+levelData+" - "+binData);
                                if(switchYN.isChecked()==false){
                                    if (rack == false|| bin == false|| level == false){
                                        Toast.makeText(getContext(), "Please select proper Location..", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        location = loggedInUser.getWarehouse() +"-"+ rackData +"-"+ levelData +"-"+binData;
                                        Toast.makeText(getContext(), "Task submitting..", Toast.LENGTH_SHORT).show();
                                        requestBody.addProperty("assign_id", task.getAssign_id());
                                        requestBody.addProperty("status", "Completed");
                                        requestBody.addProperty("location", location);
                                        requestBody.addProperty("product_id", task.getProduct_id());
                                        updateTaskDetails(requestBody);
                                        Intent intent;
                                        dialog.dismiss();
                                        if (loggedInUser.getRole().equals("Employee")){
                                            ((TaskListActivity) getContext()).finish();
                                            intent = new Intent((TaskListActivity) getContext(), TaskListActivity.class);
                                        }
                                        else {
                                            ((TaskTrackActivity) getContext()).finish();
                                            intent = new Intent((TaskTrackActivity) getContext(), TaskTrackActivity.class);
                                        }
                                        getContext().startActivity(intent);
                                    }
                                }else{
                                    location = task.getProduct_address();
                                    Toast.makeText(getContext(), "Task submitting..", Toast.LENGTH_SHORT).show();
                                    requestBody.addProperty("assign_id", task.getAssign_id());
                                    requestBody.addProperty("status", "Completed");
                                    requestBody.addProperty("location", location);
                                    requestBody.addProperty("product_id", task.getProduct_id());
                                    updateTaskDetails(requestBody);
                                    Intent intent;
                                    dialog.dismiss();
                                    if (loggedInUser.getRole().equals("Employee")){
                                        ((TaskListActivity) getContext()).finish();
                                        intent = new Intent((TaskListActivity) getContext(), TaskListActivity.class);
                                    }
                                    else {
                                        ((TaskTrackActivity) getContext()).finish();
                                        intent = new Intent((TaskTrackActivity) getContext(), TaskTrackActivity.class);
                                    }
                                    getContext().startActivity(intent);
                                }
                            }
                        });

                    }
                }
            });
            btnPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (loggedInUser.getRole().equals("Employee") && task.getIs_sub_task()=='Y'){
                        Toast.makeText(getContext(), "This action cannot perform by you", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        JsonObject requestBody = new JsonObject();
                        String taskStatus = task.getStatus();
                        if (task.getStatus().equals("InProcess")) {
                            taskStatus = "Hold";
                        } else {
                            taskStatus = "InProcess";
                        }
                        requestBody.addProperty("assign_id", task.getAssign_id());
                        requestBody.addProperty("status", taskStatus);
                        requestBody.addProperty("product_id", task.getProduct_id());
                        Log.d("Task Complete data :: ", requestBody.toString());
                        updateTaskDetails(requestBody);
                        Intent intent;
                        if (loggedInUser.getRole().equals("Employee")){
                            ((TaskListActivity) getContext()).finish();
                            intent = new Intent((TaskListActivity) getContext(), TaskListActivity.class);
                        }
                        else {
                            ((TaskTrackActivity) getContext()).finish();
                            intent = new Intent((TaskTrackActivity) getContext(), TaskTrackActivity.class);
                        }
                        getContext().startActivity(intent);
                    }
                }
            });

        }
        return converView;

    }

    public void updateTaskDetails(JsonObject task){
        Log.d("Updating Task : ", task.toString());
        compositeDisposable.add(api.updateTaskDetails(task).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d("ABC", "accept: response :: " + jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                        Log.d("ABC", "accept:  Service Call Successful..Error.........");
//                                       finish();
//                                       startActivity(getIntent());
                                   } else {
                                       Dailog.DialogForServiceDown(getContext()).show();
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d("ABC", "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                   Dailog.DialogForServiceDown(getContext()).show();
                               }
                           }

                )
        );

    }


}
