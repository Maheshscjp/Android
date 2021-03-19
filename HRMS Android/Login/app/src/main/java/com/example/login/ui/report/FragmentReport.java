package com.example.login.ui.report;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.login.ApplyLeaveActivity;
import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.example.login.ui.pay_slip.PaySlipFragment;
import com.example.login.util.Dailog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import retrofit2.Retrofit;

import static androidx.core.content.ContextCompat.getSystemService;

public class FragmentReport extends Fragment implements View.OnClickListener {
    ProgressDialog progressDialog;
    private static final String TAG = "FragmentReport";
    private FragmentReportViewModel mViewModel;
    private Activity context;
    private Spinner userSpinner;
    private Button submit, download;
    private EditText fromDate, toDate;
    private boolean first = true, second = true, button = false;
    private String user_id = "", fromDateString = "", toDateString = "";
    private String fromDateStringYMD = "", toDateStringYMD = "";
    int fromDateYear, fromDateMonth, fromDateDay;
    private Date fromdateDate, todateDate;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private TableLayout t1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    JsonObject Obj = null;
    NodeJS api;
    CompositeDisposable compositeDisposable;
    JsonArray dataAsJsonArray, dataAsJsonArray1, dataAsJsonArray2;
    public static final String channel_id = "channel_id";
    NotificationManagerCompat notificationManagerCompat;
    AnyChartView anyChartView;
    String[] status;
    int[] statusCount;
    String[] color = new String[3];

    ArrayList<String> stringArrayList = new ArrayList<>();
    ArrayList<Integer> integerArrayList = new ArrayList<>();
    ScrollView scrollview;
    User loggedInUser;

