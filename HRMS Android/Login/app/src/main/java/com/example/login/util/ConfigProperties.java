package com.example.login.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    public static String getProperty(String key, Context context) {
        Properties properties = new Properties();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            Log.d("ConfigProperties", "getProperty: " + e.getMessage());
        }
        return properties.getProperty(key);
    }
}
