package com.example.login.ui.timesheet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.example.login.util.Dailog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import retrofit2.Retrofit;

//import android.content.DialogInterface;

public class AssignedTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = "SampleActivity";
    private Spinner taskType, rack, level, bin;
    private Button assign;
    NodeJS api;
    CompositeDisposable compositeDisposable;
    boolean first = true, second = true, third = true, fourth = true;
    ProgressDialog progressDialog, progressDialog1;
    JsonArray dataAsJsonArray1, dataAsJsonArray2, dataAsJsonArray3;
    private String user_id = "";
    private ListView listview;
    private List<String> userIdList = new ArrayList<>();
    //private List<String> userNameList = new ArrayList<>();
    private List<JsonObject> userList = new ArrayList<>();
    //    private GridView gridview;
    User LoggedInUser;
    private EditText product, taskDesc, warehouse, expectedTime;
    int radioCount = 0;
    private RadioGroup rdUserGrp;
    private RadioButton rdUser;
    private Button btnSelectUser;

    private String[] userList2;
    ArrayList<Integer> mUserList = new ArrayList<>();
    private boolean[] checkedUsers;
    private TableLayout tableLayout;
    private String taskTypeId = "", productId = "", rackText = "", levelText = "", binText = "";
    List<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_task);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        LoggedInUser = SharedPrefManager.getInstance(AssignedTaskActivity.this).getUser();