    public static FragmentReport newInstance() {
        return new FragmentReport();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        context = getActivity();
        dataAsJsonArray1 = new JsonArray();
        anyChartView = view.findViewById(R.id.chart);
        scrollview = view.findViewById(R.id.scrollView);
        notificationManagerCompat = NotificationManagerCompat.from(getContext());
        loggedInUser = SharedPrefManager.getInstance(getContext()).getUser();
        //  scrollview = view.findViewById(R.id.scrollView);
        status = new String[3];
        statusCount = new int[3];

        userSpinner = view.findViewById(R.id.userSpinner);
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (second) {
                    second = false;
                } else {

                    if (position == 0) {

                        user_id = "";
                        Toast.makeText(context, "Please select User", Toast.LENGTH_SHORT).show();

                    } else {
                        position--;
                        JsonElement data = dataAsJsonArray.get(position);
                        JsonObject dataObj = data.getAsJsonObject();

                        user_id = dataObj.get("user_id").getAsString();

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fromDate = view.findViewById(R.id.fromDate);
        fromDate.setOnClickListener(this);

        toDate = view.findViewById(R.id.toDate);
        toDate.setOnClickListener(this);
        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
       /* download = view.findViewById(R.id.download);
        download.setOnClickListener(this);
*/
        t1 = view.findViewById(R.id.tableabl);
        // Init API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();

        // download.setVisibility(View.INVISIBLE);
        anyChartView.setVisibility(View.INVISIBLE);
        scrollview.setVisibility(View.INVISIBLE);
        //  t1.setVisibility(View.INVISIBLE);
        JsonObject Json = new JsonObject();
        Json.addProperty("role", loggedInUser.getRole());
        Json.addProperty("user_id",loggedInUser.getUserid());
        Json.addProperty("requiredUser", "Employee");
        new GetManEmpListAsync().execute(Json);


        return view;


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FragmentReportViewModel.class);
        // TODO: Use the ViewModel
    }


    class GetManEmpListAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
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
                                           addItemsOnSpinnerUser(jsonElements);
                                       } else {

                                           Dailog.DialogForServiceDown(context).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(context).show();
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


    class GetTaskReportAsync extends AsyncTask<JsonObject, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    "Please Wait",
                    "Wait for moments");

        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {

            compositeDisposable.add(api.getTaskReport(jsonObjects[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("success")) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           addTableRowFromDB(jsonElements.getAsJsonArray("data"));
                                           dataAsJsonArray1 = jsonElements.getAsJsonArray("data");


                                       } else if (jsonElements.get("success").getAsBoolean() && jsonElements.get("message").getAsString().equals("No data found")) {
                                           Toast.makeText(context, "No Report Found", Toast.LENGTH_SHORT).show();
                                       } else {
                                           Log.e(TAG, "accept: else ");
                                           Dailog.DialogForServiceDown(context).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed.." + throwable.getMessage());
                                       Dailog.DialogForServiceDown(context).show();
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

    private void addTableRowFromDB(JsonArray timesheetData) throws ParseException {

        //t1.setVisibility(View.VISIBLE);
        TableRow tr;
        TextView tv1, tv2, tv3, tv4;
        if (t1.getChildCount() >= 2) {
            t1.removeViewsInLayout(1, t1.getChildCount() - 1);
        }


        int a = 1;
        for (int i = 0; i < timesheetData.size(); i++) {


            JsonObject obj = (JsonObject) timesheetData.get(i);
            tr = new TableRow(context);
            tv1 = new TextView(context);

            tv2 = new TextView(context);

            tv3 = new TextView(context);

            tv4 = new TextView(context);

            // tv5 = new TextView(context);


           /* tv1.setText(Integer.toString(a));
            tv1.setGravity(Gravity.CENTER);
            tv1.setTextSize(15);
            tv1.setFreezesText(true);*/


            tv1.setText(obj.get("task_code").isJsonNull() ? "" : obj.get("task_code").getAsString());
            tv1.setGravity(Gravity.CENTER);
            tv1.setTextSize(15);
            tv1.setFreezesText(true);


            // tv2.setText(obj.get("status").isJsonNull() ? "" : obj.get("status").getAsString());
            if (obj.get("status").isJsonNull())
                tv2.setText("");
            else if (obj.get("status").getAsString().equals("Completed")) {
                tv2.setTextColor(Color.parseColor("#008000"));
                tv2.setText("COM");
            } else if (obj.get("status").getAsString().equals("InProcess")) {
                tv2.setTextColor(Color.parseColor("#ff6500"));
                tv2.setText("WIP");
            } else if (obj.get("status").getAsString().equals("Hold")) {
                tv2.setTextColor(Color.parseColor("#FF0000"));
                tv2.setText("HOLD");
            } else {

                tv2.setText(obj.get("status").getAsString());
            }


            tv2.setGravity(Gravity.CENTER);
            tv2.setTextSize(15);
            tv2.setFreezesText(true);


            tv3.setText(obj.get("start_time").isJsonNull() ? "" : FragmentReport.dateFormate(obj.get("start_time").getAsString()));//FragmentReport.dateFormate
            tv3.setGravity(Gravity.CENTER);
            tv3.setMinimumWidth(0);
            tv3.setMaxWidth(10);
            tv3.setMinHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            tv3.setMaxLines(2);
            tv3.setTextSize(15);
            tv3.setFreezesText(true);


            tv4.setText(obj.get("end_time").isJsonNull() ? "" : FragmentReport.dateFormate(obj.get("end_time").getAsString()));//FragmentReport.dateFormate
            tv4.setGravity(Gravity.CENTER);
            tv4.setMinimumWidth(0);
            tv4.setMaxWidth(10);
            tv4.setMinHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            tv4.setMaxLines(2);
            tv4.setTextSize(15);
            tv4.setFreezesText(true);


            tr.addView(tv1);
            tr.addView(tv2);
            tr.addView(tv3);
            tr.addView(tv4);


            t1.addView(tr);
            // a++;

        }
        // download.setVisibility(View.VISIBLE);
        anyChartView.setVisibility(View.VISIBLE);
        scrollview.setVisibility(View.VISIBLE);

        button = true;

    }


    public void addItemsOnSpinnerUser(JsonObject data) {
        List<String> list = new ArrayList<String>();
        list.add("User");
        dataAsJsonArray = data.getAsJsonArray("data");
        for (JsonElement TLList : dataAsJsonArray) {
            JsonObject Obj = TLList.getAsJsonObject();

            list.add(Obj.get("firstname").getAsString().toUpperCase() + " " + Obj.get("lastname").getAsString().toUpperCase());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(dataAdapter);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fromDate:
                //disable keypad
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                showDatePickerDialog("FromDateCode", 0, 0, 0);

                break;
            case R.id.toDate:
                InputMethodManager imm1 = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(v.getWindowToken(), 0);
                showDatePickerDialog("ToDateCode", fromDateYear, fromDateMonth, fromDateDay);
                break;
            case R.id.submit:

                if (user_id.equals("")) {
                    Toast.makeText(context, "Please Select User", Toast.LENGTH_SHORT).show();
                } else if (fromDateStringYMD.equals("")) {
                    Toast.makeText(context, "Please Select From date.", Toast.LENGTH_SHORT).show();
                } else {

                    if (toDateStringYMD.equals("")) {
                        toDateStringYMD = fromDateStringYMD;

                    }
                    JsonObject request = new JsonObject();
                    request.addProperty("user_id", user_id);
                    request.addProperty("from_date", fromDateStringYMD);
                    request.addProperty("to_date", toDateStringYMD);
                    new GetTaskReportAsync().execute(request);
                    JsonObject req = new JsonObject();
                    req.addProperty("user_id", user_id);
                    req.addProperty("role", "Employee");
                    //  new GetStatusCount().execute(req);
                    getstatusCount(req);


                }
                break;
           /* case R.id.download:
                if (dataAsJsonArray1.size() == 0) {
                    Toast.makeText(context, "NO Report Found.", Toast.LENGTH_SHORT).show();
                } else {


                    if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                        Toast.makeText(context, "You don't have storage. ", Toast.LENGTH_SHORT).show();
                    }

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Log.d(TAG, "onClick: second time and more.");
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_CODE);

                        } else {
                            if (SharedPrefManager.getInstance(getContext()).isFirstTimeAsking(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                SharedPrefManager.getInstance(getContext()).firstTimeAsking(Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                            } else {
                                //Permission disable by device policy or user denied permanently. Show proper error message
                                DialogForAllowFromSettings(getContext()).show();
                                //ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                            }

                        }
                    } else {
                        //already have permission


                        new DownloadPDFAsync().execute(dataAsJsonArray1);
                    }
                }

                break;*/

            default:
                break;


        }

    }

    public void showDatePickerDialog(String a, int year, int month, int day) {
        if (a.equals("FromDateCode")) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    from_dateListener,
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        } else {

            if (!fromDateString.toString().equals("")) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        to_dateListener,
                        year, month - 1, day);

                long minDate = fromdateDate.getTime();

                datePickerDialog.getDatePicker().setMinDate(minDate);
                datePickerDialog.show();
            } else {
                Toast.makeText(context, "Please select From Date.", Toast.LENGTH_SHORT).show();
            }
        }


    }


