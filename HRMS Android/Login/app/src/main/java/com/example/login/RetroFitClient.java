package com.example.login;

import android.app.Application;

import com.example.login.util.ApplicationContext;
import com.example.login.util.ConfigProperties;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroFitClient {

    private static Retrofit instance;
    public static Retrofit getInstance(){
            if (instance == null)

             //instance = new Retrofit.Builder().baseUrl("http://10.0.2.2:3030/")
        //instance = new Retrofit.Builder().baseUrl("http://192.168.0.117:3000/")
         // .addConverterFactory(ScalarsConverterFactory.create())
                instance = new Retrofit.Builder().baseUrl(ConfigProperties.getProperty("baseurl", ApplicationContext.getAppContext()))
                .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

        return instance;
    }
}
