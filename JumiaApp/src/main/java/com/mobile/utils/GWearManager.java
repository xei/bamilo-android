package com.mobile.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;

import de.akquinet.android.androlog.Log;

/**
 * Class that parses the notification received by the handheld app, and sends it to the wear app through the
 * wearable service.
 *
 * Created by pcarvalho on 3/3/15.
 */
public class GWearManager {

    private static final String TAG = GWearManager.class.getName();
    public static GWearManager INSTANCE;
    public static final String EXTRA_INDEX = "extra_index";
    public static final String EXTRA_A4STITLE = "a4stitle";
    public static final String EXTRA_A4SCONTENT = "a4scontent";
    public static final String EXTRA_A4SBIGCONTENT = "a4sbigcontent";

    /**
     * Constructor
     * @param context
     */
    public GWearManager(Context context) {
        JumiaWearableListenerService.connectToWearable(context);
    }

    /**
     * Method responsible for parsing and extracting the information received on the notification.
     * @param intent
     */
    public void parseLocalNotification(Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.e(TAG, "BUNDLE:" + bundle.toString());
        String title = bundle.getString(EXTRA_A4STITLE);
        Log.e(TAG, "title:" + title);
        String shortMessage = bundle.getString(EXTRA_A4SCONTENT);
        Log.e(TAG, "shortMessage:" + shortMessage);
        String bigContent = bundle.getString(EXTRA_A4SBIGCONTENT);
        Log.e(TAG, "bigcontent:" + bigContent);
        String content = bundle.getString("a4scontent");
        Log.e(TAG, "content:" + content);
        int index = (int) (System.currentTimeMillis() / 10000);
        //informWearOfNotification(index, title, bigContent, bundle);
        informWearOfNotification(index, bundle);
    }

    /**
     * method that sends the information extracted from the app notification, to the wear app.
     *
     * @param index
     * @param bundle
     */
    public void informWearOfNotification (int index, Bundle bundle) {
        // Log.i(TAG, "code1wear index : " + index + " name: " + title + " content : " + content);
        PutDataMapRequest putRequest = PutDataMapRequest.create("/notification_received");
        DataMap map = putRequest.getDataMap();
        map.putInt(EXTRA_INDEX, index);
        for (String key : bundle.keySet()) {
            map.putString(key, bundle.get(key).toString());
        }
        new JumiaWearableListenerService.SendFilesTask().execute(putRequest, putRequest);
    }
}