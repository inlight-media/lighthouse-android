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
    private static String APP_ID = "53d0a03cd4f4b8f4296b022d";
    private static String APP_KEY = "03cd33f642b2e8aec636a20f8101d52f8dd11e60";
    private static String APP_TOKEN = "8734fd703dd22c9b4b7c2b40c3ba786cc284fbf3";
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
        }
    }
}