    DatePickerDialog.OnDateSetListener from_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            fromDateString = dayOfMonth + "/" + month + "/" + year;
            fromDateStringYMD = year + "/" + month + "/" + dayOfMonth;
            fromDateYear = year;
            fromDateMonth = month;
            fromDateDay = dayOfMonth;

            fromDate.setText(fromDateString);


            try {
                fromdateDate = formatter.parse(fromDateString);
                Log.d(TAG, "onDateSet: " + fromdateDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };

    DatePickerDialog.OnDateSetListener to_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            month++;
            toDateString = dayOfMonth + "/" + month + "/" + year;
            toDateStringYMD = year + "/" + month + "/" + dayOfMonth;
            toDate.setText(toDateString);


            try {
                todateDate = formatter.parse(toDateString);
                Log.d(TAG, "onDateSet: " + todateDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    };


    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static AlertDialog.Builder DialogForAllowFromSettings(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Permission Required.");
        builder.setMessage("Please allow it from settings.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        return builder;
    }


    class DownloadPDFAsync extends AsyncTask<JsonArray, Integer, String> {


        //notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext(), channel_id)
                .setContentTitle("Downloading Task Report")
                .setContentText("Please Wait")
                .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;

        File apkStorage;
        File outputFile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Context con = getContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(channel_id, "channel_name", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                NotificationManager notificationManager = getSystemService(getContext(), NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }


            // notification.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            // notificationManagerCompat.notify(1, notification.build());
        }

        @Override
        protected String doInBackground(JsonArray... jsonObjects) {


            notificationManagerCompat.notify(1, notification.build());
            // apkStorage = new File(Environment.getExternalStorageDirectory()+"/"+Utils.downloadDirectory);// internal Storage of mobile i.e. 32 GB, 64 GB.
            apkStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!apkStorage.exists()) {
                apkStorage.mkdir();
                Log.e(TAG, "Directory Created.");
            }

            outputFile = new File(apkStorage, "TASK_REPORT.xlsx");//Create Output file in Main File
            //Create New File if not present
            // outputFile = new File(getExternalFilesDir(),,year+"_"+month+".pdf");
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "File Created");
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            WorkbookSettings wbSettings = new WorkbookSettings();

            wbSettings.setLocale(new Locale("en", "EN"));

            WritableWorkbook workbook;

            try {
                int a = 1;
                workbook = Workbook.createWorkbook(outputFile, wbSettings);
                //workbook.createSheet("Report", 0);
                WritableSheet sheet = workbook.createSheet("Task_Report", 0);


                Label label = new Label(0, 0, "TASK CODE");
                Label label1 = new Label(1, 0, "STATUS");
                Label label2 = new Label(2, 0, "START TIME");
                Label label3 = new Label(3, 0, "END TIME");


                sheet.addCell(label);
                sheet.addCell(label1);
                sheet.addCell(label2);
                sheet.addCell(label3);

                int count = 1;
                for (int i = 0; i < jsonObjects[0].size(); i++) {

                    Label label4 = new Label(0, count, jsonObjects[0].get(i).getAsJsonObject().get("task_code").isJsonNull() ? "" : jsonObjects[0].get(i).getAsJsonObject().get("task_code").getAsString());
                    Label label5 = new Label(1, count, jsonObjects[0].get(i).getAsJsonObject().get("status").isJsonNull() ? "" : jsonObjects[0].get(i).getAsJsonObject().get("status").getAsString());
                    Label label6 = new Label(2, count, jsonObjects[0].get(i).getAsJsonObject().get("start_time").isJsonNull() ? "" : FragmentReport.dateFormate(jsonObjects[0].get(i).getAsJsonObject().get("start_time").getAsString()));
                    Label label7 = new Label(3, count, jsonObjects[0].get(i).getAsJsonObject().get("end_time").isJsonNull() ? "" : FragmentReport.dateFormate(jsonObjects[0].get(i).getAsJsonObject().get("end_time").getAsString()));

                    sheet.addCell(label4);
                    sheet.addCell(label5);
                    sheet.addCell(label6);
                    sheet.addCell(label7);
                    count++;


                }


                workbook.write();


                try {
                    workbook.close();
                } catch (WriteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Dailog.DialogForServiceDown(context).show();
                }
                //createExcel(excelSheet);
            } catch (IOException | WriteException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Dailog.DialogForServiceDown(context).show();
            }


            try {

                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Dailog.DialogForServiceDown(context).show();
            }


            notification.setContentText("Download complete");
            notificationManagerCompat.notify(1, notification.build());


            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(String flag) {
            super.onPostExecute(flag);

        }
    }


    class GetStatusCount extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    "Please Wait",
                    "Wait for moments");

        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            getstatusCount(jsonObjects[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    public void getstatusCount(JsonObject jsonObject) {

        Log.d(TAG, jsonObject.toString());
        compositeDisposable.add(api.getstatusCount(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: response :: " + jsonElements.toString());
                                   dataAsJsonArray2 = jsonElements.getAsJsonArray("data");
                                   Log.d(TAG, "Array: dataAsJsonArray :: " + " " + dataAsJsonArray2);
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       int i = 0;
                                       for (JsonElement statusjson : dataAsJsonArray2) {
                                           Obj = statusjson.getAsJsonObject();
                                           Log.d(TAG, "Array: Obj :: " + " " + Obj);
                                           status[i] = new String(Obj.get("status").toString());
                                           statusCount[i] = new Integer(Obj.get("count").getAsInt());
                                           Log.d(TAG, "Array: response in loop :: " + " " + i
                                                   + " " + status[i] + " " + statusCount[i]);
                                           i++;
                                       }
                                       Pie pie = AnyChart.pie();
                                       List<DataEntry> dataEntries = new ArrayList<>();
                                       if (dataAsJsonArray2.size() == 0) {
                                           Toast.makeText(context, "Data Not Found", Toast.LENGTH_SHORT).show();
                                       }
                                       for (i = 0; i < status.length; i++) {
                                           if ((statusCount[i]) != 0) {
                                               dataEntries.add(new ValueDataEntry(status[i], statusCount[i]));
                                               color = new String[]{"#ff6500", "#008000", "#FF0000"};
                                               if (status[i].contains("InProcess")) {
                                                   color[i] = "#ff6500";
                                               } else if (status[i].contains("Completed")) {
                                                   color[i] = "#008000";
                                               } else if (status[i].contains("Hold")) {
                                                   color[i] = "#FF0000";
                                               } else {
                                                   color[i] = "";
                                               }
                                               pie.palette(color);
                                               //pie.palette(new String[]{"#074cad", "#62ad07", "#f7a80a"});
                                               // pie.palette(new String[]{"#90caf9", "#80cbc4", "#aed581", "#e6ee9c", "#ffcc80"});
                                           }
                                       }
                                       pie.data(dataEntries);
                                       pie.legend().title().fontColor("black");
                                       // pie.title().text("Assign Task Status").fontSize("17");
                                       pie.fill("aquastyle");
                                       //  pie.sort("asc");
                                       //pie.labels().position("outside");

                                       pie.legend().title().enabled(true);

                                       pie.legend().title().text("Assigned Tasks")
                                               .padding(0d, 0d, 10d, 0d);

                                       pie.legend()
                                               .position("center-bottom")
                                               .itemsLayout(LegendLayout.HORIZONTAL)
                                               .align(Align.CENTER).fontColor("black");


                                       anyChartView.setChart(pie);


                                       Log.d(TAG, "accept:  Service Call Successful..");
                                   } else {

                                       Log.d(TAG, "accept:  Service Call Successful..Error.........");
                                       Dailog.DialogForServiceDown(context).show();
                                   }

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d(TAG, "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                   Dailog.DialogForServiceDown(context).show();
                               }
                           }


                )
        );

    }


    /*Enable options menu in this fragment*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        //inflate menu
       inflater.inflate(R.menu.dashboard, menu);
        //hide item
        menu.findItem(R.id.exportToExcel).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.exportToExcel) {
            if (dataAsJsonArray1.size() == 0) {
                Toast.makeText(context, "NO Report Found.", Toast.LENGTH_SHORT).show();
            } else {


                if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                    Toast.makeText(context, "You don't have storage. ", Toast.LENGTH_SHORT).show();
                }

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.d(TAG, "onClick: second time and more.");
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_CODE);

                    } else {
                        if (SharedPrefManager.getInstance(getContext()).isFirstTimeAsking(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            SharedPrefManager.getInstance(getContext()).firstTimeAsking(Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                        } else {
                            //Permission disable by device policy or user denied permanently. Show proper error message
                            DialogForAllowFromSettings(getContext()).show();
                            //ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                        }

                    }
                } else {
                    //already have permission


                    new DownloadPDFAsync().execute(dataAsJsonArray1);
                }
            }

        }


        return super.onOptionsItemSelected(item);
    }

    public static String dateFormate(String date) throws ParseException {

//        SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        Date data = sdfDB.parse(date);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        return sdf.format(date);
        return date;
    }

}
