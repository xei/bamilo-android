package com.bamilo.android.appmodule.modernbamilo.util.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * This helper file is responsible for handling SharedPreferences operations entire the application.
 *
 * Created by Hamidreza Hosseinkhani on May 22, 2018 at Bamilo.
 */

private const val NAME_SHARED_PREFERENCES_SESSION = "persistent_cookies"

private const val KEY_PREFERENCE_COOKIE = "cookie..bamilo.com"

private const val KEY_TRACK_HOMEPAGE_PURCHASE = "trackHomepagePurchase"
private const val KEY_HOMEPAGE_TEASER_PURCHASE_CATEGORY = "teaserPurchaseCategory"
private const val KEY_HOMEPAGE_TEASER_PURCHASE_LABEL = "teaserPurchaseLabel"
private const val KEY_HOMEPAGE_GA_CAN_BE_TRACK = "keyHomepageGaCanBeTrack"

private fun getSessionSharedPreferences(context: Context) = context.getSharedPreferences(NAME_SHARED_PREFERENCES_SESSION, Context.MODE_PRIVATE)
fun getCookie(context: Context): String? = getSessionSharedPreferences(context).getString(KEY_PREFERENCE_COOKIE, "")
fun setHomePageItemsPurchaseTrack(context: Context,
                                  teaserPurchaseCategory: String?,
                                  label: String?,
                                  trackPurchase: Boolean) {
    getTrackHomepagePurchaseSharedPrefEditor(context)
            .putString(KEY_HOMEPAGE_TEASER_PURCHASE_CATEGORY, teaserPurchaseCategory)
            .putString(KEY_HOMEPAGE_TEASER_PURCHASE_LABEL, label)
            .putBoolean(KEY_HOMEPAGE_GA_CAN_BE_TRACK, trackPurchase)
            .apply()
}

fun getHomePageItemsPurchaseTrackCategory(context: Context): String? =
        getTrackHomepagePurchaseSharedPref(context).getString(KEY_HOMEPAGE_TEASER_PURCHASE_CATEGORY, "")

fun getHomePageItemsPurchaseTrackLabel(context: Context): String? =
        getTrackHomepagePurchaseSharedPref(context).getString(KEY_HOMEPAGE_TEASER_PURCHASE_LABEL, "")

fun setHomePageItemsPurchaseCanBeTrack(context: Context, canBeTrack: Boolean) =
        getTrackHomepagePurchaseSharedPrefEditor(context).putBoolean(KEY_HOMEPAGE_GA_CAN_BE_TRACK, canBeTrack).apply()

fun isHomePageItemsPurchaseCanBeTrack(context: Context): Boolean =
        getTrackHomepagePurchaseSharedPref(context).getBoolean(KEY_HOMEPAGE_GA_CAN_BE_TRACK, false)

private fun getTrackHomepagePurchaseSharedPref(context: Context): SharedPreferences =
        context.getSharedPreferences(KEY_TRACK_HOMEPAGE_PURCHASE, Context.MODE_PRIVATE)

private fun getTrackHomepagePurchaseSharedPrefEditor(context: Context): SharedPreferences.Editor =
        getTrackHomepagePurchaseSharedPref(context).edit()

fun clearTrackHomepagePurchase(context: Context) =
        getTrackHomepagePurchaseSharedPrefEditor(context).clear().apply()