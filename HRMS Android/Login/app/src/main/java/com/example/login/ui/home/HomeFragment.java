package com.example.login.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.example.login.ui.user_management.UserManagementFragment;
import com.example.login.util.Dailog;
import com.example.login.util.ListViewHomeFragment;
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

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private Activity context;
    User LoggedInUser;
    ListView list;
    AnyChartView anyChartView;
    String[] status = new String[3];
    int[] statusCount = new int[3];
    String[] color = new String[3];
    NodeJS api;
    JsonObject Obj = null;
    CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;
    JsonArray dataAsJsonArray;
    NotificationManagerCompat notificationManagerCompat;
    private static final String TAG = "HomeFragment";
    JsonObject Json1 = new JsonObject();
    private List<String> List_file;
    View view;
    boolean isEnable = false;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context = getActivity();
        LoggedInUser = SharedPrefManager.getInstance(context).getUser();

        notificationManagerCompat = NotificationManagerCompat.from(getContext());
        view = inflater.inflate(R.layout.activity_home, container, false);
        if (LoggedInUser.getRole().equals("WAD")) {

//            FragmentManager fm = getFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            UserManagementFragment llf = new UserManagementFragment();
//            ft.replace(R.id.showHome, llf);
//            ft.commit();

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.home_fragment, new UserManagementFragment(), "NewFragmentTag");
            ft.commit();
        } else {

            Retrofit retroFit = RetroFitClient.getInstance();
            api = retroFit.create(NodeJS.class);
            compositeDisposable = new CompositeDisposable();
            JsonObject Json = new JsonObject();
            Json.addProperty("user_id", LoggedInUser.getUserid());
            Json.addProperty("role", LoggedInUser.getRole());
            new GetStatusCount().execute(Json);
            Log.d(TAG, "onCreate: request :: " + Json.toString());
            anyChartView = view.findViewById(R.id.chart);
            anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

            List_file = new ArrayList<String>();
            list = view.findViewById(R.id.listview);


        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }


    class GetStatusCount extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    "Please Wait",
                    "Wait for moments");

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            getstatusCount(jsonObjects[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Json1.addProperty("user_id", LoggedInUser.getUserid());
            Json1.addProperty("role", LoggedInUser.getRole());
            Log.d(TAG, "onCreate: Test :: " + Json1);
            new GetPendingTaskDetails().execute(Json1);


        }
    }

    class GetPendingTaskDetails extends AsyncTask<JsonObject, String, String> {

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            compositeDisposable.add(api.getPendingTaskDetails(jsonObjects[0]).subscribeOn(
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

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            progressDialog.dismiss();

        }
    }

    private void CreateListView(JsonObject jsonObject) throws JSONException, InterruptedException {

        Log.d(TAG, "CreateListView: data of list ::" + jsonObject.toString());
        long timeToSleep = 20L;
        dataAsJsonArray = jsonObject.getAsJsonArray("data");
        for (JsonElement LeaveList : dataAsJsonArray) {
            JsonObject Obj = LeaveList.getAsJsonObject();
            Log.d(TAG, "CreateListView: Json Data Leave ::" + Obj.toString());
            String fname = (Obj.get("firstname") == null) ? " " : Obj.get("firstname").getAsString();
            String lname = (Obj.get("lastname") == null) ? " " : Obj.get("lastname").getAsString();
            Log.d("Fname Lname :: ", fname + " - " + lname);
            List_file.add(Obj.get("task_code").getAsString() + " - " + fname + " " + lname + " : " + Obj.get("deviation").getAsString());
        }
        ListViewHomeFragment adapter = new ListViewHomeFragment(context, R.layout.home_fragment, R.id.txt, List_file);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "selected task has time exceeded..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getstatusCount(JsonObject jsonObject) {

        Log.d(TAG, jsonObject.toString());
        compositeDisposable.add(api.getstatusCount(jsonObject).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                               @Override
                               public void accept(JsonObject jsonElements) throws Exception {
                                   Log.d(TAG, "accept: response :: " + jsonElements.toString());
                                   dataAsJsonArray = jsonElements.getAsJsonArray("data");
                                   Log.d(TAG, "Array: dataAsJsonArray :: " + " " + dataAsJsonArray);
                                   if (jsonElements.get("success").getAsBoolean()) {
                                       int i = 0;
                                       for (JsonElement statusjson : dataAsJsonArray) {
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
                                       if (dataAsJsonArray.size() == 0) {
                                           Toast.makeText(context, "Data Not Found", Toast.LENGTH_SHORT).show();
                                       }
                                       color = new String[]{"#ff6500", "#008000", "#FF0000"};
                                       for (i = 0; i < status.length; i++) {
                                           if ((statusCount[i]) != 0) {
                                               dataEntries.add(new ValueDataEntry(status[i], statusCount[i]));


                                               if (status[i].contains("InProcess") && statusCount[i] != 0) {
                                                   color[i] = "#ff6500";
                                               } else if (status[i].contains("Completed") && statusCount[i] != 0) {
                                                   color[i] = "#008000";
                                               } else if (status[i].contains("Hold") && statusCount[i] != 0) {
                                                   color[i] = "#FF0000";
                                               }


                                           }
                                       }
                                       Log.d("Color", color.toString());
                                       pie.palette(color);
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
