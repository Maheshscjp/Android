package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity


{

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        User LoggedInUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (LoggedInUser.getRole().equals("WAD")  ) {
            Menu menuNav=navigationView.getMenu();

            menuNav.findItem(R.id.nav_report).setVisible(false);
            menuNav.findItem(R.id.nav_timesheet).setVisible(false);
            menuNav.findItem(R.id.nav_scanproduct).setVisible(false);
        }

        if (LoggedInUser.getRole().equals("Employee")  || LoggedInUser.getRole().equals("Manager")) {

            //Remove MenuItem from drawer layout

            Menu menuNav=navigationView.getMenu();
//            MenuItem nav_item2 = menuNav.findItem(R.id.nav_user_management);
//            nav_item2.setVisible(false);

            MenuItem asset = menuNav.findItem(R.id.nav_report);
            asset.setVisible(false);
            if(LoggedInUser.getRole().equals("Manager")){
                asset.setVisible(true);
            }

            MenuItem userManagement = menuNav.findItem(R.id.nav_user_management);
            userManagement.setVisible(false);
            if(LoggedInUser.getRole().equals("Admin") || LoggedInUser.getRole().equals("WAD") ) {
                asset.setVisible(true);
            }

        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_timesheet
                ,R.id.nav_user_management
//                , R.id.nav_leave_management, R.id.nav_pay_slip_management
                ,R.id.nav_report,R.id.nav_scanproduct)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView Username = (TextView) headerView.findViewById(R.id.UserName);
        TextView UserEmail =(TextView) headerView.findViewById(R.id.userEmail);



        if (LoggedInUser != null) {

            Username.setText(LoggedInUser.getFirstname()+" "+LoggedInUser.getLastname());
            UserEmail.setText(LoggedInUser.getEmail());

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);


        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {  int id = item.getItemId();

        if (id == R.id.logoutApp) {
            Log.d("", "onCreateOptionsMenu: logout ");

            SharedPrefManager.getInstance(getApplicationContext()).logout();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.resetPassword) {
            Log.d("", "onCreateOptionsMenu: reset password ");
            Intent intent = new Intent(DashboardActivity.this, ResetPasswordActivity.class);// instead oof loginactivity call your reset password activirt
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
