package com.example.login.ui.timesheet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

import static com.example.login.R.layout.activity_submit_timesheet;

public class ActivitySubmitTimesheet extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    NodeJS api;
    CompositeDisposable compositeDisposable;
    JsonArray projectArray;
    JsonObject objTimesheet = new JsonObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final User loggedInUser;
        super.onCreate(savedInstanceState);
        setContentView(activity_submit_timesheet);
        loggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        setData(loggedInUser);
        objTimesheet.addProperty("user_id", loggedInUser.getUserid());

        EditText lbEmpName = (EditText) findViewById(R.id.etEmpName);
        lbEmpName.setEnabled(false);
        EditText lbEmpCd = (EditText) findViewById(R.id.etEmpCode);
        lbEmpCd.setEnabled(false);
        EditText lbSelectWk = (EditText) findViewById(R.id.edSelWk);
        lbSelectWk.setEnabled(false);

//        getProjectData();
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        projectArray = getProjectData();


        Spinner spinner = (Spinner) findViewById(R.id.selectTL);
        spinner.requestFocus();
        spinner.setOnItemSelectedListener(this);

        List<Date> categories = getDtList();
        List<String> dateList = new ArrayList<String>();

        findViewById(R.id.addData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(ActivitySubmitTimesheet.this, "Search...", "what are you looking for...?", null,
                        initData(projectArray), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        addTableRow(searchable.getTitle().substring(0, 6));
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();

            }
        });

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        for (Date date : categories) {
            System.out.println(fmt.format(date));
            dateList.add(fmt.format(date));
        }


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dateList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout tbl = (TableLayout) findViewById(R.id.tableData);
                JsonArray data = new JsonArray();
                for (int i = 0; i < tbl.getChildCount(); i++) {

                    if (i == 0) {
                        continue;
                    }
                    JsonObject timesheetData = new JsonObject();
                    TableRow rowData = (TableRow) tbl.getChildAt(i);
                    for (int j = 0; j < 9; j++) {

                        TextView cellData = (TextView) rowData.getChildAt(j);
                        if (j == 0){
                            if (cellData.getTag()!=null){
                                timesheetData.addProperty("timesheetMasterId", cellData.getTag().toString().replace("\"", ""));
                            }
                        }

                        String strData = cellData.getText().toString();
                        List<String> day = Arrays.asList("srNo", "ProjectCode", "mon", "tue", "wed", "thu", "fri", "sat", "sun");
                        if (strData.equals("")) {
                            strData = "0";
                        }

                        timesheetData.addProperty(day.get(j), strData);

                    }
                    data.add(timesheetData);

                }
                objTimesheet.add("rowData", data);
                Boolean Submitted = sendTimesheetData(objTimesheet);
                Log.d("Table Data :: ", objTimesheet.toString());
            }
        });

    }

    public boolean sendTimesheetData(JsonObject timesheetData) {

        compositeDisposable.add(api.addTimesheetData(timesheetData).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JsonObject>() {
                                       @Override
                                       public void accept(JsonObject jsonElements) throws Exception {
                                           Log.d("Project Data", jsonElements.toString());
                                           if (jsonElements.get("success").getAsBoolean()) {
                                               Log.d("Request Success", jsonElements.toString());
                                               DialogForServiceDown(ActivitySubmitTimesheet.this, "Submitted", "Submitted Successfully..").show();
//                                       finish();

                                           } else {
                                               DialogForServiceDown(ActivitySubmitTimesheet.this, "Unable to process Data", "We are having trubble while inserting data. Please contact to your admin").show();
                                           }

                                       }
                                   }, new Consumer<Throwable>() {
                                       @Override
                                       public void accept(Throwable throwable) throws Exception {
                                           Log.d("TAG", "accept:  Service Call Failed.." + throwable.getMessage());
                                           DialogForServiceDown(ActivitySubmitTimesheet.this, "", throwable.getMessage()).show();
                                       }
                                   }


                        )
        );


        return false;
    }


    private ArrayList<SearchModel> initData(JsonArray data) {
        ArrayList<SearchModel> items = new ArrayList<>();

        Log.d("project Json..", data.toString());

        for (JsonElement projectList : data) {
            String item = (projectList.getAsJsonObject().get("project_code").toString() + " - " + projectList.getAsJsonObject().get("project_name").toString()).replace("\"","");
            items.add(new SearchModel(item));
        }
        return items;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        Log.d("selected week", parent.getSelectedItem().toString().replace("/", ""));
        objTimesheet.addProperty("selectedWeek", parent.getSelectedItem().toString().replace("/", ""));
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        getTimesheetData(objTimesheet);
    }

    public JsonArray getTimesheetData(JsonObject obejct) {
        final JsonArray timesheetJsonArray = new JsonArray();
        compositeDisposable.add(api.getTimesheetData(obejct).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d("Project Data", jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d("Timesheet node Data", jsonElements.toString());
                                       addTableRowFromDB(jsonElements.getAsJsonArray("data"));
                                   } else {
                                       DialogForServiceDown(ActivitySubmitTimesheet.this, "Service Error", "Unable to load data. Please try again later...").show();
                                   }

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d("TAG", "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(ActivitySubmitTimesheet.this, "", throwable.getMessage()).show();
                               }
                           }


                )
        );
        return timesheetJsonArray;
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public ArrayList getDtList() {
        List<Date> disable = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        cal.set(year, month, 1);
        do {

            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY)
                disable.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } while (cal.get(Calendar.MONTH) == month);
        return (ArrayList) disable;
    }

    public void setData(User userData) {
        setContentView(activity_submit_timesheet);
        Intent intent = getIntent();
        TextView EmpName = (TextView) findViewById(R.id.tvEmpName);
        TextView EmpCode = (TextView) findViewById(R.id.tvEmpCode);
        String name = userData.getFirstname() + " " + userData.getLastname();
        String code = userData.getEmpcode();
        EmpName.setText(name);
        EmpCode.setText(code);

    }

    private void addTableRowFromDB(JsonArray timesheetData){
        Log.d("timesheet chaecking", timesheetData.toString());
        TableLayout t1;
        TableRow[] tr= new TableRow[10] ;
        EditText ed3, ed4, ed5, ed6, ed7, ed8, ed9;
        TextView tv1, tv2;
        t1 = (TableLayout) findViewById(R.id.tableData);

        t1.removeAllViewsInLayout();
        if (t1.getChildCount() >= 10) {
            Log.d("table rows Limit Reach", Integer.toString(t1.getChildCount()));
            DialogForServiceDown(ActivitySubmitTimesheet.this, "Add Timesheet Row", "Row limit reached").show();
        } else {
            for (int i = 0; i < 10; i++) {
                t1.setColumnStretchable(i, true);
            }
            for (int i=0; i < timesheetData.size();i++) {

                JsonObject obj = (JsonObject) timesheetData.get(i);
                tr[i] = new TableRow(this);
                tv1 = new TextView(this);
                tv2 = new TextView(this);
                ed3 = new EditText(this);
                ed4 = new EditText(this);
                ed5 = new EditText(this);
                ed6 = new EditText(this);
                ed7 = new EditText(this);
                ed8 = new EditText(this);
                ed9 = new EditText(this);

                InputFilter[] inputFilters = new InputFilter[1];
                inputFilters[0] = new InputFilter.LengthFilter(4);

                tv1.setText(Integer.toString(t1.getChildCount()+1));
                tv1.setTextSize(15);
                tv1.setGravity(Gravity.CENTER);
                tv1.setTag(obj.get("timesheet_master_id"));
                tv1.setFreezesText(true);


                tv2.setText(obj.get("project_code").toString().replace("\"",""));
                tv2.setTextSize(15);
                tv2.setGravity(Gravity.CENTER);
                tv1.setFreezesText(true);

                ed3.setText(obj.get("mon_hours").toString().replace("\"",""));
                ed3.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed3.setFilters(inputFilters);

                ed4.setText(obj.get("tue_hours").toString().replace("\"",""));
                ed4.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed4.setFilters(inputFilters);

                ed5.setText(obj.get("wed_hours").toString().replace("\"",""));
                ed5.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed5.setFilters(inputFilters);

                ed6.setText(obj.get("thu_hours").toString().replace("\"",""));
                ed6.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed6.setFilters(inputFilters);

                ed7.setText(obj.get("fri_hours").toString().replace("\"",""));
                ed7.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed7.setFilters(inputFilters);

                ed8.setText(obj.get("sat_hours").toString().replace("\"",""));
                ed8.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed8.setFilters(inputFilters);

                ed9.setText(obj.get("sun_hours").toString().replace("\"",""));
                ed9.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed9.setFilters(inputFilters);

                tr[i].addView(tv1);
                tr[i].addView(tv2);
                tr[i].addView(ed3);
                tr[i].addView(ed4);
                tr[i].addView(ed5);
                tr[i].addView(ed6);
                tr[i].addView(ed7);
                tr[i].addView(ed8);
                tr[i].addView(ed9);

                t1.addView(tr[i]);


            }

        }

    }

    private void addTableRow(String projectCd) {
        TableLayout t1;
        TableRow tr;
        EditText ed3, ed4, ed5, ed6, ed7, ed8, ed9;
        TextView tv1, tv2;

        t1 = (TableLayout) findViewById(R.id.tableData);
        if (t1.getChildCount() >= 10) {

            Log.d("table rows Limit Reach", Integer.toString(t1.getChildCount()));
            DialogForServiceDown(ActivitySubmitTimesheet.this, "Add Timesheet Row", "Row limit reached").show();
        } else {

            for (int i = 0; i < 10; i++) {
                t1.setColumnStretchable(i, true);
            }


            tr = new TableRow(this);
            tv1 = new TextView(this);
            tv2 = new TextView(this);
            ed3 = new EditText(this);
            ed4 = new EditText(this);
            ed5 = new EditText(this);
            ed6 = new EditText(this);
            ed7 = new EditText(this);
            ed8 = new EditText(this);
            ed9 = new EditText(this);

            InputFilter[] inputFilters = new InputFilter[1];
            inputFilters[0] = new InputFilter.LengthFilter(4);

            tv1.setText(Integer.toString(t1.getChildCount()+1));
            tv1.setTextSize(15);
            tv1.setGravity(Gravity.CENTER);
            tv1.setFreezesText(true);
            tv1.setTag(null);

            tv2.setText(projectCd);
            tv2.setTextSize(15);
            tv2.setGravity(Gravity.CENTER);
            tv2.setFreezesText(false);

            ed3.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed3.setFilters(inputFilters);
            ed4.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed4.setFilters(inputFilters);
            ed5.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed5.setFilters(inputFilters);
            ed6.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed6.setFilters(inputFilters);
            ed7.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed7.setFilters(inputFilters);
            ed8.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed8.setFilters(inputFilters);
            ed9.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ed9.setFilters(inputFilters);

            tr.addView(tv1);
            tr.addView(tv2);
            tr.addView(ed3);
            tr.addView(ed4);
            tr.addView(ed5);
            tr.addView(ed6);
            tr.addView(ed7);
            tr.addView(ed8);
            tr.addView(ed9);

            t1.addView(tr);
            Log.d("table rows count", Integer.toString(t1.getChildCount() - 1));

        }

    }

    public JsonArray getProjectData() {

        final JsonArray projectJsonArray = new JsonArray();
        compositeDisposable.add(api.getProjectList().subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d("Project Data", jsonElements.toString());
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       Log.d("Request Success", jsonElements.toString());
                                       projectJsonArray.addAll(jsonElements.getAsJsonArray("data"));
                                   } else {
                                       DialogForServiceDown(ActivitySubmitTimesheet.this, "", "").show();
                                   }

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d("TAG", "accept:  Service Call Failed.." + throwable.getMessage());
                                   DialogForServiceDown(ActivitySubmitTimesheet.this, "", throwable.getMessage()).show();
                               }
                           }


                )
        );
        return projectJsonArray;
    }


    public AlertDialog.Builder DialogForServiceDown(Context c, String altTile, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
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


}
