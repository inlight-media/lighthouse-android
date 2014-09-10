Lighthouse Android SDK
===============

The Lighthouse Android SDK is designed to be simple to develop with, allowing you to easily integrate Lighthouse iBeacon software into your apps. For more info about Lighthouse visit the [Lighthouse Website](http://lighthousebeacon.io)

## Install Guide

Installing the client should be a breeze. If it's not, please let us know at [team@lighthousebeacon.io](mailto:team@lighthousebeacon.io)

### Eclipse:
1.Download the EclipseLibs.zip file.

2.Extract the above file.

3.Create a /libs directory inside your project and copy all the JAR files there.

4.Import google-play-services_lib project from <Your_Android_SDK_Path>/extras/google/google_play_services/libproject/google-play-services_lib.

5.In a new/existing Android Application project, go to Project -> Properties -> Android -> Library -> Add, then select google-play-services_lib.

6.Add below entries to AndroidManifest.xml:

            <service android:enabled="true"
                android:exported="true"
                android:isolatedProcess="false"
                android:label="iBeacon"
                android:name="com.inlight.lighthousesdk.ibeacon.service.IBeaconService">
            </service>
    
            <service android:enabled="true"
                android:name="com.inlight.lighthousesdk.ibeacon.IBeaconIntentProcessor">
            </service>
            <receiver
                android:name="com.inlight.lighthousesdk.GcmBroadcastReceiver"
                android:permission="com.google.android.c2dm.permission.SEND" >
                <intent-filter>
                    <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                    <action android:name="com.google.android.c2dm.intent.REGISTRATION" />
                    <category android:name="com.inlight.lighthousesdk" />
                </intent-filter>
            </receiver>
            <service android:name="com.inlight.lighthousesdk.GcmIntentService" />
            <meta-data android:name="com.google.android.gms.version"
               android:value="@integer/google_play_services_version" />

7.Add below permissions to AndroidManifest.xml:

    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />

    <permission android:name="com.inlight.lighthousesdk.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />
    <uses-permission android:name="com.inlight.lighthousesdk.permission.C2D_MESSAGE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

### Android Studio / Gradle:
1.Download the AndroidStudioLibs.zip file.

2.Extract the above file.

3.Create a /libs directory inside your project and copy all the AAR files there.

4.Edit your build.gradle file, and add a "flatDir" entry to your repositories like so:

    repositories {
        mavenCentral()
        flatDir {
            dirs 'libs'
        }
    }

5.Edit your build.gradle file to add this AAR as a dependency like below:

    dependencies {
        compile 'com.inlight.lighthouse:lighthouse@aar'
        compile 'com.android.volley:volley@aar'
        compile 'com.google.android.gms:play-services:+'
        compile 'com.android.support:support-v4:+'
    }

### Compile
Try and compile. It should work!

If it doesn't work, let us know at [team@lighthousebeacon.io](mailto:team@lighthousebeacon.io) and one of us will help you right away.

### Get Application ID & Keys
If you haven't done so already, login to Ligthouse to get your Application ID & Keys.


## Instrumentation
Now it's time to actually use the client!

### Register Client
Register the LighthouseManager with your Application ID and access keys. The recommended place to do this is in your own subclass of android.app.Application. Here's some example code:

    public class ExampleApplication extends Application {
        private LighthouseManager lighthouseManager;
        // setup configure
        private static String APP_ID = "53842f4c740659ca7a295b2e";
        private static String APP_KEY = "edf8e5639f241fd8ea3e6078c3beb42f5cdd16f2";
        private static String APP_TOKEN = "b975b2548bdee49ce3e8acf8108a8353bd2d17c3";

        @Override
        public void onCreate(){
            configLighthouse();
        }

        public LighthouseManager getLighthouseManager() {
            configLighthouse();
            return lighthouseManager;
        }

        private void configLighthouse() {
            if (lighthouseManager == null) {
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


From now on, in your code, you can just reference the shared client by calling lighthouseManager = ((ExampleApplication)this.getApplicationContext()).getLighthouseManager();.

It is recommended to getInstance in android.app.Application so that lighthouseManager instance can be used each time the app is launched even from notification.

### Application Suspend & Resume
To best conserve battery and correctly trigger Lighthouse events you'll need to add the following lines to your application delegate.

    @Override 
    protected void onDestroy() {
        super.onDestroy();
        lighthouseManager.terminate();
    }
    @Override 
    protected void onPause() {
    	super.onPause();
    	if (lighthouseManager.isBound()) lighthouseManager.setBackgroundMode(true);
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
Also you can pause and reload detecting like below:
    
    //pause detecting beacons
    lighthouseManager.pause();
    //restart detecting beacons
    lighthouseManager.reload();

### Scan Period
Sets the duration in milliseconds of each Bluetooth LE scan cycle to look for iBeacons. This function is used to setup the period before launch or when switching between background/foreground. To have it effect on an already running scan (when the next cycle starts, call updateScanPeriods.

    /**
	 * Duration in milliseconds of the bluetooth scan cycle
	 */
	 lighthouseManager.setForegroundScanPeriod(1100);
	/**
	 * Duration in milliseconds spent not scanning between each
	 * bluetooth scan cycle
	 */
	 lighthouseManager.setForegroundScanPeriod(0);
	/**
	 * Duration in milliseconds of the bluetooth scan cycle when no
	 * clients are in the foreground
	 */
	 lighthouseManager.setBackgroundScanPeriod(1000);
	/**
	 * The default duration in milliseconds spent not scanning between each
	 * bluetooth scan cycle when no ranging/monitoring clients are in the
	 * foreground
	 */
	 lighthouseManager.setBackgroundBetweenScanPeriod(5 * 60 * 1000);

     //To have it effect on an already running scan
     lighthouseManager.updateScanPeriods();


### Debugging
The Lighthouse Android SDK code does a lot of logging, but it's turned off by default. If you'd like to see the log lines generated by your usage of the client, you can enable logging easily:

    lighthouseManager.enableLogging();

Just put this at any point before you use LighthouseManager.

To disable logging (by default it is disabled), simply call:

    LighthouseManager.disableLogging();

### Events
The Lighthouse Android SDK doesn't keep all the beacon events for itself, after all sharing is caring. Using the SDK you can subscribe to events such as when a user enters, exits or ranges (usually every five seconds when within a beacon). 

You can then listen to these events by implementing the LighthouseNotifier interface:

    private LighthouseNotifier lighthouseNotifier = new LighthouseNotifier() {

		@Override
		public void LighthouseDidEnterBeacon(IBeacon beaconData) {
			//deal with the case that a beacon enters the device's range
		}

		@Override
		public void LighthouseDidExitBeacon(IBeacon beaconData) {
			//deal with the case that a beacon exit the device's range		
		}

		@Override
		public void LighthouseDidRangeBeacon(final Collection<IBeacon> beacons, Region region) {
			//deal with the beacons that is in region	
			
		}

		@Override
		public void LighthouseDidReceiveCampaign(CampaignData campaignData) {
			//campaign received
		}

		@Override
		public void LighthouseDidActionCampaign(LighthouseNotification notification) {
			//action campaign
		}

		@Override
		public void LighthouseDidReceiveNotification(
				LighthouseNotification notification) {
			//received push notification from GCM
		}

    };

Event "LighthouseDidReceiveCampaign" is triggered whenever a request to get more detail about a campaign is made. More details in the next "Detailed Campaign Data" section.

### Detailed Campaign Data
we added the ability to retrieve detailed campaign data that is too large to fit in the 256 byte limit of a push notification. This is useful if you are using the "Meta" field in the Advanced Fields section of campaign creation. In future you will also use this method for getting images, videos, rules etc from the API. To get detailed campaign data you need to give Lighthouse context of the notification to get the corresponding campaign data for.
    lighthouseManager.Campaigns(notification);

This will make an API call to the Lighthouse server. On completion it will call the "LighthouseDidReceiveCampaign" method.

        @Override
        public void LighthouseDidReceiveCampaign(HashMap<String, Object> output) {
            logToDisplay(getCurrentTime()+" Did receive Campaign: "
                    + output.toString());
        }

An example of the data returned is below. You'll see it includes the campaign data as well as the original notification so you can determine which notification was used as context in retrieving the campaign.

    {
        campaign: {
            _id: '537c314c43083e7358000022',
            meta: {
                foo: 'bar'
            }
        },
        notification: LighthouseNotification.class //LighthouseNotification Object
        }
    }

NOTE: If valid JSON data is added in the 'meta' field of admin campaign console then we will automatically parse it for transmission down to the Android app so you get an JSONObject of values. However if its not valid and our JSON parser can't parse it then a string will be sent. You should check the meta type as to whether it is an String or JSONObject before assuming it's a JSONObject otherwise your application could run into a bug/crash.

### Campaign Actions

Often after you've displayed a campaign to a user you'd like to record that they performed an action on that campaign (ie, clicked a redeem button). The LighthouseManager class has a method for this instance. By transmitting that an action was performed on a campaign you can then view the results on the Lighthouse Analtytics dashboard. Here is an example:

    LighthouseManager lighthouseManager = ((ExampleApplication)this.getApplicationContext()).getLighthouseManager();
    String notificationString = this.getIntent().getStringExtra("campaignNotification");
    lighthouseManager.CampaignsActioned(LighthouseNotification.parse(notificationString));

By calling this method the SDK will subsequently callback "LighthouseDidActionCampaign" method with the LighthouseNotification class as its data.

### Custom Properties

The Lighthouse SDK gives you the ability to assign custom properties about the particular user's device. For instance you can record the gender, age group, user preferences, and much more which will then be stored against that device and synced to the Lighthouse API. We've added this feature so that future advancements of the Lighthouse API will allow you to segment analytics based on these custom properties and also create campaigns that only target users with specific properties. These are not yet available, however its a good idea to start capturing this data from day one so you can access the full benefits when these features are launched.

To get started you have the following methods available to you

You can view all the properties you have assigned:

    JSONObject properties = lighthouseManager.properties();

You can set properties all at once using a batch method:

        JSONObject properties = new JSONObject();
        try {
            properties.put("age",30);
            properties.put("gender","female");
            lighthouseManager.setProperties(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }

You can set individual properties as well. For instance this example will overwrite the gender property that was already set in the previous method:

    lighthouseManager.addProperty("gender","male");

You can also remove properties:

    lighthouseManager.removeProperty("gender");

### Transmission

There are times when you will want to leverage the Lighthouse SDK to easily access iBeacon events but you may not want it to transmit data to Lighthouse API. This could be the case during development or possibly if a user does not want their movements tracked. You can use the following methods to control whether the SDK transmits data to the server. By default it is enabled.

    lighthouseManager.enableTransmission();

Just put this at any point before you use LighthouseManager. The recommended place to do this is in your own subclass of android.app.Application.
To disable transmission to the Ligthhouse server, simply call:

    lighthouseManager.disableTransmission();

### Production Mode

By default the Lighthouse SDK operates in Development mode. Every analytic sent to Lighthouse API will record which mode the device was in when it was sent. This means that we can send push notifications using the correct certificates for the device. Also in future we'll investigate how you can filter and test your analytics more by segregating development data from your production data.

    lighthouseManager.enableProduction();

Just put this at any point before you use LighthouseManager. The recommended place to do this is in your own subclass of android.app.Application.

To put it back into development mode, you can disable production at any time (by default production is disabled):

    lighthouseManager.disableProduction();

### Google Cloud Messaging
Google Cloud Messaging for Android (GCM) is a free service that helps developers send data from servers to their Android applications on Android devices.
It is helpful to have access to the registration ID to test your push notifications. For more details please visit http://developer.android.com/google/gcm/gcm.html

    lighthouseManager.getRegistrationId();

##### 1.0.0

+ Initial SDK Release

### Questions & Support

If you have any questions, bugs, or suggestions, please email them to [team@lighthousebeacon.io](mailto:team@lighthousebeacon.io). We'd love to hear your feedback and ideas!

