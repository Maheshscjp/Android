package com.example.login.ui.scan_product;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.login.NodeJS;
import com.example.login.R;
import com.example.login.RetroFitClient;
import com.example.login.User;
import com.example.login.util.Dailog;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ScannedBarcodeActivity extends AppCompatActivity {
    private static final String TAG = "ScannedBarcodeActivity";

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    boolean isEmail = false;
    static final int CUSTOM_DIALOG_ID_SUCCESS = 0;
    TextView code, name, qty, add;
    NodeJS api;
    CompositeDisposable compositeDisposable;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    private void initViews() {
        Retrofit retroFit = RetroFitClient.getInstance();
        api = retroFit.create(NodeJS.class);
        compositeDisposable = new CompositeDisposable();
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);

    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;

                            } else {
                                isEmail = false;

                                intentData = barcodes.valueAt(0).displayValue;
                                Log.e(TAG, "data inside barcode :: " + intentData);
                                if (intentData.equals("")) {
                                    Toast.makeText(ScannedBarcodeActivity.this, "No data Found.", Toast.LENGTH_SHORT).show();
                                } else {

                                    String[] data;
                                    data = intentData.split(";");

                                    if (data.length == 4) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("code", data[0].split(":")[1]);
                                        bundle.putString("name", data[1].split(":")[1]);
                                        bundle.putString("quantity", data[2].split(":")[1]);
                                        bundle.putString("address", data[3].split(":")[1]);

                                        showDialog(CUSTOM_DIALOG_ID_SUCCESS, bundle);
                                        txtBarcodeValue.setText(intentData);
                                    }
                                    else
                                    {
                                        Toast.makeText(ScannedBarcodeActivity.this, "Invalid data Found.", Toast.LENGTH_SHORT).show();
                                    }


                                }

                            }
                        }
                    });

                }
            }
        });
    }

    class SubmitScanDataAsync extends AsyncTask<JsonObject, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ScannedBarcodeActivity.this,
                    "Please Wait",
                    "Wait for moments");
        }

        @Override
        protected String doInBackground(JsonObject... jsonObjects) {
            compositeDisposable.add(api.submitScanData(jsonObjects[0]).subscribeOn(
                    Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<JsonObject>() {
                                   @Override
                                   public void accept(JsonObject jsonElements) throws Exception {
                                       Log.d(TAG, "accept: response :: " + jsonElements.toString());
                                       if (jsonElements.get("success").getAsBoolean()) {

                                           dismissDialog(CUSTOM_DIALOG_ID_SUCCESS);
                                           Toast.makeText(ScannedBarcodeActivity.this, "Data Submitted.", Toast.LENGTH_SHORT).show();
                                       } else {

                                           Log.d(TAG, "accept:  Service Call Successful..Error.........");
                                           Dailog.DialogForServiceDown(ScannedBarcodeActivity.this).show();
                                       }
                                   }
                               }, new Consumer<Throwable>() {
                                   @Override
                                   public void accept(Throwable throwable) throws Exception {
                                       Log.d(TAG, "accept:  Service Call Failed..error :: " + throwable.getMessage());
                                       Dailog.DialogForServiceDown(ScannedBarcodeActivity.this).show();
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


    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;

        switch (id) {
            case CUSTOM_DIALOG_ID_SUCCESS:
                dialog = new Dialog(ScannedBarcodeActivity.this);
                dialog.setContentView(R.layout.dialoglayout_scan);

                code = (TextView) dialog.findViewById(R.id.code);
                name = (TextView) dialog.findViewById(R.id.name);
                qty = (TextView) dialog.findViewById(R.id.qty);
                add = (TextView) dialog.findViewById(R.id.add);


                Button dialog_OK = (Button) dialog.findViewById(R.id.OkButton);
                dialog_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JsonObject req = new JsonObject();
                        req.addProperty("code", code.getText().toString());
                        req.addProperty("name", name.getText().toString());
                        req.addProperty("quantity", qty.getText().toString());
                        req.addProperty("address", add.getText().toString());
                        new SubmitScanDataAsync().execute(req);

                    }
                });
                Button dialog_cancel = (Button) dialog.findViewById(R.id.cancelButton);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissDialog(CUSTOM_DIALOG_ID_SUCCESS);
                    }
                });
                break;


        }

        return dialog;
    }


    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {

        super.onPrepareDialog(id, dialog, bundle);

        switch (id) {
            case CUSTOM_DIALOG_ID_SUCCESS:


                code.setText(bundle.getString("code"));
                name.setText(bundle.getString("name"));
                qty.setText(bundle.getString("quantity"));
                add.setText(bundle.getString("address"));


                break;


        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();


    }

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
