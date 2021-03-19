package com.example.login.ui.scan_product;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.R;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanProductFragment extends Fragment implements View.OnClickListener{

    private ScanProductViewModel mViewModel;

    Button  btnScanBarcode;
    public static ScanProductFragment newInstance() {
        return new ScanProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.scan_product_fragment, container, false);

        btnScanBarcode = view.findViewById(R.id.btnScanBarcode);

        btnScanBarcode.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ScanProductViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnScanBarcode:
                startActivity(new Intent(getContext(), ScannedBarcodeActivity.class));
                break;
        }

    }
}
