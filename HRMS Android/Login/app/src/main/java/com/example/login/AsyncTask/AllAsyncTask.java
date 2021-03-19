package com.example.login.AsyncTask;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;


public class AllAsyncTask extends AsyncTask<JsonObject, String, JsonObject> {


    private Activity activityContext;


    ProgressDialog progressDialog;

    public interface AsynDo {
        JsonObject processTask(JsonObject jsonObjectRequest);

        void processFinish(JsonObject output);
    }

    public AsynDo asynDo = null;

    public AllAsyncTask(Activity activityContext, AsynDo asynDo) {
        this.activityContext = activityContext;
        this.asynDo = asynDo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activityContext,
                "Please Wait",
                "Wait for moments");
    }

    @Override
    protected JsonObject doInBackground(JsonObject... jsonObjects) {
        JsonObject response = new JsonObject();
        try {
            Thread.sleep(10000);

            response = asynDo.processTask(jsonObjects[0]);
            Log.d("TEST", "doInBackground: response ::" + response.toString());
        } catch (Exception e) {
            Log.d("TEST", "doInBackground: error ::" + e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(JsonObject jsonObject) {
        super.onPostExecute(jsonObject);
        progressDialog.dismiss();
        asynDo.processFinish(jsonObject);
    }


}

