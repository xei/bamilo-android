package com.bamilo.android.appmodule.bamiloapp.utils.pushwoosh;

import android.content.Context;
import android.content.SharedPreferences;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;

/**
 * Created by shahrooz on 4/16/17.
 */

public class PushWooshCounter {
    private static final String PurchaseCount_PREFERENCES="purchase_prefs" ;
    private static final String AppOpenCount_PREFERENCES="AppOpen_prefs" ;
    private static int purchaseCountValue;
    private static int appOpenCountValue;

    public static int getPurchaseCount()
    {
        SharedPreferences sharedPreferences = BamiloApplication.INSTANCE.getBaseContext().getSharedPreferences(PurchaseCount_PREFERENCES, Context.MODE_PRIVATE);
        purchaseCountValue = sharedPreferences.getInt("purchaseCountValue",0);
        return purchaseCountValue;
    }

    public static int getAppOpenCount() {
        SharedPreferences sharedPreferences = BamiloApplication.INSTANCE.getBaseContext().getSharedPreferences(AppOpenCount_PREFERENCES, Context.MODE_PRIVATE);
        appOpenCountValue = sharedPreferences.getInt("appOpenCountValue",0);
        return appOpenCountValue;
    }

    public static void increaseAppOpenCount() {
        SharedPreferences sharedPreferences = BamiloApplication.INSTANCE.getBaseContext().getSharedPreferences(AppOpenCount_PREFERENCES, Context.MODE_PRIVATE);
        appOpenCountValue = sharedPreferences.getInt("appOpenCountValue",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("appOpenCountValue", appOpenCountValue+1);
        editor.apply();
    }

    public static void increasePurchseCount() {
        SharedPreferences sharedPreferences = BamiloApplication.INSTANCE.getBaseContext().getSharedPreferences(PurchaseCount_PREFERENCES, Context.MODE_PRIVATE);
        purchaseCountValue = sharedPreferences.getInt("purchaseCountValue",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("purchaseCountValue", purchaseCountValue+1);
        editor.apply();
    }
}
