package com.example.login.ui.asset_management;

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
import android.widget.ListView;

import com.example.login.GetLeaveRequestActivity;
import com.example.login.LeaveTracker;
import com.example.login.R;
import com.example.login.SharedPrefManager;
import com.example.login.User;
import com.example.login.asset.Asset;
import com.example.login.asset.AssetWithUserActivity;

import java.util.ArrayList;
import java.util.List;

public class AssetManagementFragment extends Fragment {

    private AssetManagementViewModel mViewModel;
    private Activity context;
    User LoggedInUser;
    ListView list;
    private List<String> List_file;
    public static AssetManagementFragment newInstance() {
        return new AssetManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        context=getActivity();
        LoggedInUser = SharedPrefManager.getInstance(context).getUser();
        View view =   inflater.inflate(R.layout.asset_management_fragment, container, false);
        if(LoggedInUser.getRole().equals("Admin")) {
            List_file =new ArrayList<String>();
            list = (ListView)view.findViewById(R.id.assetManagementList);

            List_file.add("All Assets");
            List_file.add("Assets With User");


            list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,List_file));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    //args2 is the listViews Selected index

                    if(arg2 == 0) {
                       Intent intent = new Intent(context, Asset.class);
                        startActivity(intent);
                    }
                   else if(arg2 == 1){
                        Intent intent = new Intent(context, AssetWithUserActivity.class);
                        startActivity(intent);

                    }

                }
            });


        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AssetManagementViewModel.class);
        // TODO: Use the ViewModel
    }

}
