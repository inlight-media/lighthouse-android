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
    private static String APP_ID = "533bb440d3384b8a6a000012";
    private static String APP_KEY = "ecc4064ac5c71ef9a72d1c866a6dcabb6b47fbde";
    private static String APP_TOKEN = "528cd8f4fbb11e20d657889c40db967b8d5aa09f";
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
