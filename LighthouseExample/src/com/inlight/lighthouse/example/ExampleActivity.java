package com.inlight.lighthouse.example;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.inlight.lighthousesdk.LighthouseSettings;
import com.inlight.lighthousesdk.ibeacon.IBeacon;
import com.inlight.lighthousesdk.ibeacon.Region;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ExampleActivity extends Activity {
    private LocalBroadcastManager mLocalBroadcastManager;
    private LighthouseReceiver lighthouseReceiver;
    private TextView mTextView;
    private ListView mListView;
    private MyAdapter mAdapter;
    private HashMap<Region, ArrayList<IBeacon>> data = new HashMap<Region, ArrayList<IBeacon>>();
    private static final String SENDER_ID = "877637997124";
    private LighthouseManager lighthouseManager;

    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        startService(new Intent(this,LighthouseService.class));
        mTextView = (TextView) ExampleActivity.this
                .findViewById(R.id.monitoringText);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mListView = (ListView) this.findViewById(R.id.beacon_list);
        mAdapter = new MyAdapter(ExampleActivity.this);
        mListView.setAdapter(mAdapter);

        lighthouseManager = ((ExampleApplication) this.getApplicationContext()).getLighthouseManager();
        lighthouseManager.reload();
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
//            case R.id.action_disable_push_notification:
//                lighthouseManager.disablePushNotifications();
//                return true;
            case R.id.action_enable_push_notification:
                LighthouseManager lighthouseManager = ((ExampleApplication)getApplication()).getLighthouseManager();
                lighthouseManager.requestPushNotifications(SENDER_ID);
                String regId = lighthouseManager.getRegistrationId();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, regId);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(lighthouseReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        lighthouseReceiver = new LighthouseReceiver();
        String intentPrefix = this.getPackageName();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_BEACONS_IN_REGION);
        intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_ENTER_BEACON);
        intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_EXIT_BEACON);
        intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_UPDATE_SETTINGS);
        intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_CAMPAIGN);
        intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_ACTION_CAMPAIGN);
        intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_NOTIFICATION);
        mLocalBroadcastManager.registerReceiver(lighthouseReceiver, intentFilter);

        if (lighthouseManager == null) {
            lighthouseManager = ((ExampleApplication) this.getApplicationContext()).getLighthouseManager();
        }
    }

    public void onClearClicked(View view) {
        mTextView.setText(null);
    }
    public void onStopClicked(View view) {
        if (lighthouseManager.isBound()) {
            ((Button) view).setText("Start Service");
//            lighthouseManager.reset();
            stopService(new Intent(this, LighthouseService.class));
        } else {
            ((Button) view).setText("Stop Service");
//            lighthouseManager.launch();
            startService(new Intent(this, LighthouseService.class));
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
        private List<IBeacon> beacons =
                new ArrayList<IBeacon>();
        private LayoutInflater mInflater = null;

        private MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public void setup() {
            beacons.clear();
            for (ArrayList<IBeacon> value : data.values()) {
                beacons.addAll(value);
            }
            Collections.sort(beacons, new Comparator<IBeacon>() {
                @Override
                public int compare(final IBeacon o1, final IBeacon o2) {
                    return (int)(o1.getMinor() - o2.getMinor());
                }
            });
        }
        @Override
        public int getCount() {
            return beacons.size();
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
            holder.img.setImageResource(R.drawable.in);
            holder.title.setText(String.valueOf(beacons.get(position).getMinor()));

            holder.content.setText(String.valueOf(beacons.get(position).getBluetoothAddress() + "   " + beacons.get(position).getAccuracy()));

            return convertView;
        }
    }

    private class LighthouseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentPrefix = context.getPackageName();
            if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_BEACONS_IN_REGION)) {
                HashSet<IBeacon> beacons = (HashSet<IBeacon>)intent.getSerializableExtra(LighthouseManager.EXTRA_BEACONS);
                Region region = (Region)intent.getParcelableExtra(LighthouseManager.EXTRA_REGION);
                if (beacons == null || beacons.size() == 0){
                    return;
                }

                ArrayList<IBeacon> beaconList= new ArrayList<IBeacon>(beacons);
                data.put(region,beaconList);
                mAdapter.setup();
                mAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_ENTER_BEACON)) {
                IBeacon beaconData = intent.getParcelableExtra(LighthouseManager.EXTRA_BEACON);
                logToDisplay(getCurrentTime() + " Did enter iBeacon named " + beaconData.getProximityUuid() + " " + beaconData.getMajor() + " " + beaconData.getMinor());
            } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_EXIT_BEACON)) {
                IBeacon beaconData = intent.getParcelableExtra(LighthouseManager.EXTRA_BEACON);
                logToDisplay(getCurrentTime() + " Did exit iBeacon named " + beaconData.getProximityUuid() + " " + beaconData.getMajor() + " " + beaconData.getMinor());
            } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_NOTIFICATION)) {
                LighthouseNotification notification = intent.getParcelableExtra(LighthouseManager.EXTRA_NOTIFICATION);
                logToDisplay(getCurrentTime() + " Did received a notification: "
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
            } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_CAMPAIGN)) {
                CampaignData campaignData = intent.getParcelableExtra(LighthouseManager.EXTRA_CAMPAIGN);
                logToDisplay(getCurrentTime() + " Did receive Campaign: notification = "
                    + campaignData.getLighthouseNotification().toString() + "campaign = " + campaignData.getCampaign().toString());
            } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_ACTION_CAMPAIGN)) {
                LighthouseNotification notification = intent.getParcelableExtra(LighthouseManager.EXTRA_ACTION_CAMPAIGN);
                logToDisplay(getCurrentTime() + " Did Action Campaign: "
                    + notification.toJSONObject().toString());
            } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_UPDATE_SETTINGS)) {
                LighthouseSettings settings = (LighthouseSettings)intent.getSerializableExtra(LighthouseManager.EXTRA_SETTINGS);
                logToDisplay(getCurrentTime() + " Lighthouse Settings: "
                        + settings.isEnabled() + settings.getUuidsSet());
            }
        }
    }
}
