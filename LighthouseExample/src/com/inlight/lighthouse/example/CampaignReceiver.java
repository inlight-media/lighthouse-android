package com.inlight.lighthouse.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.inlight.lighthousesdk.CampaignData;
import com.inlight.lighthousesdk.LighthouseNotification;

public class CampaignReceiver extends BroadcastReceiver {
    private static final String TAG = "CampaignReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        CampaignData campaign = (CampaignData)intent.getParcelableExtra("campaign");
        Log.d("CampaignReceiver",campaign.getCampaign().toString());
    }
}
