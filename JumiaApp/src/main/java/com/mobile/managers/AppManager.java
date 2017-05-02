package com.mobile.managers;

import com.mobile.view.BuildConfig;

/**
 * Created by Narbeh M. on 4/29/17.
 */

public final class AppManager {
    public static String getAppVersionNumber() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getAppBuildNumber() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    public static String getAppFullFormattedVersion() {
        return getAppVersionNumber() + " (" + getAppBuildNumber() + ")";
    }
}
