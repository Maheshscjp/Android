package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgreesBar extends AppCompatActivity {
    Button btn;
    private ProgressBar progressBar2;
    TextView txt;
    Integer count =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progrees_bar);


        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar2.setMax(100);
        progressBar2.setVisibility(View.INVISIBLE);
        btn = (Button) findViewById(R.id.button);
        btn.setText("Start");
        txt = (TextView) findViewById(R.id.textView);
        OnClickListener listener = new OnClickListener() {
            public void onClick(View view) {
                count =1;
                progressBar2.setVisibility(View.VISIBLE);
                progressBar2.setProgress(0);
                switch (view.getId()) {
                    case R.id.button:
                        new MyTask().execute(20);
                        break;
                }
            }
        };
        btn.setOnClickListener(listener);
    }



    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; count <= params[0]; count++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar2.setVisibility(View.GONE);
            txt.setText(result);
            btn.setText("Restart");
        }
        @Override
        protected void onPreExecute() {
            txt.setText("Task Starting...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            txt.setText("Running..."+ values[0]);
            progressBar2.setProgress(values[0]);
        }
    }

}
