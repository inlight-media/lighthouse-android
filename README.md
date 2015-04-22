Lighthouse Android SDK
===============

The Lighthouse Android SDK is designed to be simple to develop with, allowing you to easily integrate Lighthouse iBeacon software into your apps. For more info about Lighthouse visit the [Lighthouse Website](http://lighthouse.io)

## Install Guide

Installing the client should be a breeze. If it's not, please let us know at [team@lighthouse.io](mailto:team@lighthouse.io)

### Eclipse:
1.Download the LighthouseSDK.zip file.

2.Extract the above file.

3.Import LighthouseSDK project.

4.Import google-play-services_lib project from <Your_Android_SDK_Path>/extras/google/google_play_services/libproject/google-play-services_lib.

5.In a new/existing Android Application project, go to Project -> Properties -> Android -> Library -> Add, then select google-play-services_lib and LighthouseSDK.

6.Add below entries to AndroidManifest.xml:

            <service
                android:name="org.altbeacon.beacon.service.BeaconService"
                android:enabled="true"
                android:exported="false"
                android:isolatedProcess="false"
                android:label="beacon" />
            <service
                android:name="org.altbeacon.beacon.BeaconIntentProcessor"
                android:enabled="true"
                android:exported="false" />
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
            <provider android:name="com.inlight.lighthousesdk.provider.LighthouseProvider"
                tools:replace="android:authorities"
                android:authorities="<Your_Package_Name>" />
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

    <permission android:name="<Your_Package_Name>.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />
    <uses-permission android:name="<Your_Package_Name>.permission.C2D_MESSAGE" />

8.Copy and paste ContentProviderAuthority.java file to your project with the same package name so that the lighthouse SDK can find it. And change the value of CONTENT_AUTHORITY to your package name.

### Android Studio / Gradle:
1.Download the lighthouse-release.aar file.

3.Create a /libs directory inside your project and copy it there.

4.Edit your build.gradle file, and add a "flatDir" entry to your repositories like so:

    repositories {
        mavenCentral()
        flatDir {
            dirs 'libs'
        }
    }

    dependencies {
        compile 'com.inlight.lighthouse:lighthouse-release@aar'
        compile 'com.mcxiaoke.volley:library:1.0.15@aar'
        compile 'com.google.android.gms:play-services:+'
        compile 'org.altbeacon:android-beacon-library:2.1.4'
    }

2.Add below entries to AndroidManifest.xml:

    <provider android:name="com.inlight.lighthousesdk.provider.LighthouseProvider"
        tools:replace="android:authorities"
        android:authorities="<Your_Package_Name>" />

3.Add below permissions to AndroidManifest.xml:

    <permission android:name="<Your_Package_Name>.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />
    <uses-permission android:name="<Your_Package_Name>.permission.C2D_MESSAGE" />

4.Copy and paste ContentProviderAuthority.java file to your project with the same package name so that the lighthouse SDK can find it. And change the value of CONTENT_AUTHORITY to your package name.

### Proguard configuration
Please add the following into your proguard configuration file if proguard is enabled.

    -keep class com.inlight.lighthouse.ContentProviderAuthority{ *; }
    -keep class org.altbeacon.beacon.** { *; }

### Compile
Try and compile. It should work!

If it doesn't work, let us know at [team@lighthouse.io](mailto:team@lighthouse.io) and one of us will help you right away.

### Get Application ID & Keys
If you haven't done so already, login to [Lighthouse](http://www.lighthouse.io) to get your Application ID & Keys.


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
            startService(new Intent(this,LighthouseService.class));
            BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(this);
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
                // Enable production mode. Default is false.
                lighthouseManager.enableProduction();
                // Enable logging. Default is false;
                lighthouseManager.enableLogging();
                // Enable ranging. Action ACTION_BEACONS_IN_REGION won't be received. Default is true;
                lighthouseManager.disableRanging();
                // Enable offline mode. Enter/Exit events will be stored when network is not available and will be sent to server when network turns available. Default is false;
                lighthouseManager.enableOffline();
                // Disable transmission. Events won't be sent to server. Default is true;
                // lighthouseManager.disableTransmission
                // Disable monitoring. ACTION_ENTER_BEACON and ACTION_EXIT_BEACON won't be received and enter/exit events won't be sent to server. Default is true;
                // lighthouseManager.disableMonitoring();
                // set the duration of the scan to be 1.1 seconds
                lighthouseManager.setBackgroundScanPeriod(1100l);
                // set the time between each scan to be 1 minute (60 seconds)
                lighthouseManager.setBackgroundBetweenScanPeriod(60000l);
            }
        }
    }


From now on, in your code, you can just reference the shared client by calling lighthouseManager = ((ExampleApplication)this.getApplicationContext()).getLighthouseManager();.

It is recommended to getInstance in android.app.Application so that lighthouseManager instance can be used each time the app is launched even from notification.

### Running Background
Like the sample app, define a service to launch the lighthouse SDK helps to keep detecting beacon on the background. In another word, your app will continue sending enter/exit events to lighthouse sever and trigger campaign notification.

### Battery manager
To best conserve battery you'll need to add the following lines to your own subclass of android.app.Application.

    @Override
    public void onCreate(){
        super.onCreate();
        BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(this);
    }


BackgroundPowerSaver only works when you have set background scan period which will track whether your activity is active or not and set background mode automatically.


### Debugging
The Lighthouse Android SDK code does a lot of logging, but it's turned off by default. If you'd like to see the log lines generated by your usage of the client, you can enable logging easily:

    lighthouseManager.enableLogging();

