package com.inlight.lighthouse.example;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inlight.lighthousesdk.CampaignData;
import com.inlight.lighthousesdk.LighthouseManager;
import com.inlight.lighthousesdk.LighthouseNotification;
import com.inlight.lighthousesdk.LighthouseNotifier;
import com.inlight.lighthousesdk.LighthouseSettings;
import com.inlight.lighthousesdk.ibeacon.IBeacon;
import com.inlight.lighthousesdk.ibeacon.Region;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExampleActivity extends Activity {
    private LighthouseManager lighthouseManager;
    private TextView mTextView;
    private ListView mListView;
    private List< RangingBeacon > data;
    private static final String SENDER_ID = "877637997124";
    private LighthouseNotifier lighthouseNotifier = new LighthouseNotifier() {
        private Set<RangingBeacon> beaconInRange = new HashSet<RangingBeacon>();
        @Override
        public void LighthouseDidEnterBeacon(IBeacon beaconData) {
            beaconInRange.add(new RangingBeacon(beaconData,false));
            logToDisplay(getCurrentTime() + " Did enter iBeacon named " + beaconData.getProximityUuid() + " " + beaconData.getMajor() + " " + beaconData.getMinor() + "  " + beaconData.getAccuracy() + "  " + beaconData.getBluetoothAddress());
        }

        @Override
        public void LighthouseDidExitBeacon(IBeacon beaconData) {
            beaconInRange.remove(new RangingBeacon(beaconData,false));
            logToDisplay(getCurrentTime()+" Did exit iBeacon named " + beaconData.getProximityUuid() + " " + beaconData.getMajor() + " " + beaconData.getMinor() + "  " + beaconData.getAccuracy() + "  " + beaconData.getBluetoothAddress());
        }

        @Override
        public void LighthouseDidRangeBeacon(final Collection<IBeacon> beacons,
                                             Region region) {
            Set<RangingBeacon> updatedBeaconInRange = new HashSet<RangingBeacon>();
            for (RangingBeacon iBeacon : beaconInRange) {
                updatedBeaconInRange.add(iBeacon);
                if (beacons != null || beacons.size() == 0) {
                    for (IBeacon beacon : beacons){
                        if (beacon.equals(iBeacon)){
                            updatedBeaconInRange.remove(iBeacon);
                            updatedBeaconInRange.add(new RangingBeacon(beacon,true));
                        }
                    }
                }

            }
            data = new ArrayList< RangingBeacon >( updatedBeaconInRange );
            Collections.sort(data, new Comparator<RangingBeacon>() {
                @Override
                public int compare(final RangingBeacon o1, final RangingBeacon o2) {
                    return o1.getMinor() - o2.getMinor();
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String[] values = new String[data.size()];
                    int i = 0;
                    for (IBeacon ibeacon : data) {
                        values[i] = ibeacon.getMinor() + "    " + String.format("%.2f", ibeacon.getAccuracy());
                        i++;
                    }
                    MyAdapter adapter = new MyAdapter(ExampleActivity.this);
                    mListView.setAdapter(adapter);
                }
            });
        }

        @Override
        public void LighthouseDidReceiveCampaign(CampaignData campaignData) {
            logToDisplay(getCurrentTime()+" Did receive Campaign: notification = "
                    + campaignData.getLighthouseNotification().toString() + "campaign = " + campaignData.getCampaign().toString());
        }

        @Override
        public void LighthouseDidActionCampaign(LighthouseNotification notification) {
            logToDisplay(getCurrentTime()+" Did Action Campaign: "
                    + notification.toJSONObject().toString());
        }

        @Override
        public void LighthouseDidReceiveNotification(
                LighthouseNotification notification) {
            logToDisplay(getCurrentTime()+" Did received a notification: "
                    + notification.toJSONObject().toString());
            // Post notification of received message.
            NotificationManager mNotificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            Intent lighthouseIntent = new Intent(ExampleActivity.this, CampainActivity.class);
            lighthouseIntent.putExtra("campaignActioned", true);
            lighthouseIntent.putExtra("campaignNotification", notification.toJSONObject().toString());
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, lighthouseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    getApplicationContext()).setSmallIcon(R.drawable.ic_launcher)
                    .setContentInfo(notification.getBadge())
                    .setContentTitle("Lighthouse Manager Notification")
                    .setContentText(notification.getAlert());

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setTicker(notification.getAlert());
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(1, mBuilder.build());
            lighthouseManager.campaign(notification);
        }

        @Override
        public void LighthouseDidUpdateSettings(LighthouseSettings settings) {
            logToDisplay(getCurrentTime()+" Did Update Settings: "
                    + settings.isEnabled());
        }
    };

    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        mTextView = (TextView) ExampleActivity.this
                .findViewById(R.id.monitoringText);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mListView = (ListView) this.findViewById(R.id.beacon_list);


        lighthouseManager = ((ExampleApplication)this.getApplicationContext()).getLighthouseManager();
        JSONObject properties = new JSONObject();
        try {
            properties.put("age",30);
            properties.put("gender","female");
            lighthouseManager.setProperties(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lighthouseManager.setLightHouseNotifier(lighthouseNotifier);
        lighthouseManager.requestPushNotifications(SENDER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_pause:
                lighthouseManager.pause();
                return true;
            case R.id.action_reload:
                lighthouseManager.reload();
                return true;
            case R.id.action_update_scan:
                lighthouseManager.setBackgroundBetweenScanPeriod(0);
                lighthouseManager.setBackgroundScanPeriod(1100);
                lighthouseManager.updateScanPeriods();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lighthouseManager.terminate();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (lighthouseManager.isBound()) {
            lighthouseManager.setBackgroundMode(true);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        if (lighthouseManager.isBound()) {
            lighthouseManager.setBackgroundMode(false);
        } else {
            lighthouseManager.launch();
        }
    }

    public void onRequestClicked(View view) {
        lighthouseManager.requestPushNotifications(SENDER_ID);
        String regId = lighthouseManager.getRegistrationId();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, regId);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }
    public void onStopClicked(View view) {
        if (lighthouseManager.isBound()) {
            ((Button) view).setText("Start Service");
            lighthouseManager.terminate();
        } else {
            ((Button) view).setText("Stop Service");
            lighthouseManager.launch();
        }
    }


    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                mTextView.append(line + "\n");
            }
        });
    }

    static class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView content;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater = null;

        private MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.beacon_list, null);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.content = (TextView) convertView.findViewById(R.id.content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (data.get(position).isInside()) {
                holder.img.setImageResource(R.drawable.in);
                holder.title.setText(String.valueOf(data.get(position).getMinor()));
            } else {
                holder.img.setImageResource(R.drawable.out);
                holder.title.setText(String.valueOf(data.get(position).getMinor()));
            }

            holder.content.setText(String.valueOf(data.get(position).getBluetoothAddress() + "   " + data.get(position).getAccuracy()));

            return convertView;
        }
    }

    private class RangingBeacon extends IBeacon{
        private Integer integer = Integer.valueOf(9);
        private Integer getInteger() {
            return integer;
        }

        private RangingBeacon(IBeacon iBeacon, boolean isInside) {
            super (iBeacon);
            this.isInside = isInside;
        }

        public boolean isInside() {
            return isInside;
        }

        public void setInside(boolean isInside) {
            this.isInside = isInside;
        }

        private boolean isInside;
    }
}
