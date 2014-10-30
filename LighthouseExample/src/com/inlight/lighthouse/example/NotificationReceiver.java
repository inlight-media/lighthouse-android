package com.inlight.lighthouse.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.inlight.lighthousesdk.LighthouseNotification;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        LighthouseNotification notification = (LighthouseNotification)intent.getParcelableExtra("notification");
        Log.d("NotificationReceiver",notification.toString());
        ((ExampleApplication)context.getApplicationContext()).getLighthouseManager().campaign(notification);
    }
}
