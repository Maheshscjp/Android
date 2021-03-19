package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView UserName;
    Button LogOut;


    DBHandler dbHandler = new DBHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserName = (TextView) findViewById(R.id.UserName);


        User LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        String username = null;
        Button button_AddEmp = findViewById(R.id.button_AddEmp);
        if (LoggedInUser.getRole().equals("employee")) {

            //Remove component from UI
            ((ViewGroup) button_AddEmp.getParent()).removeView(button_AddEmp);
        }


        if (LoggedInUser != null) {
            username = LoggedInUser.getFirstname();
            UserName.setText("Welcome " + username);
        }

        final ListView list = findViewById(R.id.ListView);


        ArrayList<String> arrayList = dbHandler.GetTaskList();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) list.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, clickedItem, Toast.LENGTH_LONG).show();
            }
        });

        LogOut = findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

      /*  button_AddEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEmployee.class);
                startActivity(intent);
            }
        });*/


    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}