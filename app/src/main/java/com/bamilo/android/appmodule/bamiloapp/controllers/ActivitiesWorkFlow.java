package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.view.OverLoadErrorActivity;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.RedirectInfoActivity;
import com.bamilo.android.appmodule.bamiloapp.view.SplashScreenActivity;

/**
 * This Class is responsible by all the application workflow. Contains all
 * static methods used to start all the activities in the application.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Sergio Pereira
 * 
 * @version 1.01
 * 
 *          2012/06/19
 * 
 */
public class ActivitiesWorkFlow {

    /**
     * Start activity with slide transition
     */
    private static void startWithSlideTransition(@NonNull Activity activity, @NonNull Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Start activity with fade transition
     */
    private static void startWithFadeTransition(@NonNull Activity activity, @NonNull Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Start splash activity
     */
	public static void splashActivityNewTask(@NonNull Activity activity ) {
	    Intent intent = new Intent(activity.getApplicationContext(), SplashScreenActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startWithSlideTransition(activity, intent);
	}
	
	/**
	 * Used to share.
	 */
	public static void startActivitySendString(@NonNull Activity activity, String chooserText, String extraText) {
	    Intent sharingIntent = new Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT, extraText);
        activity.startActivity(Intent.createChooser(sharingIntent, chooserText));
	}

    /**
     * Start the market from package id.
     */
    public static void startMarketActivity(@NonNull Activity activity, @NonNull String id) {
        startMarketActivity(activity, id, activity.getString(R.string.market_store_external_uri, id));
    }

    /**
     * Start the market from package id or from link.
     */
    public static void startMarketActivity(@NonNull Activity activity, @NonNull String id, @NonNull String link) {
        try {
            startMarketActivity(activity, Uri.parse(activity.getString(R.string.market_store_uri, id)));
        } catch (ActivityNotFoundException ex) {
            startExternalWebActivity(activity, Uri.parse(link));
        }
    }

    /**
     * Start market activity.
     */
    private static void startMarketActivity(@NonNull Activity activity, @NonNull Uri uri) throws ActivityNotFoundException {
        startWithSlideTransition(activity, new Intent(Intent.ACTION_VIEW).setData(uri));
    }

    /**
     * Open an External Link
     */
    public static void startExternalWebActivity(@NonNull Activity activity, @NonNull String link, @NonNull String label) throws ActivityNotFoundException {
//        TrackerDelegator.trackClickOnExternalLink(label);
        startExternalWebActivity(activity, Uri.parse(link));
    }

    /**
     * Start external web activity
     */
    private static void startExternalWebActivity(@NonNull Activity activity, @NonNull Uri uri) {
        startWithSlideTransition(activity, new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * Shows server overload page
     */
    public static void showOverLoadErrorActivity(@NonNull Activity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), OverLoadErrorActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startWithFadeTransition(activity, intent);
    }

    /**
     * Shows redirect page
     */
    public static boolean showRedirectInfoActivity(@NonNull Activity activity, @NonNull Parcelable redirect) {
        Intent intent = new Intent(activity.getApplicationContext(), RedirectInfoActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY)
        .putExtra(ConstantsIntentExtra.DATA, redirect);
        startWithFadeTransition(activity, intent);
        activity.finish();
        return true;
    }

}