package com.mobile.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.mobile.constants.BundleConstants;
import com.mobile.preferences.ShopPreferences;
import com.mobile.view.SplashScreenActivity;

import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * Class responsible for listening to the wearable service on the hadnheld app side, and also for handling all the information sent
 * and received through it.
 * <p/>
 * Created by pcarvalho on 3/3/15.
 */
public class JumiaWearableListenerService extends WearableListenerService {

    private static final String TAG = JumiaWearableListenerService.class.getSimpleName();

    public static GoogleApiClient sGoogleApiClient;

    public static final String EXTRA_SEARCH_TERM = "extra_search_term";

    public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";

    @Override
    public void onCreate() {
        super.onCreate();
        connectToWearable(getApplicationContext());
    }

    /**
     * callBack that is called every time there is some change in the service flow.
     *
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "APP code1wear onDataChanged ");

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            Log.d(TAG, "APP code1wear onDataChanged uri is : " + uri);
            Log.d(TAG, "APP code1wear onDataChanged : " + uri.getPath());
            final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
            Log.d(TAG, "APP code1wear dataMap : " + dataMap.toString());
            if ("/wearnotification".equals(uri.getPath())) {
                NotificationManagerCompat.from(this).cancelAll();
                Bundle bundle = buildBundleForNotification(dataMap);
                Log.d(TAG, "APP code1bundle : " + bundle);
                Log.d(TAG, "APP code1error : Ups! error while loading information!");

                if (bundle != null) {
                    Intent newIntent = new Intent(this, SplashScreenActivity.class);
                    newIntent.putExtra(BundleConstants.EXTRA_GCM_PAYLOAD, bundle);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(newIntent);

//                    String deepLinkUrl = dataMap.getString("u");
//                    Log.d(TAG, "APP BUNDLE ->  deepLinkUrl " + deepLinkUrl);
//                    Uri data = Uri.parse(deepLinkUrl);
//                    Log.d(TAG, "APP BUNDLE ->  data " + data);
//                    Bundle gcmBundle = new Bundle();
//                    gcmBundle.putString(BundleConstants.DEEPLINKING_PAGE_INDICATION,deepLinkUrl);
//                    Bundle deepLinkBundle =  DeepLinkManager.loadDeepLink(getApplicationContext(), data);
//                    Log.d(TAG, "APP BUNDLE ->  deepLinkBundle " + deepLinkBundle);
                }


            } else if ("/wearperformsearch".equals(uri.getPath())) {
                Log.e(TAG, "PERFORMED VOICE SEARCH");
                String searchTerm = dataMap.getString(EXTRA_SEARCH_TERM);
                Log.i(TAG, "code1search : searchterm : " + searchTerm);
                String countryISO = ShopPreferences.getShopCountryISO(getApplicationContext());
                if (countryISO == null) {
//                    sendErrorToWear("Ups! error while loading information!");
                    sendErrorToWear("Ups! error, choose country on App!");
                    return;
                }
                String deepLinkUrl = countryISO + "/s/" + searchTerm;
                Log.e(TAG, "deepLinkUrl:" + deepLinkUrl);
                Uri data = Uri.parse(deepLinkUrl);
                Bundle gcmBundle = new Bundle();
                gcmBundle.putString(BundleConstants.DEEPLINKING_PAGE_INDICATION, deepLinkUrl);
                Log.e(TAG, "gcmBundle:" + gcmBundle);
                Intent newIntent = new Intent(this, SplashScreenActivity.class);
                newIntent.putExtra(BundleConstants.EXTRA_GCM_PAYLOAD, gcmBundle);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(newIntent);
            }
        }

    }

    /**
     * method that sends a DataMapRequest with the error message to the wear app.
     *
     * @param errorMessage
     */
    public void sendErrorToWear(String errorMessage) {
        PutDataMapRequest putRequest = PutDataMapRequest.create("/errormessage");
        DataMap map = putRequest.getDataMap();
        map.putLong("md5", System.currentTimeMillis());
        if (errorMessage != null) {
            map.putString(EXTRA_ERROR_MESSAGE, errorMessage);
        }
        new SendFilesTask().execute(putRequest, putRequest);

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
     * Method responsible for opening a googleApiClient connection.
     */
    public synchronized static void connectToWearable(Context context) {
        if (sGoogleApiClient == null) {
            sGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        }

        if (!sGoogleApiClient.isConnected() && !sGoogleApiClient.isConnecting()) {
            sGoogleApiClient.connect();
        }
    }

    /**
     * Method responsible for sending a generic DataMapRequest from the handheld app to the wear app.
     */
    public static class SendFilesTask extends AsyncTask<PutDataMapRequest, Integer, Integer> {
        protected Integer doInBackground(PutDataMapRequest... requests) {
            Log.e("WEAR", "doInBackground");
            long token = Binder.clearCallingIdentity();
            try {
                Log.e("WEAR", "doInBackground 1");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(sGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    Log.e("WEAR", "doInBackground 2");

                    DataApi.DataItemResult result = Wearable.DataApi.putDataItem(sGoogleApiClient, requests[0].asPutDataRequest()).await();
                    if (result.getStatus().isSuccess()) {
                        Log.e("WEAR", "doInBackground 3");
                        Log.i(TAG, "code1DataMap: success! " + requests[0].getDataMap() + " sent to: " + node.getDisplayName());
                    } else {
                        Log.e("WEAR", "doInBackground 4");
                        // Log an error
                        Log.i(TAG, "code1DataMap ERROR: failed to send DataMap");
                    }
                }


            } finally {
                Binder.restoreCallingIdentity(token);
            }

            return requests.length;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.e("WEAR", "onProgressUpdate");
        }

    }


}

