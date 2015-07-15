package com.mobile.wear.receiver;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.mobile.wear.R;
import com.mobile.wear.activities.ErrorActivity;
import com.mobile.wear.application.CustomApplication;
import com.mobile.wear.service.IDataService;
import com.mobile.wear.service.IDataServiceCallback;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Class responsible for listening to the wearable service on the wear app side, and also for handling all the information sent
 * and received through it.
 * <p/>
 * Created by pcarvalho on 3/2/15.
 */
public class JumiaDataReceiverService extends WearableListenerService {

    private static final String TAG = JumiaDataReceiverService.class.getSimpleName();

    public static IDataServiceCallback mCallback;

    private static JumiaDataReceiverService mJumiaDataReceiverService;
    private static GoogleApiClient mGoogleApiClient;
    public static final String EXTRA_A4STITLE = "a4stitle";
    public static final String EXTRA_A4SCONTENT = "a4scontent";
    public static final String EXTRA_A4SBIGCONTENT = "a4sbigcontent";
    public static final String EXTRA_INDEX = "extra_index";
    public static final String EXTRA_DATAMAP = "extra_datamap";
    public static final String KEY_BUNDLE_DATA = "key_bundle_data";
    public static final String EXTRA_CONTENT = "extra_content";
    public static final String EXTRA_SEARCH_TERM = "extra_search_term";
    public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";


    public static JumiaDataReceiverService getInstance() {
        if (mJumiaDataReceiverService == null) {
            mJumiaDataReceiverService = new JumiaDataReceiverService();
        }
        return mJumiaDataReceiverService;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("WEAR", "code1wear onCreate");
        connectToWearable();
        mJumiaDataReceiverService = this;
    }

