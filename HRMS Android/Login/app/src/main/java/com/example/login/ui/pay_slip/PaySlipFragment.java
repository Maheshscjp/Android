package com.example.login.ui.pay_slip;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.login.DeleteUserActivity;
import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.example.login.util.Dailog;
import com.example.login.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;
//change in sharedpreference.
public class PaySlipFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private PaySlipViewModel mViewModel;
    private Activity context;
    private Button download;
    Spinner spinnerUser, spinnerYear,spinnerMonth;
    User LoggedInUser;
    ProgressDialog progressDialog;
    private static final String TAG = "PaySlipFragment";
    NodeJS api;
    CompositeDisposable compositeDisposable;
    JsonArray dataAsJsonArray;
    boolean first = true, second = true, third = true;
    String year="", month="", email="";
    public static final String channel_id="channel_id";
NotificationManagerCompat notificationManagerCompat;
String downloadFileName;
    private static final int PERMISSION_REQUEST_CODE = 200;
    public static PaySlipFragment newInstance() {
        return new PaySlipFragment();
    }





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context=getActivity();
        LoggedInUser = SharedPrefManager.getInstance(context).getUser();
        notificationManagerCompat = NotificationManagerCompat.from(getContext());

        View view =  inflater.inflate(R.layout.pay_slip_fragment, container, false);
        download = view.findViewById(R.id.download);
        spinnerUser = view.findViewById(R.id.spinnerUser);

        spinnerYear = view.findViewById(R.id.spinnerYear);

        spinnerMonth= view.findViewById(R.id.spinnerMonth);

        spinnerYear.setOnItemSelectedListener(this);
        spinnerMonth.setOnItemSelectedListener(this);



       // Init API
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();


        download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                JsonObject Jsonobject = new JsonObject();
                Jsonobject.addProperty("year",year);
                Jsonobject.addProperty("month", month);
                Jsonobject.addProperty("email", email);
                if (TextUtils.isEmpty(Jsonobject.get("email").getAsString())) {


                    Toast.makeText(context, "Please select User", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(Jsonobject.get("year").getAsString())){
                    Toast.makeText(context, "Please select Year", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(Jsonobject.get("month").getAsString())){
                    Toast.makeText(context, "Please select Month", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!isExternalStorageAvailable() || isExternalStorageReadOnly() )
                    {
                        Toast.makeText(context, "You don't have storage. ", Toast.LENGTH_SHORT).show();
                    }

                     if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)    != PackageManager.PERMISSION_GRANTED){
                         if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                             Log.d(TAG, "onClick: second time and more.");
                             ActivityCompat.requestPermissions(getActivity(),
                                     new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                     PERMISSION_REQUEST_CODE);

                         }
                         else{    if(SharedPrefManager.getInstance(getContext()).isFirstTimeAsking( Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                             SharedPrefManager.getInstance(getContext()).firstTimeAsking(Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
                             ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                         } else {
                             //Permission disable by device policy or user denied permanently. Show proper error message
                            DialogForAllowFromSettings(getContext()).show();
                             //ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                         }

                         }
                    }

                    else {
                        //already have permission

                        new DownloadPDFAsync().execute(Jsonobject);
                    }


                }
            }
        });

        if(LoggedInUser.getRole().equals("Admin")) {

            JsonObject Json = new JsonObject();
            Json.addProperty("requiredUser", "Employee','Manager");
            new GetManEmpListAsync().execute(Json);

            return view;

        }
        else{

            List<String> list = new ArrayList<String>();
            list.add(LoggedInUser.getFirstname()+" "+LoggedInUser.getLastname());
            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, list);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUser.setAdapter(dataAdapter2);
            spinnerUser.setEnabled(false);
            email = LoggedInUser.getEmail();

            return view;

        }





    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PaySlipViewModel.class);
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

            getManEmpList(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }


    class DownloadPDFAsync extends AsyncTask<JsonObject, Integer, String> {


        //notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext(), channel_id)
                .setContentTitle("Downloading Pay Slip")
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
                NotificationChannel notificationChannel = new NotificationChannel(channel_id , "channel_name", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                NotificationManager notificationManager = getSystemService(getContext(),NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }


           // notification.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
           // notificationManagerCompat.notify(1, notification.build());
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {


            compositeDisposable.add(api.downloadPDF(jsonObjects[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: Response ::" + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean()) {
                                           Log.d(TAG, "accept:  Service Call Successful..");
                                           Toast.makeText(getContext(), "Downloading Start.", Toast.LENGTH_SHORT).show();
                                           notificationManagerCompat.notify(1, notification.build());
                                          // apkStorage = new File(Environment.getExternalStorageDirectory()+"/"+Utils.downloadDirectory);// internal Storage of mobile i.e. 32 GB, 64 GB.
                                          apkStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                           if (!apkStorage.exists()) {
                                               apkStorage.mkdir();
                                               Log.e(TAG, "Directory Created.");
                                           }

                                           outputFile = new File(apkStorage,year+"_"+month+".pdf");//Create Output file in Main File
                                           //Create New File if not present
                                          // outputFile = new File(getExternalFilesDir(),,year+"_"+month+".pdf");
                                           if (!outputFile.exists()) {
                                               outputFile.createNewFile();
                                               Log.e(TAG, "File Created");
                                           }
                                           FileOutputStream fos = new FileOutputStream(outputFile);
                                           byte[] pdfAsBytes = Base64.decode(jsonElements.get("data").getAsString(), 0);
                                           // publishProgress((int) ((i / (float) count) * 100));
                                           fos.write(pdfAsBytes);

                                           fos.flush();
                                           fos.close();
                                           Toast.makeText(getContext(), "Downloading Complete.", Toast.LENGTH_SHORT).show();

                                           notification.setContentText("Download complete");
                                           notificationManagerCompat.notify(1, notification.build());
                                       } else {

                                           Toast.makeText(getContext(), "Please Contact Administrator.", Toast.LENGTH_SHORT).show();

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
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
           // notification.setProgress(PROGRESS_MAX, progress[0], false);
           // notificationManagerCompat.notify(1, notification.build());
        }

        @Override
        protected void onPostExecute(String flag) {
            super.onPostExecute(flag);


           /*
                Toast.makeText(getContext(), "Pay Slip Downloaded Successfully.", Toast.LENGTH_SHORT).show();
                notification.setContentText("Download complete");
                       // .setProgress(0,0,false);
                notificationManagerCompat.notify(1, notification.build());*/



        }
    }


    public void getManEmpList(JsonObject Json) {

        compositeDisposable.add(api.getSuperUserList(Json).subscribeOn(
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


    }

   public void addItemsOnSpinnerUser(JsonObject data) {
        List<String> list = new ArrayList<String>();
        list.add("select");
        dataAsJsonArray = data.getAsJsonArray("data");
        for (JsonElement TLList : dataAsJsonArray) {
            JsonObject Obj = TLList.getAsJsonObject();

            list.add(Obj.get("firstname").getAsString() + " " + Obj.get("lastname").getAsString());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUser.setAdapter(dataAdapter);
        spinnerUser.setOnItemSelectedListener(this);



    }

@Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
       Context context1 = parent.getContext();
        switch (parent.getId()) {
            case R.id.spinnerYear:
                if (second) {
                    second = false;
                }
                else {

                    if (pos == 0) {


                        Toast.makeText(context1, "Please select Year", Toast.LENGTH_SHORT).show();

                    } else {

                        year = spinnerYear.getSelectedItem().toString();
                        Log.d(TAG, "onItemSelected: selected year ::"+year);





                    }
                }
                break;
            case R.id.spinnerMonth:
                if (third) {
                    third = false;
                } else {

                    if (pos == 0) {
                        Toast.makeText(context1, "Please select Month", Toast.LENGTH_SHORT).show();
                    } else {
                       month= spinnerMonth.getSelectedItem().toString();
                        Log.d(TAG, "onItemSelected: selected month ::"+month);




                    }
                }
                break;

            case R.id.spinnerUser:
                if (first) {
                    first = false;
                } else {

                    if (pos == 0) {
                        Toast.makeText(context1, "Please select User", Toast.LENGTH_SHORT).show();
                    } else {
                        pos--;
                        JsonElement data = dataAsJsonArray.get(pos);
                        JsonObject dataObj = data.getAsJsonObject();
                        Log.d(TAG, "onItemSelected: selected user data :"+dataObj.toString());
                        email = dataObj.get("email").getAsString();/////// change to email id............

                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d(TAG, "onNothingSelected: Please select ");
        Context context2 = parent.getContext();
        switch (parent.getId()) {

            case R.id.spinnerUser :
                Toast.makeText(context2, "Please select User", Toast.LENGTH_SHORT).show();
                break;
            case R.id.spinnerYear:
                Toast.makeText(context2, "Please select Year", Toast.LENGTH_SHORT).show();
                break;
            case R.id.spinnerMonth:
                Toast.makeText(context2, "Please select Month", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }

    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult:  PERMISSION_REQUEST_CODE"+requestCode);
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission is granted.", Toast.LENGTH_SHORT).show();
                } else {
/*
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);*/
                    Toast.makeText(context, "Permission is denied.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;




        }
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


}
