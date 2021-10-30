package com.example.hackathon;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class GlobalEnvs extends Application {
    public static String BASE_URL = null;
    public static SharedPreferences sharedPreferences = null;

    @Override
    public void onCreate() {
        super.onCreate();
        BASE_URL = "http://192.168.43.160:5000/";
        sharedPreferences = this.getSharedPreferences("com.example.hackathon.sharedpreferences", Context.MODE_PRIVATE);
    }
}