Just put this at any point before you use LighthouseManager.

To disable logging (by default it is disabled), simply call:

    LighthouseManager.disableLogging();

### Events
The Lighthouse Android SDK doesn't keep all the beacon events for itself, after all sharing is caring. Using the SDK you can subscribe to events such as when a user enters, exits or ranges (usually every five seconds when within a beacon). 

You can then listen to these intents by using the following example commands:

    LocalBroadcastManager mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
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

    private class LighthouseReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentPrefix = context.getPackageName();
        if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_BEACONS_IN_REGION)) {
            //deal with the beacons that is in region
        } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_ENTER_BEACON)) {
            //deal with the case that a beacon enters the device's range
        } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_EXIT_BEACON)) {
            //deal with the case that a beacon exit the device's range
        } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_NOTIFICATION)) {
            //received push notification from GCM
        } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_CAMPAIGN)) {
            //campaign received
        } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_ACTION_CAMPAIGN)) {
            //action campaign
        } else if (intent.getAction().equals(intentPrefix+LighthouseManager.ACTION_UPDATE_SETTINGS)) {
            //update Settings
        }
    }

Event LighthouseManager.ACTION_CAMPAIGN is triggered whenever a request to get more detail about a campaign is made. More details in the next "Detailed Campaign Data" section.


### Detailed Campaign Data
we added the ability to retrieve detailed campaign data that is too large to fit in the 256 byte limit of a push notification. This is useful if you are using the "Meta" field in the Advanced Fields section of campaign creation. In future you will also use this method for getting images, videos, rules etc from the API. To get detailed campaign data you need to give Lighthouse context of the notification to get the corresponding campaign data for.
    lighthouseManager.campaign(notification);


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
    lighthouseManager.campaignActioned(LighthouseNotification.parse(notificationString));

By calling this method the SDK will subsequently send the broadcast of action name "LighthouseManager.ACTION_ACTION_CAMPAIGN" with the LighthouseNotification class as its data.

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

### Offline Mode

Since the Lighthouse SDK need network connection to send data to the server, sometimes data will be lost when network is not available. You can enable this offline mode to store the data and send it whenever there's network connections. By default it is disabled.

    lighthouseManager.disableOffline();

Just put this at any point before you use LighthouseManager. The recommended place to do this is in your own subclass of android.app.Application.
To enable offline mode to the Ligthhouse server, simply call:

    lighthouseManager.enableOffline();

### Ranging & Monitoring

By default the Lighthouse SDK does both ranging and monitoring ibeacons. Sometimes you will not be interested in ranging or monitoring. You can use the following methods to control whether doing ranging or monitoring.By default they are enabled.

    lighthouseManager.disableRanging();
    // or
    lighthouseManager.disableMonitoring();


The recommended place to do this is in your own subclass of android.app.Application before launch the lighthouse SDK. Otherwise, this would probably not work.

### Google Cloud Messaging
Google Cloud Messaging for Android (GCM) is a free service that helps developers send data from servers to their Android applications on Android devices.
It is helpful to have access to the registration ID to test your push notifications. For more details please visit http://developer.android.com/google/gcm/gcm.html

    lighthouseManager.requestPushNotifications(<Your_Sender_ID>);
    lighthouseManager.getRegistrationId();

## Settings

The Lighthouse SDK allows you to inspect specific settings for that are retrieved from the server whenever the application is opened (either launched or becomes active). These settngs contain the beacon uuids being monitored and also whether Lighthouse should be enabled or not. Importantly this "isEnabled" boolean allows you to remotely control from the server whether the Lighthouse SDK should be enabled on the device or not.

You can retrieve these settings using two methods, because the request is asynchronous you should use both ways to get the latest updates.

At anytime you can request the settings synchronously using

    lighthouseManager.getSettings();

NOTE: This method can return a null value if the settings have not yet been retrieved for the application. Usually this is on first install of the app because after getting the settings once it saves the settings for future app launches. However you are advised to specifically check this for null value before trying to perform any operations on the result otherwise you will create crashes.

To asynchronously receive notification when the settings are updated you can add the intent filter in the same way as other events.

    intentFilter.addAction(intentPrefix+LighthouseManager.ACTION_UPDATE_SETTINGS);


## Changelog
##### 1.4
+ Change domain name to lighthouse.io

+ Add dependency of Android Beacon Library

+ Support for Android L

##### 1.3
+ Use background service to track beacons.

+ Provide offline mode to cache data when network is not available.

+ Use broadcast to receive data from lighthouse SDK.

+ Add function of disabling ranging or monitoring.

+ Change the inside expiration time in milliseconds to 45 seconds

##### 1.2
+ Change default foreground scan period from 1.1 second to 2 seconds.

+ Change method name Campaigns() to campaign().

+ Change method name CampaignsActioned() to campaignActioned().

+ Send broadcast when received push notification and received campaign data.

##### 1.1
+ Added ability to read and subscribe to Lighthouse SDK server settings, in particular whether the SDK should be enabled or not. When the SDK is disabled non of the SDK commands will perform functionality. This means you can include the SDK in a release of your app with it disabled on the server and then in the future you can update the server to enabled and the SDK will begin to perform desired functionality. [See Settings information](https://github.com/inlight-media/lighthouse-android#settings).

+ Add parameter while request push notifications. Developer can use its own send id to implement GCM push notification. 

##### 1.0.0
+ Initial SDK Release

### Questions & Support

If you have any questions, bugs, or suggestions, please email them to [team@lighthouse.io](mailto:team@lighthouse.io). We'd love to hear your feedback and ideas!

