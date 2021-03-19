package com.example.login.ui.user_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.login.AddActivity;
import com.example.login.R;
import com.example.login.UpdateEmployeeActivity;

import java.util.ArrayList;
import java.util.List;

public class UserManagementFragment extends Fragment {

    private Button addUser, delete_user;

    private Activity context;
    private UserManagementViewModel userManagementViewModel;

    ListView list;
    private List<String> List_file;

    public UserManagementFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        userManagementViewModel =  ViewModelProviders.of(this).get(UserManagementViewModel.class);
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);
        //final TextView textView = root.findViewById(R.id.textView7);
        context=getActivity();
        userManagementViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

              //  textView.setText(s);

            }
        });
        List_file =new ArrayList<String>();
        list = (ListView)view.findViewById(R.id.listview);

        List_file.add("Add User");
        List_file.add("Update User Details");

        list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,List_file));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                //args2 is the listViews Selected index

                if(arg2 == 0){
                    /*Intent intent = new Intent(context, AddUserActivity.class);*/
                    Intent intent = new Intent(context, AddActivity.class);
                    startActivity(intent);

                }

                if(arg2 == 1){
                  //  Intent intent = new Intent(context, DeleteUserActivity.class);
                    Intent intent = new Intent(context, UpdateEmployeeActivity.class);
                    startActivity(intent);

                }

            }
        });



        return view;
    }


}
