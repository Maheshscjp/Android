package com.example.login.ui.timesheet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.login.R;
import com.example.login.SharedPrefManager;
import com.example.login.User;

import java.util.ArrayList;
import java.util.List;

public class timesheet extends Fragment {

    private TimesheetViewModel mViewModel;

    private Activity context;
    ListView list;
    private List<String> List_file;
    User LoggedInUser;

    public static timesheet newInstance() {
        return new timesheet();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel =  ViewModelProviders.of(this).get(TimesheetViewModel.class);
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);
        context=getActivity();

        LoggedInUser = SharedPrefManager.getInstance(context).getUser();

        if(LoggedInUser.getRole().equals("Employee")) {
            List_file =new ArrayList<String>();
            list = (ListView)view.findViewById(R.id.listview);
            List_file.add("Task List");

            list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,List_file));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    //args2 is the listViews Selected index

                    if(arg2 == 0){

                        Intent intent = new Intent(context, TaskListActivity.class);

                        startActivity(intent);

                    }


                }
            });
            return view;

        }
        else {
            List_file = new ArrayList<String>();
            list = (ListView) view.findViewById(R.id.listview);

            List_file.add("Assign Task");
            List_file.add("Track Task");

            list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, List_file));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    //args2 is the listViews Selected index

                    if (arg2 == 0) {

                        Intent intent = new Intent(context, AssignedTaskActivity.class);
//
                        startActivity(intent);
                    }

                    if(arg2 == 1){

                        Intent intent = new Intent(context, TaskTrackActivity.class);
                        startActivity(intent);

                   }
                }
            });
            return view;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TimesheetViewModel.class);
        // TODO: Use the ViewModel


    }

}
