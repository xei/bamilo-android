package com.mobile.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.configs.RedirectInfo;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.OverLoadErrorActivity;
import com.mobile.view.R;
import com.mobile.view.RedirectInfoActivity;
import com.mobile.view.SplashScreenActivity;

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
	protected final static String TAG = ActivitiesWorkFlow.class.getSimpleName();

	public static void splashActivityNewTask(Activity activity ) {
	    Print.i(TAG, "START ACTIVITY: splashActivity");
	    Intent intent = new Intent(activity.getApplicationContext(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        addStandardTransition(activity);
	}
	
	/**
	 * Used to share.
	 */
	public static void startActivitySendString(Activity activity, String chooserText, String extraText){
	    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, extraText);
        activity.startActivity(Intent.createChooser(sharingIntent, chooserText));
	}

    public static void startActivityWebLink(@NonNull Activity activity, @NonNull String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        activity.startActivity(browserIntent);
    }

    public static void startActivityWebLink(@NonNull Activity activity, @StringRes int link){
        startActivityWebLink(activity, activity.getString(link));
    }

    public static void startMarketActivity(@NonNull Activity activity) throws android.content.ActivityNotFoundException {
        String uri = activity.getString(R.string.market_store_uri, activity.getString(R.string.id_market));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        activity.startActivity(intent);
    }

    public static void addStandardTransition(@NonNull Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Shows server overload page
     */
    public static void showOverLoadErrorActivity(@NonNull Activity activity){
        Intent intent = new Intent(activity.getApplicationContext(), OverLoadErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Shows server overload page
     */
    public static void showRedirectInfoActivity(@NonNull Activity activity, @NonNull RedirectInfo redirect) {
        Intent intent = new Intent(activity.getApplicationContext(), RedirectInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(ConstantsIntentExtra.DATA, redirect);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}