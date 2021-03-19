package com.example.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddUserActivity extends AppCompatActivity {
Button addEmp, addTL;
    ListView list;
    private List<String> List_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
//from activity to fragment on back button click
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        List_file =new ArrayList<String>();
        list = (ListView)findViewById(R.id.listview);

        List_file.add("Add Employee");
        List_file.add("Add Manager");

        list.setAdapter(new ArrayAdapter<String>(AddUserActivity.this, android.R.layout.simple_list_item_1,List_file));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                //args2 is the listViews Selected index

                if(arg2 == 0){
                    //Intent intent = new Intent(AddUserActivity.this, AddEmployeeActivity.class);
                    Intent intent = new Intent(AddUserActivity.this, AddActivity.class);
                    startActivity(intent);

                }

                if(arg2 == 1){
                   // Intent intent = new Intent(AddUserActivity.this, AddTLActivity.class);
                    Intent intent = new Intent(AddUserActivity.this, AddActivity.class);
                    startActivity(intent);

                }

            }
        });





    }
//from activity to fragment on back button click
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            super.onOptionsItemSelected(item);
            switch (item.getItemId()) {
                    case android.R.id.home:
                    finish();
                    break;
            }

            return true;
        }






}