//        listview = findViewById(R.id.listview);
//        gridview = findViewById(R.id.gridview);
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//
//
//                CheckedTextView item = (CheckedTextView) arg1;
//                if (item.isChecked()) {
//                    JsonElement obj = dataAsJsonArray1.get(arg2);
//                    JsonObject jsonObject = obj.getAsJsonObject();
//                    user_id = jsonObject.get("user_id").getAsString();
//                    userIdList.add(user_id);
//                    //userNameList.add(jsonObject.get("firstname").getAsString().toUpperCase() + " " + jsonObject.get("lastname").getAsString().toUpperCase());
//                    userList.add(dataAsJsonArray1.get(arg2).getAsJsonObject());
//
//
//                    if (userList.size() > 1) {
//                        if (tableLayout.getChildCount() > 1) {
//                            tableLayout.removeViewsInLayout(1, tableLayout.getChildCount() - 1);
//                            tableLayout.setVisibility(View.INVISIBLE);
//                        }
//                        for (int i = 0; i < userList.size(); i++) {
//                            tableLayout.setVisibility(View.VISIBLE);
//
//                            TableRow tr = new TableRow(AssignedTaskActivity.this);
//                            // tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1));
//                            tr.setPadding(5,15,5,0);
//
//                            TextView tv = new TextView(AssignedTaskActivity.this);
//                            tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,1));
//                            tv.setText(userList.get(i).get("firstname").getAsString().toUpperCase() + " " + userList.get(i).get("lastname").getAsString().toUpperCase());
//                            tv.setGravity(Gravity.LEFT);
//
//
//                            EditText ed1 = new EditText(AssignedTaskActivity.this);
//                            ed1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,1));
//                            ed1.setBackgroundResource(R.drawable.textbox_back);
//
//                            ed1.setMaxLines(1);
//                            ed1.setGravity(Gravity.LEFT);
//
//
//
//
//
//
//                            tr.addView(tv);
//                            tr.addView(ed1);
//
//
//                            tableLayout.addView(tr);
//
//                        }
//                    }
//
//
//                } else {
//                    JsonElement obj = dataAsJsonArray1.get(arg2);
//                    JsonObject jsonObject = obj.getAsJsonObject();
//                    user_id = jsonObject.get("user_id").getAsString();
//                    int index = userIdList.indexOf(user_id);
//                    userIdList.remove(user_id);
//                    //userNameList.remove(jsonObject.get("firstname").getAsString().toUpperCase() + " " + jsonObject.get("lastname").getAsString().toUpperCase());
//                    userList.remove(dataAsJsonArray1.get(arg2).getAsJsonObject());
//
//
//                    if (tableLayout.getChildCount() > 1) {
//                        tableLayout.removeViewAt(++index);
//                    }
//
//
//                    if (userList.size() == 1) {
//                        tableLayout.removeViewsInLayout(1, 1);
//                    }
//
//
//                }
//
//
//            }
//
//
//        });


        rdUserGrp = (RadioGroup) findViewById(R.id.radioGrp);
        btnSelectUser = (Button) findViewById(R.id.btnSelectUser);
        taskDesc = findViewById(R.id.taskDesc);

        taskType = findViewById(R.id.spinnerTaskType);
        taskType.setOnItemSelectedListener(this);
        warehouse = findViewById(R.id.spinnerWarehouse);

        expectedTime = (EditText) findViewById(R.id.edExpctdTime);
        warehouse.setText(LoggedInUser.getWarehouse());
        warehouse.setEnabled(false);
        rack = findViewById(R.id.spinnerRack);
        rack.setOnItemSelectedListener(this);
        level = findViewById(R.id.spinnerLevel);
        level.setOnItemSelectedListener(this);
        bin = findViewById(R.id.spinnerBin);
        bin.setOnItemSelectedListener(this);
        product = findViewById(R.id.product);
        product.setOnClickListener(this);
        assign = findViewById(R.id.assign);
        assign.setOnClickListener(this);
        tableLayout = findViewById(R.id.tableabl);
        tableLayout.setVisibility(View.INVISIBLE);
        // Init API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        JsonObject Json = new JsonObject();
        Json.addProperty("user_id", LoggedInUser.getUserid());
        Json.addProperty("role", LoggedInUser.getRole());
        Json.addProperty("requiredUser", "Employee");
        new GetEmpListAsync().execute(Json);

        new GetTaskTypeAsync().execute(new JsonObject());


        rdUserGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {


                rdUser = (RadioButton) group.findViewById(checkedId);
                boolean isChecked = rdUser.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {

                    if (rdUser.getText().toString().equals("Individual")) {
                        radioCount = 1;
                    }
                    if (rdUser.getText().toString().equals("Group Task")) {
                        radioCount = 2;
                    }


                }

            }
        });

        btnSelectUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder mbuilder = new AlertDialog.Builder(AssignedTaskActivity.this);
                mbuilder.setTitle("Select User.");
                Toast.makeText(AssignedTaskActivity.this, Integer.toString(radioCount), Toast.LENGTH_SHORT).show();
                if (radioCount == 0) {
                    Toast.makeText(AssignedTaskActivity.this, "Please Select User For.", Toast.LENGTH_SHORT).show();
                } else if (radioCount == 1) {
                    mbuilder.setSingleChoiceItems(userList2,-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {

                            Toast.makeText(AssignedTaskActivity.this, "User Selected...", Toast.LENGTH_SHORT).show();
                            userIdList.clear();
                            userList.clear();
                            userList.add(dataAsJsonArray1.get(position).getAsJsonObject());
                            userIdList.add(dataAsJsonArray1.get(position).getAsJsonObject().get("user_id").getAsString());
                        }

                    });
                    mbuilder.setCancelable(false);
                    mbuilder.setPositiveButton("OK",new DialogInterface.OnClickListener()

                    {
                        @Override
                        public void onClick (DialogInterface dialog,int which){

                        }
                    });
                    mbuilder.setNegativeButton("Dismiss",new DialogInterface.OnClickListener()

                    {
                        @Override
                        public void onClick (DialogInterface dialog,int which){
                            dialog.dismiss();
                        }
                    });
                    mbuilder.show();

                }else {

                    mbuilder.setMultiChoiceItems(userList2, checkedUsers, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position, boolean isChecked) {

                            if (isChecked) {
                                if (!mUserList.contains(position)) {
                                    mUserList.add(position);
                                } else{
                                    mUserList.remove(position);
                                    }

                                }
                            }
                        });

                    mbuilder.setCancelable(false);
                    mbuilder.setPositiveButton("OK",new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick (DialogInterface dialog,int which){
                            ArrayList<String> selectedUser = new ArrayList<>();
                                userList.clear();
                                userIdList.clear();
                            for (int i = 0; i < mUserList.size(); i++) {
                                Log.d("selected User ::", userList2[mUserList.get(i)]);
                                selectedUser.add(userList2[mUserList.get(i)]);
                                userList.add(dataAsJsonArray1.get(mUserList.get(i)).getAsJsonObject());
                                userIdList.add(dataAsJsonArray1.get(mUserList.get(i)).getAsJsonObject().get("user_id").getAsString());
                            }
                            createTableRows(selectedUser);
                            Toast.makeText(AssignedTaskActivity.this, selectedUser.toString(), Toast.LENGTH_SHORT).show();


                        }
                        });
                    mbuilder.setNegativeButton("Dismiss",new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick (DialogInterface dialog,int which){
                            dialog.dismiss();
                        }
                        });
                    mbuilder.show();
                    }
                }
            });
        }

        public void createTableRows(ArrayList<String> selectedUser){
//        if (tableLayout.getChildCount() > 1) {
//            tableLayout.removeViewsInLayout(1, tableLayout.getChildCount() - 1);
//            tableLayout.setVisibility(View.INVISIBLE);
//        }
        for (int i = 0; i < selectedUser.size(); i++) {
            tableLayout.setVisibility(View.VISIBLE);

            TableRow tr = new TableRow(AssignedTaskActivity.this);
            // tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1));
            tr.setPadding(5, 15, 5, 0);

            TextView tv = new TextView(AssignedTaskActivity.this);
            tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            tv.setText(selectedUser.get(i));
            tv.setGravity(Gravity.LEFT);


            EditText ed1 = new EditText(AssignedTaskActivity.this);
            ed1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
            ed1.setBackgroundResource(R.drawable.textbox_back);

            ed1.setMaxLines(1);
            ed1.setGravity(Gravity.LEFT);


            tr.addView(tv);
            tr.addView(ed1);


            tableLayout.addView(tr);
        }
    }
        @Override
        public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

            switch (parent.getId()) {


                case R.id.spinnerTaskType:
                    if (first) {
                        first = false;
                    } else {
                        if (position == 0) {
                            Toast.makeText(this, "Please Select Task Type.", Toast.LENGTH_SHORT).show();
                            //requestBody.addProperty("task_type_id", "");
                            taskTypeId = "";
                        } else {
                            position--;
                            //requestBody.addProperty("task_type_id", dataAsJsonArray3.get(position).getAsJsonObject().get("task_type_id").getAsString());
                            taskTypeId = dataAsJsonArray3.get(position).getAsJsonObject().get("task_type_id").getAsString();
                            JsonObject jsonObject = new JsonObject();
                            if (parent.getItemAtPosition(++position).toString().equals("Pick")) {
                                jsonObject.addProperty("isactive", "N");
                                dataAsJsonArray2 = getProductData(jsonObject);
                            } else {
                                jsonObject.addProperty("isactive", "Y");
                                dataAsJsonArray2 = getProductData(jsonObject);
                            }


                        }
                    }


                    break;

                case R.id.spinnerRack:
                    if (second) {
                        second = false;
                    } else {

                        if (position == 0) {
                            Toast.makeText(this, "Please Select Rack.", Toast.LENGTH_SHORT).show();
                            rackText = "";
                        } else {

                            rackText = parent.getItemAtPosition(position).toString();


                        }
                    }
                    break;

                case R.id.spinnerLevel:
                    if (third) {
                        third = false;
                    } else {


                        if (position == 0) {
                            Toast.makeText(this, "Please Select Level.", Toast.LENGTH_SHORT).show();
                            levelText = "";
                        } else {

                            levelText = parent.getItemAtPosition(position).toString();


                        }
                    }
                    break;


                case R.id.spinnerBin:
                    if (fourth) {
                        fourth = false;
                    } else {

                        if (position == 0) {
                            Toast.makeText(this, "Please Select Bin.", Toast.LENGTH_SHORT).show();
                            binText = "";
                        } else {

                            binText = parent.getItemAtPosition(position).toString();

                        }
                    }
                    break;


                default:
                    break;

            }

        }

        @Override
        public void onNothingSelected (AdapterView < ? > parent){


        }


        @Override
        public void onClick (View v){

            switch (v.getId()) {
                case R.id.product:
                    if (taskTypeId.equals("")) {
                        Toast.makeText(this, "Please select Task Type.", Toast.LENGTH_SHORT).show();
                    } else {
                        new SimpleSearchDialogCompat(this, "Search...", "what are you looking for...?", null,
                                initData(dataAsJsonArray2), new SearchResultListener<Searchable>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                                productId = dataAsJsonArray2.get(i).getAsJsonObject().get("product_id").getAsString();
                                product.setText(searchable.getTitle());
                                expectedTime.setText(dataAsJsonArray2.get(i).getAsJsonObject().get("time_required").getAsString());
                                baseSearchDialogCompat.dismiss();
                            }
                        }).show();
                    }
                    break;


                case R.id.assign:
                    if (taskTypeId.equals("")) {
                        Toast.makeText(this, "Please Select Task Type.", Toast.LENGTH_SHORT).show();
                    } else if (userList.size() == 0) {
                        Toast.makeText(this, "Please Select User.", Toast.LENGTH_SHORT).show();
                    } else if (productId.equals("")) {
                        Toast.makeText(this, "Please Select Product.", Toast.LENGTH_SHORT).show();
                    } else if (rackText.equals("")) {
                        Toast.makeText(this, "Please Select Rack.", Toast.LENGTH_SHORT).show();
                    } else if (levelText.equals("")) {
                        Toast.makeText(this, "Please Select Level.", Toast.LENGTH_SHORT).show();
                    } else if (binText.equals("")) {
                        Toast.makeText(this, "Please Select Bin.", Toast.LENGTH_SHORT).show();
                    } else if (expectedTime.getText().toString().equals("")) {
                        Toast.makeText(this, "Please Enter Expected Time.", Toast.LENGTH_SHORT).show();
                    } else {

                        JsonObject requestBody = new JsonObject();
                        requestBody.addProperty("task_type_id", taskTypeId);
                        requestBody.addProperty("task_description", taskDesc.getText().toString());
                        requestBody.addProperty("product_id", productId);
                        requestBody.addProperty("warehouse", warehouse.getText().toString());
                        requestBody.addProperty("rack", rackText);
                        requestBody.addProperty("level", "L" + levelText);
                        requestBody.addProperty("bin", "B" + binText);
                        requestBody.addProperty("manager_id", LoggedInUser.getUserid());
                        requestBody.addProperty("time_required", expectedTime.getText().toString());

                        if (tableLayout.getChildCount() > 2) {
                            List<String> subTaskList = new ArrayList<>();
                            for (int i = 1; i < tableLayout.getChildCount(); i++) {

                                TableRow row = (TableRow) tableLayout.getChildAt(i);
                                EditText sub_task = (EditText) row.getChildAt(1);
                                subTaskList.add(sub_task.getText().toString());
                            }
                            // JsonArray array = new Gson().toJsonTree(subTaskList).getAsJsonArray();
                            JsonArray array = new JsonArray();
                            int count = 0;
                            Log.d("userIdList :: ", userIdList.toString());
                            Log.d("subTaskList :: ", subTaskList.toString());
                            for (String id : userIdList) {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("user_id", id);
                                jsonObject.addProperty("sub_task", subTaskList.get(count));
                                array.add(jsonObject);
                                count++;

                            }
                            requestBody.add("sub_task", array);
                            requestBody.addProperty("is_sub_task", "Y");
                        } else {
                            requestBody.addProperty("user_id", userIdList.get(0));
                            requestBody.addProperty("is_sub_task", "N");
                        }

                        Log.e(TAG, "onClick: request" + requestBody.toString());

                        new AssignTaskAsync().execute(requestBody);
                    }


                    break;
                default:
                    break;

            }

        }

        private ArrayList<SearchModel> initData (JsonArray data){
            ArrayList<SearchModel> items = new ArrayList<>();

            Log.d("product Json..", data.toString());

            for (JsonElement projectList : data) {
                String item = (projectList.getAsJsonObject().get("sku_code").toString() + " - " + projectList.getAsJsonObject().get("product_name").toString() + " - " + projectList.getAsJsonObject().get("product_quantity").toString()).replace("\"", "");
                items.add(new SearchModel(item));
            }
            return items;
        }

        public JsonArray getProductData (JsonObject jsonObject){

            final JsonArray productJsonArray = new JsonArray();
            compositeDisposable.add(api.getTaskList(jsonObject).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d("Product Data", jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean()) {
                                           Log.d("Request Success", jsonElements.toString());
                                           if (jsonElements.isJsonNull()) {
                                               Toast.makeText(AssignedTaskActivity.this, "No product found.", Toast.LENGTH_SHORT).show();

                                           } else {
                                               productJsonArray.addAll(jsonElements.getAsJsonArray("data"));
                                           }
                                       } else {
                                           Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
                                       }

                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d("TAG", "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
                                   }
                               }


                    )
            );
            return productJsonArray;
        }

        class GetEmpListAsync extends AsyncTask<JsonObject, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(AssignedTaskActivity.this,
                        "Please Wait",
                        "Wait for moments");

            }

            @Override
            protected String doInBackground(JsonObject... jsonObjects) {


                compositeDisposable.add(api.getSuperUserList(jsonObjects[0]).subscribeOn(
                        Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<JsonObject>() {
                                               @Override
                                               public void accept(JsonObject jsonElements) throws Exception {
                                                   Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                                   if (jsonElements.get("success").getAsBoolean()) {
                                                       Log.d(TAG, "accept:  Service Call Successful..");

                                                       dataAsJsonArray1 = jsonElements.getAsJsonArray("data");
                                                       for (JsonElement List : dataAsJsonArray1) {
                                                           JsonObject Obj = List.getAsJsonObject();
                                                           list.add(Obj.get("firstname").getAsString().toUpperCase() + " " + Obj.get("lastname").getAsString().toUpperCase());

                                                       }
                                                       Log.d("list ::" , list.toString());
                                                       userList2 = new String[list.size()];
                                                       userList2 = list.toArray(userList2);
                                                       checkedUsers = new boolean[userList2.length];
                                                   } else {

                                                       Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
                                                   }
                                               }
                                           }, new Consumer<Throwable>() {
                                               @Override
                                               public void accept(Throwable throwable) throws Exception {
                                                   Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                                   Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
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


        class GetTaskTypeAsync extends AsyncTask<JsonObject, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog1 = ProgressDialog.show(AssignedTaskActivity.this,
                        "Please Wait",
                        "Wait for moments");

            }

            @Override
            protected String doInBackground(JsonObject... jsonObjects) {


                compositeDisposable.add(api.getTaskType().subscribeOn(
                        Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JsonObject>() {
                                       @Override
                                       public void accept(JsonObject jsonElements) throws Exception {
                                           Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                           if (jsonElements.get("success").getAsBoolean()) {
                                               Log.d(TAG, "accept:  Service Call Successful..");
                                               addDataOnTaskTypeSpinner(jsonElements);
                                           } else {

                                               Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
                                           }
                                       }
                                   }, new Consumer<Throwable>() {
                                       @Override
                                       public void accept(Throwable throwable) throws Exception {
                                           Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                           Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
                                       }
                                   }


                        )
                );

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog1.dismiss();
            }
        }

        private void addDataOnTaskTypeSpinner (JsonObject jsonObject){


            List<String> list = new ArrayList<String>();
            list.add("select");
            dataAsJsonArray3 = jsonObject.getAsJsonArray("data");
            for (JsonElement TLList : dataAsJsonArray3) {
                JsonObject Obj = TLList.getAsJsonObject();

                list.add(Obj.get("task_type").getAsString());
            }


            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AssignedTaskActivity.this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            taskType.setAdapter(dataAdapter);


        }


        class AssignTaskAsync extends AsyncTask<JsonObject, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(AssignedTaskActivity.this,
                        "Please Wait",
                        "Wait for moments");

            }

            @Override
            protected String doInBackground(JsonObject... jsonObjects) {


                compositeDisposable.add(api.assignTask(jsonObjects[0]).subscribeOn(
                        Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JsonObject>() {
                                       @Override
                                       public void accept(JsonObject jsonElements) throws Exception {
                                           Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                           if (jsonElements.get("success").getAsBoolean()) {
                                               Log.d(TAG, "accept:  Service Call Successful..");
                                               Toast.makeText(AssignedTaskActivity.this, "Task Assigned Successfully.", Toast.LENGTH_SHORT).show();
                                               finish();

                                           } else {

                                               Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
                                           }
                                       }
                                   }, new Consumer<Throwable>() {
                                       @Override
                                       public void accept(Throwable throwable) throws Exception {
                                           Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                           Dailog.DialogForServiceDown(AssignedTaskActivity.this).show();
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
        protected void onStop () {
            compositeDisposable.clear();
            super.onStop();

        }

        @Override
        protected void onDestroy () {
            compositeDisposable.clear();
            super.onDestroy();
        }
        //from activity to fragment on back button click
        @Override
        public boolean onOptionsItemSelected (MenuItem item){

            super.onOptionsItemSelected(item);
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
            }

            return true;
        }
    }
