package com.inlight.lighthouse.example;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.inlight.lighthousesdk.LighthouseConfig;
import com.inlight.lighthousesdk.LighthouseException;
import com.inlight.lighthousesdk.LighthouseManager;

public class ExampleApplication extends Application {
    private static final String TAG = "ExampleApplication";
    private LighthouseManager lighthouseManager;
    // setup configure
    private static String APP_ID = "5417cbdad6db8a8f4b13846b";
    private static String APP_KEY = "986471a7ee11583b0266851d8d7b62f883791bf7";
    private static String APP_TOKEN = "01c68e60a23d53b04128302686f049700341280f";
    @Override
    public void onCreate(){
        super.onCreate();
        configLighthouse();

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.d("ExampleApplication","Google Play services might not be installed or enabled on this device");
        }
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        lighthouseManager.terminate();
    }
    public LighthouseManager getLighthouseManager() {
        configLighthouse();
        return lighthouseManager;
    }

    private void configLighthouse(){
        if (lighthouseManager == null){
            lighthouseManager = LighthouseManager.getInstance(this);

            LighthouseConfig lighthouseConfig = new LighthouseConfig(
                    this.getApplicationContext(),
                    APP_ID,
                    APP_KEY,
                    APP_TOKEN);
            lighthouseManager.setLighthouseConfig(lighthouseConfig);
            lighthouseManager.debug = true;
        }
    }
}
