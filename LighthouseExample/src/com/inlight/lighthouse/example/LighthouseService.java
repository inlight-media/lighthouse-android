package com.inlight.lighthouse.example;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.inlight.lighthousesdk.LighthouseManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LighthouseService extends Service {
    private LighthouseManager lighthouseManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lighthouseManager = ((ExampleApplication) this.getApplicationContext()).getLighthouseManager();
        JSONObject properties = new JSONObject();
        try {
            properties.put("property", "example");
            lighthouseManager.setProperties(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lighthouseManager.launch();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved( Intent rootIntent ) {
        lighthouseManager.onTaskRemoved();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lighthouseManager.reset();
    }
}
