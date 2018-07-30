package com.bamilo.modernbamilo.util.storage

import android.content.Context


/**
 * This helper file is responsible for handling SharedPreferences operations entire the application.
 *
 * Created by Hamidreza Hosseinkhani on May 22, 2018 at Bamilo.
 */
private const val NAME_SHARED_PREFERENCES_SESSION = "persistent_cookies"

private const val KEY_PREFERENCE_COOKIE = "cookie..bamilo.com"


private fun getSessionSharedPreferences(context: Context)
        = context.getSharedPreferences(NAME_SHARED_PREFERENCES_SESSION, Context.MODE_PRIVATE)

fun getCookie(context: Context): String = getSessionSharedPreferences(context).getString(KEY_PREFERENCE_COOKIE, "")