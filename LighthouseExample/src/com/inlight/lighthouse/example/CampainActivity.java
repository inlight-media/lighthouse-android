package com.inlight.lighthouse.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.inlight.lighthousesdk.LighthouseManager;
import com.inlight.lighthousesdk.LighthouseNotification;


public class CampainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campain);
        boolean isAction =  this.getIntent().getBooleanExtra("campaignActioned",false);
        TextView notificationTextView = (TextView)this.findViewById(R.id.notification);
        if (isAction){
            LighthouseManager lighthouseManager = ((ExampleApplication)this.getApplicationContext()).getLighthouseManager();
            String notificationString = this.getIntent().getStringExtra("campaignNotification");

            lighthouseManager.campaignActioned(LighthouseNotification.parse(notificationString));

            notificationTextView.setText("Campaign action : "+notificationString);
        } else {
            notificationTextView.setText("start normally.");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.campain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