    /**
     * Method responsible for opening a googleApiClient connection.
     */
    public static void connectToWearable() {
        Log.e("WEAR", "code1wear connectToWearable");
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(CustomApplication.INSTANCE.getApplicationContext())
                    .addApi(Wearable.API)
                    .build();
            CustomApplication.INSTANCE.setGoogleApiClient(mGoogleApiClient);
        }

        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }

    }

    /**
     * callBack that is called every time there is some change in the service flow.
     *
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        Log.e("WEAR", "onDataChanged");
        Log.e("WEAR", "dataEvents:" + dataEvents.toString());
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {

            Uri uri = event.getDataItem().getUri();
            Log.e("WEAR", "uri:" + uri.getPath());
            Log.e("WEAR", "event:" + event.toString());

            if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.e("WEAR", "DataEvent.TYPE_CHANGED");

                final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Log.e("WEAR", "dataMap:" + dataMap.toString());

                if ("/notification_received".equals(uri.getPath())) {
                    Log.i("WEAR", "code1wear received notification_received ");
                    int index = dataMap.getInt(EXTRA_INDEX);
                    String title = dataMap.getString(EXTRA_A4STITLE);
                    String bigContent = dataMap.getString(EXTRA_A4SBIGCONTENT);
                    String content = dataMap.getString(EXTRA_A4SCONTENT);
                    Log.e("WEAR", "bigContent:" + bigContent);
                    Log.e("WEAR", "content:" + content);
                    Log.e("WEAR", "index:" + index);
                    Log.e("WEAR", "title:" + title);

                    createNotification(index, title, content, dataMap);

                } else if ("/errormessage".equals(uri.getPath())) {
                    Log.e("WEAR", "errormessage:");
                    Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String errorMessage = dataMap.getString(EXTRA_ERROR_MESSAGE);
                    Log.i(TAG, "code1wearerrormessage : " + errorMessage);
                    intent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage);
                    //
                    startActivity(intent);
//                    try {
//                        mCallback.openActivity(intent);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
                }

            } else {
                Log.e("WEAR", " NOT DataEvent.TYPE_CHANGED");
            }

        }

    }


    /**
     * function that parses a DataMap object into a bundle Object.
     *
     * @param datamap
     * @return bundle
     */
    private Bundle buildBundleForNotification(DataMap datamap) {
        Bundle bundle = new Bundle();
        for (String key : datamap.keySet()) {
            bundle.putString(key, datamap.get(key).toString());
        }
        return bundle;
    }

    /**
     * function responsible for creating a customized notification on the wear app.
     *
     * @param key
     * @param title
     * @param content
     * @param dataMap
     */
    public void createNotification(int key, String title, String content, DataMap dataMap) {
        CustomApplication.INSTANCE.getGoogleApiClient();
        CustomApplication.INSTANCE.getNodeId();

        Log.i(TAG, "code1wear postNotification : " + title + " : " + content);
        NotificationManagerCompat.from(this).cancelAll();
        int notificationId = 005;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, getClass()).setAction("clicked_notification");
        viewIntent.putExtra(EXTRA_INDEX, key);
        viewIntent.putExtra(EXTRA_DATAMAP, buildBundleForNotification(dataMap));

        PendingIntent viewPendingIntent = PendingIntent.getService(getApplicationContext(), 105, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.small_icon)
        .setContentTitle(title)
        .setContentText(content)
        .setAutoCancel(true)
        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.background))
        .addAction(new NotificationCompat.Action.Builder(R.drawable.ic_full_openonphone, getApplicationContext().getString(R.string.common_open_on_phone), viewPendingIntent)
                .extend(new NotificationCompat.Action.WearableExtender().setAvailableOffline(false))
                .build());
        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());

    }


    /**
     * performs the successfully sent animation
     */
    protected void showAnimation() {
        Intent openOnPhoneIntent = new Intent(this, ConfirmationActivity.class);
        openOnPhoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        openOnPhoneIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        startActivity(openOnPhoneIntent);
    }


    /**
     * Function responsible for detecting the click on the notification
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.i(TAG, "code1wear onStartCommand : " + intent.getAction());
            if ("clicked_notification".equals(intent.getAction())) {
                Log.i(TAG, "code1wear onStartCommand clicked_notification");
                openNotificationOnPhone(intent.getExtras().getBundle(EXTRA_DATAMAP));
                // clear notification from wear and app
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.cancelAll();
                //shows successfully sent animation
                showAnimation();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void openNotificationOnPhone(final Bundle bundle) {
        Log.i(TAG, "code1wear sendStarLoadingCatalog " + CustomApplication.INSTANCE.getNodeId());
        if (CustomApplication.INSTANCE.getNodeId() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "code1wear sendStarLoadingCatalog ");
                    PutDataMapRequest putRequest = PutDataMapRequest.create("/wearnotification");
                    Log.i(TAG, "code1wear building : " + putRequest.getUri().toString());
                    DataMap map = putRequest.getDataMap();
                    //map.putString(EXTRA_CONTENT, searchTerm);
                    map.putLong(EXTRA_INDEX, System.currentTimeMillis());
                    for (String key : bundle.keySet()) {
                        if (bundle.get(key) != null) {
                            Log.i(TAG, "code1wearbundle : key: " + key + " " + bundle.get(key).toString());
                            map.putString(key, bundle.get(key).toString());
                        }
                    }
                    DataApi.DataItemResult result = Wearable.DataApi.putDataItem(CustomApplication.INSTANCE.getGoogleApiClient(), putRequest.asPutDataRequest()).await();
                }
            }).start();
        }
    }


    /**
     * Function responsible for sending a DataApi item to the handheld app with a search term in order to perform a search
     * on the handheld app.
     *
     * @param searchTerm , words recognized by the voice input on wear app
     */
    public static void performSearch(final String searchTerm) {

        if (CustomApplication.INSTANCE.getNodeId() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "code1wear sendStarLoadingCatalog ");
                    PutDataMapRequest putRequest = PutDataMapRequest.create("/wearperformsearch");
                    Log.i(TAG, "code1wear building : " + putRequest.getUri().toString());
                    DataMap map = putRequest.getDataMap();
                    map.putString(EXTRA_SEARCH_TERM, searchTerm);
                    map.putLong(EXTRA_INDEX, System.currentTimeMillis());
                    Wearable.DataApi.putDataItem(CustomApplication.INSTANCE.getGoogleApiClient(), putRequest.asPutDataRequest()).await();
                }
            }).start();

        } else {
            Message msg = new Message();
            msg.getData().putString("searchQuery", searchTerm);
            Log.i(TAG, "code1retry search :" + searchTerm);
            handlerRetrySearch.sendMessageDelayed(msg, 250);
        }

    }

    /**
     * Handler to perform a retry to send the information if for any reason the nod isn't available
     */
    static Handler handlerRetrySearch = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String searchQuery = msg.getData().getString("searchQuery");
            Log.i(TAG, "code1retry search handler :" + searchQuery);
            performSearch(searchQuery);

        }
    };


    /**
     * Sets the Data Service callback
     *
     * @param callback
     */
    public void setCallback(IDataServiceCallback callback) {
        try {
            mBinder.registerCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * defines Data Service binder
     */
    private static IDataService.Stub mBinder = new IDataService.Stub() {

        @Override
        public void registerCallback(IDataServiceCallback cb) throws RemoteException {
            mCallback = cb;
        }

        @Override
        public void unregisterCallback(IDataServiceCallback cb) throws RemoteException {
            mCallback = null;
        }
    };


    /////////////////////// TO BE USED on 2nd PHASE ///////////////////////

    public static void deleteAllInfoFromDataApi(Uri uri) {
        Wearable.DataApi.deleteDataItems(CustomApplication.INSTANCE.getGoogleApiClient(), uri).await();
    }


    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            Log.i(TAG, "code1wear loadBitmapFromAsset Asset must be non-null");
            return BitmapFactory.decodeResource(getResources(), R.drawable.ic_full_cancel);
            //throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                CustomApplication.INSTANCE.getGoogleApiClient(), asset).await().getInputStream();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }


        // decode the stream into a bitmap
        Bitmap bMap = null;
        try {
            bMap = BitmapFactory.decodeStream(assetInputStream);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        Log.i(TAG, "code1load imaged loaded with success!");
        return bMap;
    }
}
