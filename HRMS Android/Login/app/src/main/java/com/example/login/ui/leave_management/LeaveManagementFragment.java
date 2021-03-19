package com.example.login.ui.leave_management;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.AddActivity;
import com.example.login.ApplyLeaveActivity;
import com.example.login.CheckLeaveStatusActivity;
import com.example.login.GetLeaveRequestActivity;
import com.example.login.LeaveTracker;
import com.example.login.R;
import com.example.login.SharedPrefManager;
import com.example.login.TableActivity;
import com.example.login.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class LeaveManagementFragment extends Fragment {

    private LeaveManagementViewModel mViewModel;

    private Button applyLeaveRequest;

    private Activity context;

    ListView list;
    private List<String> List_file;
    User LoggedInUser;

    public static LeaveManagementFragment newInstance() {
        return new LeaveManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context=getActivity();
        LoggedInUser = SharedPrefManager.getInstance(context).getUser();

        View view =  inflater.inflate(R.layout.leave_management_fragment, container, false);

        //Create an adapter for the listView and add the ArrayList to the adapter.



        if(LoggedInUser.getRole().equals("Admin")) {
            List_file =new ArrayList<String>();
            list = (ListView)view.findViewById(R.id.listview);


            List_file.add("Leave Request");
            List_file.add("Leave Tracker");


            list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,List_file));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    //args2 is the listViews Selected index

                    if(arg2 == 0) {
                        Intent intent = new Intent(context, GetLeaveRequestActivity.class);
                        startActivity(intent);
                    }
                    if(arg2 == 1){
                        Intent intent = new Intent(context, LeaveTracker.class);
                        startActivity(intent);

                    }
                }
            });
            return view;
        }

        if(LoggedInUser.getRole().equals("Employee")){
            List_file =new ArrayList<String>();
            list = (ListView)view.findViewById(R.id.listview);

/*
        CreateListView();*/
            List_file.add("Apply Leave");
            List_file.add("Leave History");

            list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,List_file));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    //args2 is the listViews Selected index


                    if(arg2 == 0){
                        Intent intent = new Intent(context, ApplyLeaveActivity.class);
                        startActivity(intent);

                    }

                    if(arg2 == 1){
                        Intent intent = new Intent(context, CheckLeaveStatusActivity.class);
                        startActivity(intent);

                    }






                }
            });
            return view;

        }

        List_file =new ArrayList<String>();
        list = (ListView)view.findViewById(R.id.listview);

/*
        CreateListView();*/
        List_file.add("Apply Leave");
        List_file.add("Leave History");
        List_file.add("Leave Request");


        list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,List_file));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                //args2 is the listViews Selected index



                if(arg2 == 0){
                    Intent intent = new Intent(context, ApplyLeaveActivity.class);
                    startActivity(intent);

                }

                if(arg2 == 1){
                    Intent intent = new Intent(context, CheckLeaveStatusActivity.class);
                    startActivity(intent);

                }
                if(arg2 == 2){
                    Intent intent = new Intent(context, GetLeaveRequestActivity.class);
                    startActivity(intent);

                }


            }
        });
        return view;

    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LeaveManagementViewModel.class);
        // TODO: Use the ViewModel
    }


}
